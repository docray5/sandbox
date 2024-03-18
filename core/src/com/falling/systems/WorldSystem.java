package com.falling.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.falling.components.ParticleComponent;
import com.falling.components.PixmapComponent;
import com.falling.components.TextureRegionComponent;
import com.falling.components.ParticleComponent.ParticleType;
import com.falling.events.Message;
import com.falling.events.MessageProcessor;
import com.falling.factories.Director;

import static com.falling.utils.Mappers.*;
import static com.falling.Core.*;
import static com.falling.components.ParticleComponent.ParticleType.*;
import static com.falling.components.ParticleComponent.MatterType.*;

public class WorldSystem extends EntitySystem {
    private Entity[][] world;
    private int worldSW;
    private int worldSH;
    private int xOffsetI;
    private int yOffsetI;
    private boolean update;

    private final TextureRegionComponent textureRegionComponent;
    private final PixmapComponent pixmapComponent;
    private Entity entityTmp;
    private ParticleComponent particleComponent;
    private int xTmp;
    private int yTmp;
    private int xTargetTmp;
    private int yTargetTmp;
    private boolean columnDir;

    private ParticleType typeToSpawn;
    private final MessageProcessor processor;

    public WorldSystem(int priority) {
		super(priority);

        worldSW = (int) worldWidth;
        worldSH = (int) worldHeight;
        xOffsetI = (int) xGutOffset;
        yOffsetI = (int) yGutOffset;

        world = new Entity[worldSH][worldSW];

        world[worldSH/2][worldSW/2] = Director.instance.createSand();
        world[worldSH/2-1][worldSW/2] = Director.instance.createSand();

        Entity entity = Director.instance.createPixmap();
        pixmapComponent = pixmapMapper.get(entity);
        textureRegionComponent = texRegionMapper.get(entity);

        update = true;

        // Move to spawner system
        typeToSpawn = SAND;
        processor = new MessageProcessor() {
            @Override
            public void processMessage(Message message) {
                switch (message.getEvent()) {
                    case SPAWN_PARTICLE:
                        drawCircleAtMouse(11);
                        update = true;
                        break;
                    case KEY_DOWN:
                        if (selSandPressed) typeToSpawn = SAND;
                        else if (selWaterPressed) typeToSpawn = WATER;
                        else if (selWoodPressed) typeToSpawn = WOOD;
                        else if (selErasePressed) typeToSpawn = null;
                        break;
					default:
						break;
                }
            };
        };
    }

    @Override
    public void update(float deltaTime) {
        processor.update();

        if (!update) return;
        update = false;

        // === Update ===
        for (int iy = 0; iy < worldSH; iy++) {
            columnDir = MathUtils.randomBoolean();
            for (int ix = worldSW-1; ix >= 0; ix--) {
                xTmp = ix;
                if (columnDir) xTmp = -ix + worldSW-1;
                if (world[iy][xTmp] == null) continue;
                yTmp = iy;
                entityTmp = world[yTmp][xTmp];
                particleComponent = particleMapper.get(entityTmp);

                switch (particleComponent.particleType) {
                    case SAND:
                        updateSand();
                        break;
                    case WATER:
                        updateWater();
                        break;
                }
            }
        }

        // === Draw ===
        
        pixmapComponent.pixmap.setColor(Color.CLEAR);
        pixmapComponent.pixmap.fill();
        
        for (int iy = 0; iy < worldSH; iy++) {
            for (int ix = 0; ix < worldSW; ix++) {
                if (world[iy][ix] == null) continue;
                pixmapComponent.pixmap.drawPixel(ix, iy, particleMapper.get(world[iy][ix]).colorBits);
            }
        }

        pixmapComponent.texture.draw(pixmapComponent.pixmap, 0, 0);
        textureRegionComponent.textureRegion.setRegion(pixmapComponent.texture);
        textureRegionComponent.textureRegion.flip(false, true);
    }

    public void updateWater() {
        if (tryMove(0, -1)) return;
        if (tryMove(-1, -1)) return;
        if (tryMove(1, -1)) return;
        if (tryMove(1, 0)) return;
        if (tryMove(-1, 0)) return;
    }

    public void updateSand() {
        if (tryMove(0, -1)) return;
        if (tryMove(-1, -1)) return;
        if (tryMove(1, -1)) return;
    }

    public boolean tryMove(int dx, int dy) {
        xTargetTmp = xTmp + dx;
        yTargetTmp = yTmp + dy;

        if (particleComponent.matterType == SOLID) {
            if (yTargetTmp < 0 || xTargetTmp < 0 || xTargetTmp > worldSW-1 ||
                    world[yTargetTmp][xTargetTmp] != null &&
                    particleMapper.get(world[yTargetTmp][xTargetTmp]).matterType == SOLID)
                return false;
        } else if (particleComponent.matterType == FLUID) {
            if (yTargetTmp < 0 || xTargetTmp < 0 || xTargetTmp > worldSW-1 ||
                    world[yTargetTmp][xTargetTmp] != null)
                return false;

        }

        world[yTmp][xTmp] = world[yTargetTmp][xTargetTmp];
        world[yTargetTmp][xTargetTmp] = entityTmp;

        update = true;
        updateBlur = true;

        return true;
    }

    public void dispose() {
        pixmapComponent.pixmap.dispose();
        pixmapComponent.texture.dispose();
    }

    public void drawSquareAtMouse(int size) {
        int posX = (int) mousePos.x - size/2;
        int posY = (int) mousePos.y - size/2;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (MathUtils.random() < 0.8f) continue;
                if (posY+i < 0 || posY+i > worldSH-1 || posX+j < 0 || posX+j > worldSW-1) continue;
                world[posY + i][posX + j] = spawn(posX-i, posY-j);
            }
        }
    }

    public void drawCircleAtMouse(int size) {
        int posX = (int) ( mousePos.x + lrGutter );
        int posY = (int) ( mousePos.y + tbGutter );

        int r = size/2;
        int a = posX+r;
        int b = posY+r;

        int x;
        int y;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (MathUtils.random() < 0.8f) continue;

                x = posX+i;
                y = posY+j;

                if ((x-a) * (x-a) + (y-b) * (y-b) > r*r) continue; 
                if (y-r < 0 || y-r > worldSH-1 || x-r < 0 || x-r > worldSW-1) continue;
                if (world[y-r][x-r] != null && typeToSpawn != null) continue;
                world[y-r][x-r] = spawn(x-r, y-r);
            }
        }
    }

    public Entity spawn(int px, int py) {
        if (typeToSpawn == SAND) return Director.instance.createSand();
        else if (typeToSpawn == WATER) return Director.instance.createWater();
        else if (typeToSpawn == WOOD) return Director.instance.createWood();
        else if (typeToSpawn == null) {
            if (world[py][px] != null) getEngine().removeEntity(world[py][px]);
            return null;
        }

        return Director.instance.createSand();
    }

    public void resize() {
        worldSW = (int) worldWidth;
        worldSH = (int) worldHeight;
        xOffsetI = (int) xGutOffset;
        yOffsetI = (int) yGutOffset;


        pixmapComponent.pixmap.dispose();
        pixmapComponent.texture.dispose();
        pixmapComponent.pixmap = null;
        pixmapComponent.texture = null;
        pixmapComponent.pixmap = new Pixmap(worldSW, worldSH, Pixmap.Format.RGBA8888);
        pixmapComponent.texture = new Texture(worldSW, worldSH, Pixmap.Format.RGBA8888);

        resizeWorld();

        update = true;
    }

    public void resizeWorld() {
        Entity[][] newWorld = new Entity[worldSH][worldSW];

        for (int iy = 0; iy < world.length; iy++) {
            for (int ix = 0; ix < world[0].length; ix++) {
                if (world[iy][ix] == null || iy+yOffsetI < 0 || iy+yOffsetI > worldSH-1 || ix+xOffsetI < 0 || ix + xOffsetI > worldSW-1) continue;
                newWorld[iy+yOffsetI][ix+xOffsetI] = world[iy][ix];
            }
        }

        world = newWorld;
    }
}
