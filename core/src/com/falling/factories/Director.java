package com.falling.factories;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.falling.assets.Assets;
import com.falling.components.ResizeComponent;
import com.falling.components.TypeComponent;
import com.falling.components.ResizeComponent.STICK_TYPE;
import com.falling.events.Event;

import static com.falling.components.ParticleComponent.*;
import static com.falling.Core.*;
import static com.falling.components.ParticleComponent.ParticleType.*;

public class Director {
    public static Director instance;
    public static void setInstance(Director _instance) { instance = _instance; }

    private final Factory factory;

    private Color colorTmp = new Color();

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

    public void createSpawnArea() {
        factory.createEntity()
        .addTransform(0, 0)
                .addClickable(0, 0, worldWidth, worldHeight)
                .addResize(ResizeComponent.STICK_TYPE.LEFT_BOTTOM)
                .addButton(null, Event.SPAWN_PARTICLE_DOWN, Event.SPAWN_PARTICLE_UP, null, null)
                .addType(TypeComponent.Type.FULLSCREEN_CLICK)
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
                .addTransform(0, 0)
                .addRenderable()
                    .setPriority(20)
                .endComponent()
                .addTextureRegion()
                .addResize(STICK_TYPE.LEFT_BOTTOM)
                .addPixmap()
                .endEntity();
    }

    public Entity createElement(ParticleType type) {
         if (type == SAND) return createSand();
        else if (type == WATER) return createWater();
        else if (type == WOOD) return createWood();

        return createSand();
    }

    public Entity createSand() {
        // colorTmp.set(colorTmp.fromHsv(sandColor[0], sandColor[1] + MathUtils.random(0.2f)-0.2f, sandColor[2] + MathUtils.random(0.15f)-0.15f));

        switch (MathUtils.random(4)) {
            case 0:
                colorTmp.set(0.96f, 0.84f, 0.69f, 1);
                break;
            case 1:
                colorTmp.set(0.95f, 0.82f, 0.66f, 1);
                break;
            case 2:
                colorTmp.set(0.93f, 0.8f, 0.64f, 1);
                break;
            case 3:
                colorTmp.set(0.91f, 0.77f, 0.59f, 1);
                break;
            case 4:
                colorTmp.set(0.88f, 0.75f, 0.57f, 1);
                break;
        }

        return factory.createEntity()
            .addParticle(Color.rgba8888(colorTmp), ParticleType.SAND, MatterType.SOLID)
            .endEntity();
    }

    public Entity createWater() {
            return factory.createEntity()
                .addParticle(Color.rgba8888(Color.SKY), ParticleType.WATER, MatterType.FLUID)
                .endEntity();
    }

    public Entity createWood() {
            return factory.createEntity()
                .addParticle(Color.rgba8888(Color.BROWN), ParticleType.WOOD, MatterType.SOLID)
                .endEntity();
    }
}
