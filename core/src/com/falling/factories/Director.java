package com.falling.factories;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.falling.assets.Assets;
import com.falling.commands.*;
import com.falling.components.ResizeComponent;
import com.falling.components.TypeComponent;
import com.falling.components.ResizeComponent.STICK_TYPE;

import static com.falling.components.ElementComponent.*;
import static com.falling.Core.*;
import static com.falling.components.ElementComponent.ElementType.*;
import static com.falling.components.VecAnimatorComponent.AnimatorType.*;

public class Director {
    public static Director instance;
    public static void setInstance(Director _instance) { if (instance==null) instance = _instance; }

    private final Factory factory;
    private final Engine engine;

    private Color colorTmp = new Color();

    public Director(Engine engine, Assets assets) {
        this.factory = new Factory(engine, assets);
        this.engine = engine;
    }
    // ================================ Main Menu ================================

    public void createTitle() {
        factory.createEntity()
                .addTransform(worldWidth/2f, worldHeight/2f)
                .addRenderable()
                .setPriority(2)
                .setCenter(true)
                .endComponent()
                .addTextureRegion("title")
                .addAnimation(factory.createAnimator(POS, 1, null, Interpolation.swingOut))
                .endEntity();
    }
    
    public void createNinePatch() {
        factory.createEntity()
            .addTransform(100, 100)
            .addResize(STICK_TYPE.RIGHT_TOP)
            .addRenderable()
                .setPriority(10)
                .setCenter(true)
            .endComponent()
            .addNinePatch("np", 14, 14, 14, 14, 40, 40)
            .addAnimation(factory.createAnimator(POS, 1, null, Interpolation.swingOut), factory.createAnimator(SCALE, 0.25f, null, Interpolation.swingOut), factory.createAnimator(SIZE, 0.25f, null, Interpolation.swingOut))
            .addClickableAuto(null, new AnimToCmd(engine, SIZE, 40-10, 40-10, false), new AnimToCmd(engine, SIZE, 40, 40, false), new AnimToCmd(engine, SIZE, 44, 44, false), new AnimToCmd(engine, SIZE, 40, 40, false), 0, 10)
            .endEntity();
    }

    public void createSpawnArea() {
        factory.createEntity()
        .addTransform(0, 0)
                .addClickable(0, 0, worldWidth, worldHeight,
                null, Commands.instance.spawnElementDown, Commands.instance.spawnElementUp,
                null, null, 2, 0)
                .addResize(ResizeComponent.STICK_TYPE.LEFT_BOTTOM)
                .addType(TypeComponent.Type.FULLSCREEN_CLICK)
                .endEntity();
    }

    public void createUIButton(float x, float y, float width, float height, String fn, STICK_TYPE stick, Command commandOnClick) {
        factory.createEntity()
            .addTransform(x-width/2, y-height/2)
            .addResize(stick)
            .addRenderable()
                .setPriority(10)
                .setCenter(true)
            .endComponent()
            .addTextureRegion(fn)
            .addAnimation(factory.createAnimator(POS, 1, null, Interpolation.swingOut), factory.createAnimator(SCALE, 0.25f, null, Interpolation.swingOut))
            .addClickableAuto(commandOnClick, new AnimToCmd(engine, SCALE, 0.7f, 0.7f, false), new AnimToCmd(engine, SCALE, 1f, 1f, false), new AnimToCmd(engine, SCALE, 1.1f, 1.1f, false), new AnimToCmd(engine, SCALE, 1f, 1f, false), 0, 10)
            .endEntity();
    }

    public void createBlurBtn() {
        createUIButton(worldWidth-4, worldHeight-4, 27, 33, "btn", STICK_TYPE.RIGHT_TOP, Commands.instance.shiftBlur);
    }

    public void createPauseBtn() {
        createUIButton(100, 100, 30, 30, "test", STICK_TYPE.LEFT_BOTTOM, Commands.instance.pauseWorld);
    }

    public void createStartText() {
        factory.createEntity()
                .addTransform(worldWidth/2f, 100)
                .addRenderable()
                    .setPriority(2)
                    .setCenter(true)
                    .setColor(new Color(1, 1, 1, 0.2f))
                .endComponent()
                .addText("1-3 - sand, water, wood\ne - eraser\nz,x,c - brushes")
                .endEntity();
    }

    public Entity createPixmap() {
        return factory.createEntity()
                .addTransform(0, 0)
                .addRenderable()
                    .setPriority(5)
                .endComponent()
                .addTextureRegion()
                .addResize(STICK_TYPE.LEFT_BOTTOM)
                .addPixmap()
                .endEntity();
    }

    public Entity createElement(ElementType type) {
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
            .addElement(Color.rgba8888(colorTmp), ElementType.SAND, MatterType.POWDER, 8, 0.2f, 0)
            .endEntity();
    }

    public Entity createWater() {
            return factory.createEntity()
                .addElement(Color.rgba8888(Color.SKY), ElementType.WATER, MatterType.FLUID, 8, 0.2f, 5)
                .endEntity();
    }

    public Entity createWood() {
            return factory.createEntity()
                .addElement(Color.rgba8888(Color.BROWN), ElementType.WOOD, MatterType.SOLID, 0, 0, 0)
                .endEntity();
    }
}
