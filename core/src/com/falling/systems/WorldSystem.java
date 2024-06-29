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
import com.falling.components.ElementComponent.ElementType;
import com.falling.events.Event;
import com.falling.events.EventHandler;
import com.falling.factories.Director;

import static com.falling.utils.Mappers.*;
import static com.falling.Core.*;
import static com.falling.components.ElementComponent.ElementType.*;
import static com.falling.components.ElementComponent.MatterType.*;

public class WorldSystem extends EntitySystem {
    private EventHandler eventHandler;

    private Entity[][] world;
    private int worldSW;
    private int worldSH;
    private int xOffsetI;
    private int yOffsetI;
    private boolean update;
    private boolean render;
    private boolean pause;
    private int lastMX;
    private int lastMY;

    private final TextureRegionComponent textureRegionComponent;
    private final PixmapComponent pixmapComponent;
    private Entity entityTmp;
    private ElementComponent elementComponent;
    private int xTmp;
    private int yTmp;
    private int xTargetTmp;
    private int yTargetTmp;
    private boolean columnDir;
    private int updateCount;
    private boolean moved;

    private float velocityTmp;
    private float modTmp;
    private int flooredTmp;
    private float absTmp;

    private ElementType typeToSpawn;
    private BrushType brushType;
    private boolean spawnElement;

    public WorldSystem(int priority) {
		super(priority);

        eventHandler = new EventHandler(this.getClass()) {
            @Override
            public void processEvent(Event event) {
            }
        };

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
        render = true;

        // Move to spawner system
        typeToSpawn = SAND;
        brushType = BrushType.CIRCLE;
    }

    @Override
    public void update(float deltaTime) {
        eventHandler.handleEvents();

        if (spawnElement) {
            draw();
            if (!pause) update = true;
            updateBlur = true;
        }

        updateWorld();

        render();

        if (pause) update = false;
    }

    private void updateWorld() {
        if (!update) return;
        update = false;

        for (int iy = 0; iy < worldSH; iy++) {
            columnDir = MathUtils.randomBoolean();
            for (int ix = worldSW-1; ix >= 0; ix--) {
                // setup vars
                xTmp = ix;
                if (columnDir) xTmp = -ix + worldSW-1;
                if (world[iy][xTmp] == null) continue;
                yTmp = iy;
                entityTmp = world[yTmp][xTmp];
                elementComponent = elementMapper.get(entityTmp);

                updateElement();
            }
        }
    }

    private void render() {
        if (!render) return;
        render = false;

        pixmapComponent.pixmap.setColor(Color.CLEAR);
        pixmapComponent.pixmap.fill();
        
        for (int iy = 0; iy < worldSH; iy++) {
            for (int ix = 0; ix < worldSW; ix++) {
                if (world[iy][ix] == null) continue;
                pixmapComponent.pixmap.drawPixel(ix, iy, elementMapper.get(world[iy][ix]).colorBits);
            }
        }

        pixmapComponent.texture.draw(pixmapComponent.pixmap, 0, 0);
        textureRegionComponent.textureRegion.setRegion(pixmapComponent.texture);
        textureRegionComponent.textureRegion.flip(false, true);
    }

    private void updateElement() {
        if (elementComponent.matterType == SOLID) return;

        updateVelocity();

        switch (elementComponent.elementType) {
            case SAND:
                updateSand();
                break;
            case WATER:
                updateWater();
                break;
        }
    }

    private void updateWater() {
        updateCount = getUpdateCount();
        for (int i = 0; i < updateCount; i++) {
            if (moveFluid(0, -1)) continue;
            if (moveFluid(-1, -1)) continue;
            if (moveFluid(1, -1)) continue;
            if (trySpread(1)) continue;
            if (trySpread(-1)) continue;
            elementComponent.velocity = 0; // when no movement, no velocity
            break;
        }
    }

    private boolean trySpread(int dx) {
        moved = false;
        updateCount = elementComponent.spread - MathUtils.random(2);
        for (int i = 0; i < updateCount; i++) {
            if (!moveFluid(dx, 0)) break;
            moved = true;
        }
        return moved;
    }


    private boolean moveFluid(int dx, int dy) {
        xTargetTmp = xTmp + dx;
        yTargetTmp = yTmp + dy;

        if (yTargetTmp < 0 || xTargetTmp < 0 || xTargetTmp > worldSW-1 ||
                world[yTargetTmp][xTargetTmp] != null)
            return false;

        swap();

        return true;
    }

    private void updateSand() {
        updateCount = getUpdateCount();
        for (int i = 0; i < updateCount; i++) {
            if (movePowder(0, -1)) continue;
            if (movePowder(-1, -1)) continue;
            if (movePowder(1, -1)) continue;
            elementComponent.velocity = 0; // when no movement, no velocity
            break;
        }
    }

    private boolean movePowder(int dx, int dy) {
        xTargetTmp = xTmp + dx;
        yTargetTmp = yTmp + dy;

        if (yTargetTmp < 0 || xTargetTmp < 0 || xTargetTmp > worldSW-1 ||
                world[yTargetTmp][xTargetTmp] != null &&
                (elementMapper.get(world[yTargetTmp][xTargetTmp]).matterType == POWDER ||
                elementMapper.get(world[yTargetTmp][xTargetTmp]).matterType == SOLID))
            return false;

        swap();

        return true;
    }

    private void swap() {
        world[yTmp][xTmp] = world[yTargetTmp][xTargetTmp];
        world[yTargetTmp][xTargetTmp] = entityTmp;

        // update positon and screen
        yTmp = yTargetTmp;
        xTmp = xTargetTmp;

        update = true;
        updateBlur = true;
        render = true;
    }

    private int getUpdateCount() {
        absTmp = Math.abs(elementComponent.velocity);
        flooredTmp = MathUtils.floor(absTmp);
        modTmp = absTmp - flooredTmp;
        return flooredTmp + (MathUtils.random() < modTmp ? 1 : 0);
    }

    private void updateVelocity() {
        velocityTmp = elementComponent.velocity + elementComponent.acceleration;

        if (Math.abs(velocityTmp) > elementComponent.maxSpeed) 
            velocityTmp = Math.signum(velocityTmp) * elementComponent.maxSpeed;

        elementComponent.velocity = velocityTmp;
    }

    private void draw() {
        int posX = (int) (mousePos.x + lrGutter);
        int posY = (int) (mousePos.y + tbGutter);
    
        if (posX == lastMX && posY == lastMY) {
            brush(posX, posY);
            return;
        }

        int xDiff = posX - lastMX;
        int yDiff = posY - lastMY;
        boolean xDiffIsLarger = Math.abs(xDiff) > Math.abs(yDiff);

        int xModifier = xDiff < 0 ? 1 : -1;
        int yModifier = yDiff < 0 ? 1 : -1;

        int longerSideLength = Math.max(Math.abs(xDiff), Math.abs(yDiff));
        int shorterSideLength = Math.min(Math.abs(xDiff), Math.abs(yDiff));
        float slope = (shorterSideLength == 0 || longerSideLength == 0) ? 0 : ((float) (shorterSideLength) / (longerSideLength));

        int shorterSideIncrease, currentY, currentX, yIncrease, xIncrease;
        for (int i = 1; i <= longerSideLength; i++) {
            shorterSideIncrease = Math.round(i * slope);
            if (xDiffIsLarger) {
                xIncrease = i;
                yIncrease = shorterSideIncrease;
            } else {
                yIncrease = i;
                xIncrease = shorterSideIncrease;
            }
            currentY = posY + (yIncrease * yModifier);
            currentX = posX + (xIncrease * xModifier);

            brush(currentX, currentY);
        }

        lastMX = posX;
        lastMY = posY;
    }

    private void brush(int posX, int posY) {
        boolean randomize = false;
        
        if (typeToSpawn == null || typeToSpawn == WOOD) randomize = false;

        switch (brushType) {
            case CIRCLE:
                drawCircleAtPos(posX, posY, 3, randomize);
                break;
            case PIXEL:
                drawPixel(posX, posY);
                break;
            case SQUARE:
                drawSquareAtPos(posX, posY, 11, randomize);
                break;
            default:
                break;
        }
    }

    private void drawSquareAtPos(int posX, int posY, int size, boolean randomize) {
        posX -= size/2;
        posY -= size/2;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (randomize && MathUtils.random() < 0.8f) continue;
                drawPixel(posX+j, posY+i);
            }
        }
    }

    private void drawCircleAtPos(int posX, int posY, int size, boolean randomize) {
        int r = size/2;
        int a = posX+r;
        int b = posY+r;

        int x;
        int y;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (randomize && MathUtils.random() < 0.8f) continue;

                x = posX+i;
                y = posY+j;

                if ((x-a) * (x-a) + (y-b) * (y-b) > r*r) continue;
                x-=r;
                y-=r;
                drawPixel(x, y);
            }
        }
    }

    private void drawPixel(int x, int y) {
        if (isOutsideBounds(x, y)) return;
        if (typeToSpawn == null) {
            if (world[y][x] != null) getEngine().removeEntity(world[y][x]);
            world[y][x] = null;
            render = true;
            return;
        }
        if (world[y][x] != null) return;
        world[y][x] = Director.instance.createElement(typeToSpawn);
        render = true;
    }

    private boolean isOutsideBounds(int x, int y) {
        return y < 0 || y > worldSH-1 || x < 0 || x > worldSW-1;
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
        render = true;
    }

    private void resizeWorld() {
        Entity[][] newWorld = new Entity[worldSH][worldSW];

        for (int iy = 0; iy < world.length; iy++) {
            for (int ix = 0; ix < world[0].length; ix++) {
                if (world[iy][ix] == null || iy+yOffsetI < 0 || iy+yOffsetI > worldSH-1 || ix+xOffsetI < 0 || ix + xOffsetI > worldSW-1) continue;
                newWorld[iy+yOffsetI][ix+xOffsetI] = world[iy][ix];
            }
        }

        world = newWorld;
    }

    public void dispose() {
        pixmapComponent.pixmap.dispose();
        pixmapComponent.texture.dispose();
    }

    public void spawnElementDown() {
        lastMX = (int) (mousePos.x + lrGutter);
        lastMY = (int) (mousePos.y + tbGutter);
        spawnElement = true;
    }

    public void spawnElementUp() {
        spawnElement = false;
    }

    public void selectElement(ElementType type) {
        typeToSpawn = type;
    }

    public void selectBrush(BrushType type) {
        brushType = type;
    }

    public void pause() {
        pause = !pause;
        if (!update) { 
            update = true;
            render = true;
        }
    }

    public enum BrushType {
        CIRCLE, SQUARE, PIXEL
    }
}
