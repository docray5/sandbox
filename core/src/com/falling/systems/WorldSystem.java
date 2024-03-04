package com.falling.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.falling.components.PixmapComponent;
import com.falling.components.TextureRegionComponent;
import com.falling.factories.Director;

import static com.falling.utils.Mappers.*;
import static com.falling.Core.*;

public class WorldSystem extends EntitySystem {
    private final TextureRegionComponent textureRegionComponent;
    private final PixmapComponent pixmapComponent;
    private int posX;
    private int posY;
    private Color colorTmp;
    private Vector2 posTmp;

    public WorldSystem(int priority) {
		super(priority);
        Entity entity = Director.instance.createPixmap();
        pixmapComponent = pixmapMapper.get(entity);
        textureRegionComponent = texRegionMapper.get(entity);
    }

    @Override
    public void update(float deltaTime) {
        pixmapComponent.pixmap.setColor(Color.CLEAR);
        pixmapComponent.pixmap.fill();
        
        // go through all the elements.
        pixmapComponent.pixmap.drawPixel((int) worldWidth/2, (int) worldHeight/2, Color.rgba8888(Color.WHITE));

        pixmapComponent.texture.draw(pixmapComponent.pixmap, 0, 0);
        textureRegionComponent.textureRegion.setRegion(pixmapComponent.texture);
        textureRegionComponent.textureRegion.flip(false, true);
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
}
