package com.falling.utils;

import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.utils.Array;
import com.falling.components.*;

public class Families {
    public static Family renderableFamily;
    public static Family inputFamily;
    public static Family animationFamily;
    public static Family textFamily;
    public static Family cursorFamily;
    public static Family resizeFamily;
    public static Family pauseFamily;
    public static Family worldFamily;

    public static Array<Family> families;

    public static void init() {
        renderableFamily = Family.all(TransformComponent.class, RenderableComponent.class).one(TextureRegionComponent.class, TextComponent.class).get();
        inputFamily = Family.all().get();
        animationFamily = Family.all(AnimationComponent.class, TransformComponent.class).get();
        textFamily = Family.all(RenderableComponent.class, TransformComponent.class, TextComponent.class, AnimationComponent.class).get();
        cursorFamily = Family.all(CursorComponent.class).get();
        resizeFamily = Family.all(ResizeComponent.class).get();
        pauseFamily = Family.all(PauseComponent.class).get();
        worldFamily = Family.all().get();

        families = new Array<>();
        families.addAll(
                renderableFamily, animationFamily,
                textFamily, cursorFamily, pauseFamily,
                resizeFamily, worldFamily
        );
    }
}
