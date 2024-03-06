package com.falling.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.falling.components.ElementComponent;
import com.falling.components.PixmapComponent;
import com.falling.components.TextureRegionComponent;
import com.falling.components.ElementComponent.MatterType;
import com.falling.factories.Director;

import static com.falling.utils.Mappers.*;
import static com.falling.Core.*;

public class WorldSystem extends EntitySystem {
    private Entity[][] world;
    private int worldSW;
    private int worldSH;

    private final TextureRegionComponent textureRegionComponent;
    private final PixmapComponent pixmapComponent;
    private Entity entityTmp;
    private ElementComponent elementComponent;
    private int xTmp;
    private int yTmp;
    private int xTargetTmp;
    private int yTargetTmp;

    public WorldSystem(int priority) {
		super(priority);

        worldSW = (int) worldWidth;
        worldSH = (int) worldHeight;

        world = new Entity[worldSH][worldSW];

        world[worldSH/2][worldSW/2] = Director.instance.createSand();
        world[worldSH/2-1][worldSW/2] = Director.instance.createSand();

        Entity entity = Director.instance.createPixmap();
        pixmapComponent = pixmapMapper.get(entity);
        textureRegionComponent = texRegionMapper.get(entity);
    }

    @Override
    public void update(float deltaTime) {
        // Move to spawner system
        if (touchHeld) drawCircleAtMouse(11);

        // === Update ===
        for (int iy = 0; iy < worldSH; iy++) {
            for (int ix = worldSW-1; ix >= 0; ix--) {
                if (world[iy][ix] == null) continue;
                entityTmp = world[iy][ix];
                elementComponent = elementMapper.get(entityTmp);
                xTmp = ix;
                yTmp = iy;

                switch (elementComponent.elementType) {
                    case SAND:
                        processSand();
                        break;
                }
            }
        }

        // === Draw ===
        pixmapComponent.pixmap.setColor(Color.CLEAR);
        pixmapComponent.pixmap.fill();
        
        for (int iy = 0; iy < world.length; iy++) {
            for (int ix = 0; ix < world.length; ix++) {
                if (world[iy][ix] == null) continue;
                pixmapComponent.pixmap.drawPixel(ix, iy, elementMapper.get(world[iy][ix]).colorBits);
            }
        }

        pixmapComponent.texture.draw(pixmapComponent.pixmap, 0, 0);
        textureRegionComponent.textureRegion.setRegion(pixmapComponent.texture);
        textureRegionComponent.textureRegion.flip(false, true);
    }

    public void processSand() {
        if (tryMove(0, -1)) return;
        if (tryMove(-1, -1)) return;
        if (tryMove(1, -1)) return;
    }

    public boolean tryMove(int dx, int dy) {
        xTargetTmp = xTmp + dx;
        yTargetTmp = yTmp + dy;

        if (yTargetTmp < 0 || xTargetTmp < 0 || xTargetTmp > worldSW-1 ||
                world[yTargetTmp][xTargetTmp] != null &&
                elementMapper.get(world[yTargetTmp][xTargetTmp]).matterType == MatterType.SOLID)
            return false;

        world[yTmp][xTmp] = world[yTargetTmp][xTargetTmp];
        world[yTargetTmp][xTargetTmp] = entityTmp;
        
        return true;
    }

    public void resize() {
        pixmapComponent.pixmap.dispose();
        pixmapComponent.texture.dispose();
        pixmapComponent.pixmap = null;
        pixmapComponent.texture = null;
        pixmapComponent.pixmap = new Pixmap((int) worldWidth, (int) worldHeight, Pixmap.Format.RGBA8888);
        pixmapComponent.texture = new Texture((int) worldWidth, (int) worldHeight, Pixmap.Format.RGBA8888);
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
                world[posY + i][posX + j] = Director.instance.createSand();
            }
        }
    }

    public void drawCircleAtMouse(int size) {
        int posX = (int) mousePos.x;
        int posY = (int) mousePos.y;
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

                if (y < 0 || y > worldSH-1 || x < 0 || x > worldSW-1) continue;
                if ((x-a) * (x-a) + (y-b) * (y-b) > r*r) continue; 
                world[y-r][x-r] = Director.instance.createSand();
            }
        }
    }
}
