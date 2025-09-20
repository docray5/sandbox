package com.falling.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.Sort;
import com.falling.assets.Assets;
import com.falling.components.TextureRegionComp;
import com.falling.factories.Director;

import java.util.Arrays;
import java.util.Comparator;

import static com.badlogic.gdx.Gdx.gl;
import static com.falling.Core.*;
import static com.falling.utils.Mappers.*;

public class FluidSystem extends EntitySystem {

    public boolean leftMousePressed = false;
    public boolean rightMousePressed = false;
    private final Vector2 scaledMousePos = new Vector2();

    private float particleSize = 5; // width of texture Note: particles are drawn centered already around their vector's pos
    private final float particleSpacing = 0.2f;
    private final float scale = 0.1f; // can't be more than 0.1f for best sims (well actually maybe it could)
    private final float fixedDeltaTime = 0.1f ; // before was: 0.1f / (fps/60) for a more adaptive sim | It should rather be 1/fps, but then it's extra slow

    private final int numOfParticles = 1682; // 1568
    private final float mass = 1f;
    private float targetDensity = 3;
    private float pressureMultiplier = 40; // Stiffness constant works best at 20-50
    private float gravity = 0; // 0.5f is quite optimal, wouldn't go beyond 1
    private final float collisionDamping = 0.9f;
    private final Vector2 bounds = new Vector2(worldWidth*scale, worldHeight*scale);
    private final int gridWidth = (int) bounds.x;
    private final int gridHeight = (int) bounds.y;

    private final Vector2[] positions;
    private final Vector2[] predictedPositions;
    private final Vector2[] velocities;
    private final float[] densities;

    private final float smoothingRadius = 1f;

    // Potentially change to long[][], and if you need to have variable num of particles that changes dynamically
    // use some different object like IntArray from libgdx
    /** Stores pairs: particle index and cell key (a 2D coordinate of grid cell which
     * the particle sits in (not the particle coordinate) compressed into a valid index)
     * Gets sorted by key */
    private final int[][] spatialLookup;
    /** each index is cell key which's value corresponds to index in spatial lookup which is
     *  the beginning of all particles starting from given cell key */
    private final int[] startIndices;

    // For rendering
    private final FrameBuffer fluidFbo;
    private final SpriteBatch fluidBatch;
    private final TextureRegion particleTexture;
    private final Vector2 particleTextureOffset = new Vector2();
    private final Color fluidColor = Color.SKY;

    // temp variables for better mem management
    private final Vector2 pressureForceTmp = new Vector2();
    private final Vector2 interactionForceTmp = new Vector2();
    private Vector2 dirToInputPointTmp;
    private final int[] posTmp = new int[2];

    // Compute helpers:
    private float scaleConstant;
    private float volumeConstant;
    private int[][] cellOffsets = {
            {-1,  1}, {0,  1}, {1,  1},
            {-1,  0}, {0,  0}, {1,  0},
            {-1, -1}, {0, -1}, {1, -1}
    };

    public FluidSystem(int priority, Assets assets) {
        super(priority);

        // Rendering related setup:
        Entity fluidDrawArea = Director.instance.createFluidFbo();
        fluidFbo = frameBufferMapper.get(fluidDrawArea).fbo;
        TextureRegionComp fluidTextureRegion = texRegionMapper.get(fluidDrawArea);
        fluidTextureRegion.textureRegion.setRegion(fluidFbo.getColorBufferTexture());
        fluidTextureRegion.width = (int) worldWidth;
        fluidTextureRegion.height = (int) worldHeight;
        fluidTextureRegion.textureRegion.flip(false, true);
        fluidTextureRegion.textureRegion.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Nearest);

        fluidBatch = new SpriteBatch(numOfParticles);

        particleTexture = new TextureRegion(assets.getTexture("fl"));
        particleTextureOffset.x = particleTexture.getRegionWidth()/2f;
        particleTextureOffset.y = particleTexture.getRegionHeight()/2f;

        particleSize = particleTexture.getRegionWidth(); // Since we draw squares we can automatically determine their size based off of textures

        // Simulation setup
        positions = new Vector2[numOfParticles];
        predictedPositions = new Vector2[numOfParticles];
        velocities = new Vector2[numOfParticles];
        densities = new float[numOfParticles];

        spatialLookup = new int[numOfParticles][2];
        startIndices = new int[numOfParticles];

        createParticlesRect(1, 1);
    }

    @Override
    public void update(float deltaTime) {
        deltaTime = fixedDeltaTime;
        updateBlur = true;

        precomputeKernelConstants(smoothingRadius);

        if (leftMousePressed || rightMousePressed) {
            // calculate position of mouse relative to small grid
            scaledMousePos.set(mousePos.x * scale, mousePos.y * scale);
        }

        // Can be run on a different thread (altough this does not cost much performance)
        for (int i = numOfParticles-1; i >=0; i--) {
            // Maybe this one should be below mouse presses
            velocities[i].y -= gravity * deltaTime;

            // This probably can't be run on a different thread
            if (leftMousePressed)
                velocities[i].add(interactionForce(scaledMousePos, 5f, 1f, i));
            else if (rightMousePressed)
                velocities[i].add(interactionForce(scaledMousePos, 0.5f, -0.3f, i));

            predictedPositions[i].x = positions[i].x + velocities[i].x * deltaTime;
            predictedPositions[i].y = positions[i].y + velocities[i].y * deltaTime;
        }

        updateSpatialLookup();

        // Can be run on a different thread
        updateDensities();

        // Can be run on a different thread
        for (int i = numOfParticles-1; i >=0; i--) {
            pressureForceTmp.set(calculatePressureForce(i));
            velocities[i].add(pressureForceTmp.x * deltaTime / densities[i], pressureForceTmp.y * deltaTime / densities[i]);
        }

        // Can be run on a different thread
        for (int i = numOfParticles-1; i >=0; i--) {
            positions[i].x += velocities[i].x * deltaTime;
            positions[i].y += velocities[i].y * deltaTime;
            resolveCollisions(i);
        }

        updateFbo();
    }

    private void precomputeKernelConstants(float radius) {
        // Could be (float) Math.pow(radius, 4) instead of radius * radius * radius * radius,
        // but thanks to that we don't type cast shit
        float radiusSquared = radius * radius;
        float radiusForth = radiusSquared * radiusSquared;
        volumeConstant = (MathUtils.PI * radiusForth) / 6;
        scaleConstant = 12 / (MathUtils.PI * radiusForth);
    }

    private float SmoothingKernel(float dst, float radius) {
        if (dst >= radius) return 0;
        return (radius - dst) * (radius - dst) / volumeConstant;
    }

    private float SmoothingKernelDerivative(float dst, float radius) {
        if (dst >= radius) return 0;
        return (dst - radius) * scaleConstant;
    }

    private float calculateDensity(Vector2 samplePoint) {
        float density = 0;


        int[] center = posToCellCords(samplePoint, smoothingRadius);
        float sqrRadius = smoothingRadius * smoothingRadius;

        // System.out.println("\nCalculating neighbors of: " + samplePoint);
        for (int offIndex = 0; offIndex < cellOffsets.length; offIndex++) {
            int key = getKeyFromHash(hashCell(center[0]+cellOffsets[offIndex][0], center[1]+cellOffsets[offIndex][1]));
            int cellStartIndex = startIndices[key];

            for (int i = cellStartIndex; i < numOfParticles; i++) {
                if (spatialLookup[i][1] != key) break;

                int particleIndex = spatialLookup[i][0];
                float sqrDst = (predictedPositions[particleIndex].x - samplePoint.x) * (predictedPositions[particleIndex].x - samplePoint.x) + (predictedPositions[particleIndex].y - samplePoint.y) * (predictedPositions[particleIndex].y - samplePoint.y);

                if (sqrDst <= sqrRadius) {
                    // System.out.println(particleIndex);
                    float influence = SmoothingKernel(predictedPositions[particleIndex].dst(samplePoint), smoothingRadius);
                    density += mass * influence;
                }
            }
        }

        return density;
    }

    // Calculate Property Gradient
    private Vector2 calculatePressureForce(int particleIndex) { // Causes most of CPU usage
        pressureForceTmp.set(0, 0);

        int[] center = posToCellCords(predictedPositions[particleIndex], smoothingRadius);
        float sqrRadius = smoothingRadius * smoothingRadius;

        for (int offIndex = 0; offIndex < cellOffsets.length; offIndex++) {
            int key = getKeyFromHash(hashCell(center[0]+cellOffsets[offIndex][0], center[1]+cellOffsets[offIndex][1]));
            int cellStartIndex = startIndices[key];

            for (int i = cellStartIndex; i < numOfParticles; i++) {
                if (spatialLookup[i][1] != key) break;

                int index = spatialLookup[i][0];
                float sqrDst = (predictedPositions[index].x - predictedPositions[particleIndex].x) * (predictedPositions[index].x - predictedPositions[particleIndex].x) + (predictedPositions[index].y - predictedPositions[particleIndex].y) * (predictedPositions[index].y - predictedPositions[particleIndex].y);

                if (sqrDst <= sqrRadius) {
                    if (particleIndex == index) continue;

                    float dst = predictedPositions[index].dst(predictedPositions[particleIndex]);
                    float dirX;
                    float dirY;

                    if (dst == 0) {
                        dirX = MathUtils.random(-1, 1);
                        dirY = MathUtils.random(-1, 1);
                    } else {
                        dirX = (predictedPositions[index].x - predictedPositions[particleIndex].x) / dst;
                        dirY = (predictedPositions[index].y - predictedPositions[particleIndex].y) / dst;
                    }

                    float slope = SmoothingKernelDerivative(dst, smoothingRadius);
                    float sharedPressure = ((densities[index] - targetDensity) * pressureMultiplier + (densities[particleIndex] - targetDensity) * pressureMultiplier) / 2;
                    // float densityInv = 1f / densities[index];
                    pressureForceTmp.add(sharedPressure * dirX * slope * mass / densities[index], sharedPressure * dirY * slope * mass / densities[index]);
                }
            }
        }

        return pressureForceTmp;
    }

    private void updateDensities() {
        // Could be run in a different thread.
        for (int i = numOfParticles-1; i >= 0; i--)
            densities[i] = calculateDensity(predictedPositions[i]);
    }

    private void resolveCollisions(int particleIndex) {
        float boundsRX = bounds.x - particleSize*scale;
        float boundsTY = bounds.y - particleSize*scale;
        float boundsLX = 0+particleSize*scale;
        float boundsBY = 0+particleSize*scale;

        if (positions[particleIndex].x > boundsRX) {
            positions[particleIndex].x = boundsRX;
            velocities[particleIndex].x *= -collisionDamping;
        } else if (positions[particleIndex].x < boundsLX) {
            positions[particleIndex].x = boundsLX;
            velocities[particleIndex].x *= -collisionDamping;
        }
        if (positions[particleIndex].y > boundsTY) {
            positions[particleIndex].y = boundsTY;
            velocities[particleIndex].y *= -collisionDamping;
        } else if (positions[particleIndex].y < boundsBY) {
            positions[particleIndex].y = boundsBY;
            velocities[particleIndex].y *= -collisionDamping;
        }
    }

    public void leftFluid() {
        gravity+=0.5f;
        System.out.println(gravity);
    }

    public void rightFluid() {
        pressureMultiplier+=1;
        System.out.println(pressureMultiplier);
    }

    private void createParticlesSquare(int x, int y) {
        int amount = (int) Math.sqrt(numOfParticles);
        int index = 0;
        float spawnX;
        float spawnY;

        for (int i = 0; i<amount; i++) {
            for (int j = 0; j<amount; j++) {
                spawnX = i*(particleSize*scale+particleSpacing)+x;
                spawnY = j*(particleSize*scale+particleSpacing)+y;
                positions[index] = new Vector2(new Vector2(spawnX, spawnY));
                predictedPositions[index] = new Vector2();
                velocities[index] = new Vector2();
                index+=1;
            }
        }
    }

    private void createParticlesRect(int x, int y) {
        int amountY = (int) Math.sqrt(numOfParticles/2f);
        int amountX = amountY*2;
        System.out.println(amountX);
        System.out.println(amountY);
        int index = 0;
        float spawnX;
        float spawnY;

        for (int i = 0; i<amountX; i++) {
            for (int j = 0; j<amountY; j++) {
                spawnX = i*(particleSize*scale+particleSpacing)+x;
                spawnY = j*(particleSize*scale+particleSpacing)+y;
                positions[index] = new Vector2(spawnX, spawnY);
                predictedPositions[index] = new Vector2();
                velocities[index] = new Vector2();
                index+=1;
            }
        }
    }

    private Vector2 interactionForce(Vector2 inputPos, float radius, float strength, int particleIndex) {
        interactionForceTmp.setZero();
        float offsetX = inputPos.x - positions[particleIndex].x;
        float offsetY = inputPos.y - positions[particleIndex].y;
        float sqrDst = Vector2.dot(offsetX, offsetY, offsetX, offsetY);

        if (sqrDst < radius*radius) {
            float inputPointDst = (float) Math.sqrt(sqrDst);
            dirToInputPointTmp = inputPointDst <= 0.001f ? Vector2.Zero : new Vector2(offsetX/inputPointDst, offsetY/inputPointDst);
            float centreT = 1 - inputPointDst / radius;
            interactionForceTmp.add((dirToInputPointTmp.x * strength - velocities[particleIndex].x) * centreT, (dirToInputPointTmp.y * strength - velocities[particleIndex].y) * centreT);
        }

        return interactionForceTmp;
    }

    public void gravity() {
        if (gravity == 0) gravity = 0.5f;
        else gravity = 0;
    }

    private static final Comparator<int[]> comp = Comparator.comparingInt(a -> a[1]);

    private void updateSpatialLookup() {
        // Can be run on a different thread
        for (int i = 0; i < numOfParticles; i++) {
            int cellKey = getKeyFromHash(hashCell(posToCellCords(predictedPositions[i], smoothingRadius)));
            spatialLookup[i][0] = i;
            spatialLookup[i][1] = cellKey;
            startIndices[i] = Integer.MAX_VALUE;
        }

        Sort.instance().sort(spatialLookup, comp);

        // Can be run on a different thread
        for (int i = 0; i < numOfParticles; i++) {
            int key = spatialLookup[i][1];
            int previousKey;

            if (i == 0) previousKey = Integer.MAX_VALUE;
            else previousKey = spatialLookup[i - 1][1];

            if (key != previousKey)
                startIndices[key] = i;
        }
    }

    public void getNeighbors(Vector2 point) {
        int[] center = posToCellCords(point, smoothingRadius);
        float sqrRadius = smoothingRadius * smoothingRadius;

        for (int offIndex = 0; offIndex < cellOffsets.length; offIndex++) {
            int key = getKeyFromHash(hashCell(center[0]+cellOffsets[offIndex][0], center[1]+cellOffsets[offIndex][1]));
            int cellStartIndex = startIndices[key];

            for (int i = cellStartIndex; i < numOfParticles; i++) {
                if (spatialLookup[i][1] != key) break;

                int particleIndex = spatialLookup[i][0];
                float sqrDst = (positions[particleIndex].x - point.x) * (positions[particleIndex].x - point.x) + (positions[particleIndex].y - point.y) * (positions[particleIndex].y - point.y);

                if (sqrDst <= sqrRadius) {
                    // Do something
                }
            }
        }
    }

    private int[] posToCellCords(Vector2 point, float radius) {
        posTmp[0] = (int) (point.x / radius);
        posTmp[1] = (int) (point.y / radius);
        return posTmp;
    }

    // some time soon change to long if too many particles
    private int hashCell(int cellX, int cellY) {
        return (cellX * 15823) + (cellY * 9737333);
    }

    private int hashCell(int[] cellPos) {
        return cellPos[0] * 15823 + cellPos[1] * 9737333;
    }

    private int getKeyFromHash(int hash) {
        return Math.floorMod(hash, numOfParticles);
    }

    public void resize() {
        // TBD
    }

    private void updateFbo() {
        fluidBatch.setProjectionMatrix(cameraMatrixTemp);
        fluidFbo.begin();
        fluidBatch.begin();
        gl.glClearColor(0f, 0f, 0f, 0f);
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Primer for drawing XD
        fluidBatch.draw(particleTexture, -30, -30);

        // fluidBatch.setColor(fluidColor);
        // draw all the particles
        for (int i = positions.length-1; i >= 0; i--) {
            fluidBatch.setColor(0.529f * ((Math.abs(velocities[i].x) + Math.abs(velocities[i].y))*4), 0.808f, 0.921f, 1f); // Debug view
            fluidBatch.draw(particleTexture, positions[i].x*(1/scale) - particleTextureOffset.x, positions[i].y*(1/scale) - particleTextureOffset.y);
        }

        fluidFbo.end();
        fluidBatch.end();
    }

    public void dispose() {
        fluidFbo.dispose();
        fluidBatch.dispose();
    }
}