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

//    private int[] startIndices;
//    private Entry[] spatialLookup;

    private final float smoothingRadius = 1f;
    private Vector2 point = new Vector2(35, 35);

    private final float cellSize = smoothingRadius;
    private final Array<Integer>[][] grid; // Stores indexes of particles from all the positions, velocities etc. lists

    // temp variables for better mem management
    private TransformComp transformTmp;

    public FluidSystem(int priority) {
        super(priority);

        positions = new Vector2[numOfParticles];
        predictedPositions = new Vector2[numOfParticles];
        velocities = new Vector2[numOfParticles];
        densities = new float[numOfParticles];
        particles = new Entity[numOfParticles];

//        startIndices = new int[numOfParticles];
//        spatialLookup = new Entry[numOfParticles];

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

        for (int i = numOfParticles-1; i >=0; i--)
            densities[i] = calculateDensity(predictedPositions[i]);

        for (int i = numOfParticles-1; i >=0; i--) {
            Vector2 pressureForce = calculatePressureForce(i);
            velocities[i].add(pressureForce.x * deltaTime / densities[i], pressureForce.y * deltaTime / densities[i]);
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
        float volume = (MathUtils.PI * (float) Math.pow(radius, 4)) / 6;
        return (radius - dst) * (radius - dst) / volume;
    }

    private float SmoothingKernelDerivative(float dst, float radius) {
        if (dst >= radius) return 0;
        float scale = 12 / (MathUtils.PI * (float) Math.pow(radius, 4));
        return (dst - radius) * scale;
    }

    private float calculateDensity(Vector2 samplePoint) {
        float density = 0;

        // TODO optimize to only go through particles inside the smoothing radius
//        for (int i = numOfParticles-1; i >=0; i--) {
//            float influence = SmoothingKernel(predictedPositions[i].dst(samplePoint), smoothingRadius);
//            density += mass * influence;
//        }

        for (int i : getNeighbors(samplePoint)) {
            float influence = SmoothingKernel(predictedPositions[i].dst(samplePoint), smoothingRadius);
            density += mass * influence;
        }

        return density;
    }
    
    // Calculate Property Gradient
    private Vector2 calculatePressureForce(int particleIndex) {
        Vector2 pressureForce = new Vector2();

        // TODO optimize to only go through particles inside the smoothing radius
//        for (int i = numOfParticles-1; i >=0; i--) {
//            if (particleIndex == i) continue;
//
//            float dst = positions[i].dst(positions[particleIndex]);
//            Vector2 dir = dst == 0 ? getRandomDir() : new Vector2((positions[i].x-positions[particleIndex].x)/dst, (positions[i].y - positions[particleIndex].y)/dst);
//            float slope = SmoothingKernelDerivative(dst, smoothingRadius);
//            float sharedPressure = calculateSharedPressure(densities[i], densities[particleIndex]);
//            pressureForce.add(sharedPressure * dir.x * slope * mass / densities[i], sharedPressure * dir.y * slope * mass / densities[i]);
//        }

        for (int i : getNeighbors(positions[particleIndex])) {
            if (particleIndex == i) continue;

            float dst = positions[i].dst(positions[particleIndex]);
            Vector2 dir = dst == 0 ? getRandomDir() : new Vector2((positions[i].x-positions[particleIndex].x)/dst, (positions[i].y - positions[particleIndex].y)/dst);
            float slope = SmoothingKernelDerivative(dst, smoothingRadius);
            float sharedPressure = calculateSharedPressure(densities[i], densities[particleIndex]);
            pressureForce.add(sharedPressure * dir.x * slope * mass / densities[i], sharedPressure * dir.y * slope * mass / densities[i]);
        }

        return pressureForce;
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
        //point.x -= 1;
        gravity+=0.5f;
        System.out.println(gravity);
    }

    public void rightFluid() {
        //point.x += 1;
        pressureMultiplier+=1;
        System.out.println(pressureMultiplier);
    }

    private float calculateSharedPressure(float densityA, float densityB) {
        return (convertDensityToPressure(densityA) + convertDensityToPressure(densityB)) /2;
    }

    private Vector2 getRandomDir() {
        return new Vector2(MathUtils.random(-1, 1), MathUtils.random(-1, 1));
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
        Array<Integer> neighbors = new Array<>();

        int centerX = (int) Math.floor(samplePoint.x / cellSize);
        int centerY = (int) Math.floor(samplePoint.y / cellSize);

        // Check 3×3 neighborhood
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int x = centerX + dx;
                int y = centerY + dy;

                if (x >= 0 && x < gridWidth && y >= 0 && y < gridHeight) {
                    neighbors.addAll(grid[x][y]);
                }
            }
        }
        return neighbors;
    }

//    public long hashCell(int cellX, int cellY) {
//        return (long) cellX * 15823 + (long) cellY * 9737333;
//    }
//
//    public long getKeyFromHash(long hash) {
//        return hash % (long) spatialLookup.length;
//    }
//
//    public int[] positionToCellCords(Vector2 point, float radius) {
//        return new int[]{(int) (point.y / radius), (int) (point.x / radius)};
//    }
//
//    public class SortByCellKey implements Comparator<Entry> {
//
//        @Override
//        public int compare(Entry o1, Entry o2) {
//            return Math.toIntExact(o1.cellKey - o2.cellKey);
//        }
//    }
//
//    public void updateSpatialLookup(Vector2[] points, float radius) {
//        this.positions = points;
//        this.smoothingRadius = radius;
//
//        for (int i = numOfParticles-1; i >=0; i--) {
//            int[] cellPos = positionToCellCords(positions[i], radius);
//
//            long cellKey = getKeyFromHash(hashCell(cellPos[0], cellPos[1]));
//            spatialLookup[i] = new Entry(i, cellKey);
//            startIndices[i] = Integer.MAX_VALUE;
//        }
//
//        Arrays.sort(spatialLookup, new SortByCellKey());
//
//        for (int i = 0; i < numOfParticles; i++) {
//            long key = spatialLookup[i].cellKey;
//            long keyPrev = i == 0 ? Long.MAX_VALUE : spatialLookup[i-1].cellKey;
//            if (key != keyPrev) {
//                startIndices[Math.toIntExact(key)] = i;
//            }
//        }
//    }

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

//    public class Entry {
//        protected int i;
//        protected long cellKey;
//
//        public Entry(int i, long cellKey) {
//            this.i = i;
//            this.cellKey = cellKey;
//        }
//    }
}