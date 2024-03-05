package com.falling.factories;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.falling.assets.Assets;
import com.falling.components.*;
import com.falling.components.ElementComponent.ElementType;
import com.falling.components.ElementComponent.MatterType;
import com.falling.events.Event;

import static com.falling.Core.*;
import static com.falling.utils.Families.*;

public class Director {
    public static Director instance;
    public static void setInstance(Director _instance) { instance = _instance; }

    private final Factory factory;

    public Director(Engine engine, Assets assets) {
        this.factory = new Factory(engine, assets);
    }
    // ================================ Main Menu ================================

    public void createTitle() {
        factory.createEntity()
                .addTransform(worldWidth/2f, worldHeight/2f)
                .addRenderable()
                .setPriority(4)
                .setCenter(true)
                .endComponent()
                .addTextureRegion("title")
                .addAnimation()
                .setPos(worldWidth/2f, worldHeight/2f)
                .setRounding(10)
                .endComponent()
                .endEntity();
    }

    public void createStartText() {
        factory.createEntity()
                .addTransform(worldWidth/2f, 60)
                .addRenderable()
                    .setPriority(4)
                    .setCenter(true)
                .endComponent()
                .addTextureRegion("start")
                .addAnimation()
                .setPos(worldWidth/2f, 60)
                .setRounding(10)
                .endComponent()
                .endEntity();
    }

    public Entity createPixmap() {
        return factory.createEntity()
                .addTransform(worldWidth/2, worldHeight/2)
                .addRenderable()
                    .setPriority(20)
                    .setCenter(true)
                .endComponent()
                .addTextureRegion()
                .addPixmap()
                .endEntity();
    }

    public Entity createSand() {
        return factory.createEntity()
            .addElement(Color.rgba8888(0.96f, 0.84f, 0.69f, 1), ElementType.SAND, MatterType.SOLID)
            .endEntity();
    }
}
