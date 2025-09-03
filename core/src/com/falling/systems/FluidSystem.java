package com.falling.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.falling.components.TransformComp;
import com.falling.factories.Director;
import com.falling.utils.Mappers;

import static com.falling.Core.*;

public class FluidSystem extends EntitySystem {

    private final float particleSize = 5; // width of texture Note: particles are drawn centered already around their vector's pos
    private final float particleSpacing = 0.2f;
    private final float scale = 0.1f; // can't be more than 0.1f for best sims (well actually maybe it could)
    private final float fixedDeltaTime = 0.1f;

    private final int numOfParticles = 1458;
    private final float mass = 1f;
    private float targetDensity = 3;
    private float pressureMultiplier = 40; // Stiffness constant works best at 20-50
    private float gravity = 0; // 0.5f is quite optimal, wouldn't go beyond 1
    private final float collisionDamping = 0.9f;
    private final Vector2 bounds = new Vector2(worldWidth*scale, worldHeight*scale);
    private final int gridWidth = (int) bounds.x;
    private final int gridHeight = (int) bounds.y;

    private Entity[] particles;
    private Vector2[] positions;
    private Vector2[] predictedPositions;
    private Vector2[] velocities;
    private float[] densities;

    private final float smoothingRadius = 1f;

    private final float cellSize = smoothingRadius;
    private final Array<Integer>[][] grid; // Stores indexes of particles from all the positions, velocities etc. lists

    // temp variables for better mem management
    private TransformComp transformTmp;
    private Array<Integer> neighborsTmp;
    private float boundsRXTmp;
    private float boundsTYTmp;
    private float boundsLXTmp;
    private float boundsBYTmp;
    private Vector2 pressureForceTmp = new Vector2();
    private float influenceTmp;
    private float densityTmp;
    private Vector2 randomDirTmp = new Vector2();
    private float volumeTmp;
    private float scaleTmp;
    private Vector2 dirTmp = new Vector2();
    private float sharedPressureTmp;
    private float dstTmp;
    private float slopeTmp;

    public FluidSystem(int priority) {
        super(priority);

        positions = new Vector2[numOfParticles];
        predictedPositions = new Vector2[numOfParticles];
        velocities = new Vector2[numOfParticles];
        densities = new float[numOfParticles];
        particles = new Entity[numOfParticles];

        grid = new Array[gridWidth][gridHeight];
        for(int x=0;x<gridWidth;x++)
            for(int y=0;y<gridHeight;y++)
                grid[x][y] = new Array<>();

        createParticlesRect(1, 1);
    }

    @Override
    public void update(float deltaTime) {
        deltaTime = fixedDeltaTime;
        updateBlur = true;

        for (int i = numOfParticles-1; i >=0; i--) {
            velocities[i].y -= gravity * deltaTime;
            predictedPositions[i].x = positions[i].x + velocities[i].x * deltaTime;
            predictedPositions[i].y = positions[i].y + velocities[i].y * deltaTime;
        }

        updateSpatialGrid();

        updateDensities();

        for (int i = numOfParticles-1; i >=0; i--) {
            pressureForceTmp = calculatePressureForce(i);
            velocities[i].add(pressureForceTmp.x * deltaTime / densities[i], pressureForceTmp.y * deltaTime / densities[i]);
        }

        for (int i = numOfParticles-1; i >=0; i--) {
            positions[i].x += velocities[i].x * deltaTime;
            positions[i].y += velocities[i].y * deltaTime;
            resolveCollisions(i);

            syncForDrawing(i);
        }
    }

    private float SmoothingKernel(float dst, float radius) {
        if (dst >= radius) return 0;
        volumeTmp = (MathUtils.PI * (float) Math.pow(radius, 4)) / 6;
        return (radius - dst) * (radius - dst) / volumeTmp;
    }

    private float SmoothingKernelDerivative(float dst, float radius) {
        if (dst >= radius) return 0;
        scaleTmp = 12 / (MathUtils.PI * (float) Math.pow(radius, 4));
        return (dst - radius) * scaleTmp;
    }

    private float calculateDensity(Vector2 samplePoint) {
        densityTmp = 0;

        for (int i : getNeighbors(samplePoint)) {
            influenceTmp = SmoothingKernel(predictedPositions[i].dst(samplePoint), smoothingRadius);
            densityTmp += mass * influenceTmp;
        }

        return densityTmp;
    }
    
    // Calculate Property Gradient
    private Vector2 calculatePressureForce(int particleIndex) {
        pressureForceTmp.set(0, 0);

        for (int i : getNeighbors(positions[particleIndex])) {
            if (particleIndex == i) continue;

            dstTmp = positions[i].dst(positions[particleIndex]);

            if (dstTmp == 0) dirTmp = getRandomDir();
            else dirTmp.set((positions[i].x-positions[particleIndex].x)/dstTmp, (positions[i].y - positions[particleIndex].y)/dstTmp);

            slopeTmp = SmoothingKernelDerivative(dstTmp, smoothingRadius);
            sharedPressureTmp = calculateSharedPressure(densities[i], densities[particleIndex]);
            pressureForceTmp.add(sharedPressureTmp * dirTmp.x * slopeTmp * mass / densities[i], sharedPressureTmp * dirTmp.y * slopeTmp * mass / densities[i]);
        }

        return pressureForceTmp;
    }

    private void updateDensities() {
        // Could be run in a different thread.
        for (int i = numOfParticles-1; i >= 0; i--)
            densities[i] = calculateDensity(predictedPositions[i]);
    }

    private void resolveCollisions(int particleIndex) {
        boundsRXTmp = bounds.x - particleSize*scale;
        boundsTYTmp = bounds.y - particleSize*scale;
        boundsLXTmp = 0+particleSize*scale;
        boundsBYTmp = 0+particleSize*scale;

        if (positions[particleIndex].x > boundsRXTmp) {
            positions[particleIndex].x = boundsRXTmp;
            velocities[particleIndex].x *= -collisionDamping;
        } else if (positions[particleIndex].x < boundsLXTmp) {
            positions[particleIndex].x = boundsLXTmp;
            velocities[particleIndex].x *= -collisionDamping;
        }
        if (positions[particleIndex].y > boundsTYTmp) {
            positions[particleIndex].y = boundsTYTmp;
            velocities[particleIndex].y *= -collisionDamping;
        } else if (positions[particleIndex].y < boundsBYTmp) {
            positions[particleIndex].y = boundsBYTmp;
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

    private float calculateSharedPressure(float densityA, float densityB) {
        return (convertDensityToPressure(densityA) + convertDensityToPressure(densityB)) /2;
    }

    private Vector2 getRandomDir() {
        randomDirTmp.set(MathUtils.random(-1, 1), MathUtils.random(-1, 1));
        return randomDirTmp;
    }

    private float convertDensityToPressure(float density) {
        return (density - targetDensity) * pressureMultiplier;
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
                Entity entTmp = Director.instance.createFluidParticle(new Vector2(spawnX, spawnY));
                particles[index] = entTmp;
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
                Entity entTmp = Director.instance.createFluidParticle(new Vector2(spawnX, spawnY));
                particles[index] = entTmp;
                positions[index] = new Vector2(new Vector2(spawnX, spawnY));
                predictedPositions[index] = new Vector2();
                velocities[index] = new Vector2();
                index+=1;
            }
        }
    }

    private void syncForDrawing(int i) {
        transformTmp = Mappers.transformMapper.get(particles[i]);
        transformTmp.pos.set(positions[i].x*(1/scale), positions[i].y*(1/scale));
    }

    public void updateSpatialGrid() {
        for(int x=0; x<gridWidth; x++)
            for(int y=0; y<gridHeight; y++)
                grid[x][y].clear();

        for (int i = numOfParticles-1; i >=0; i--) {
            int gridX = (int) Math.floor(positions[i].x / cellSize);
            int gridY = (int) Math.floor(positions[i].y / cellSize);
            grid[gridX][gridY].add(i);
        }
    }

    public Array<Integer> getNeighbors(Vector2 samplePoint) {
        neighborsTmp = new Array<>();

        int centerX = (int) Math.floor(samplePoint.x / cellSize);
        int centerY = (int) Math.floor(samplePoint.y / cellSize);

        // Check 3×3 neighborhood
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int x = centerX + dx;
                int y = centerY + dy;

                if (x >= 0 && x < gridWidth && y >= 0 && y < gridHeight) {
                    neighborsTmp.addAll(grid[x][y]);
                }
            }
        }
        return neighborsTmp;
    }

    // TODO:
    // Well storing 1000x texture regions is not really effective, so I would rather just use instancing (for now not)
    // remove all these variable creations

    // NI-- what?

    // Alternative smoothing functions
    //    private float SmoothingKernel(float dst, float radius) {
//        float volume = MathUtils.PI * (float) Math.pow(radius, 8)/4;
//        float value = Math.max(0, radius*radius - dst*dst);
//        return value * value * value / volume;
//    }
//
//    private float SmoothingKernelDerivative(float dst, float radius) {
//        if (dst >= radius) return 0;
//        float f = radius * radius - dst * dst;
//        float scale = -24 / (MathUtils.PI * (float) Math.pow(radius, 8));
//        return scale * dst * f * f;
//    }
}