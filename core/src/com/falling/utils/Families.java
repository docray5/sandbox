package com.falling.utils;

import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.utils.Array;
import com.falling.components.*;

public class Families {
    public static Family renderableFamily;
    public static Family buttonFamily;
    public static Family inputFamily;
    public static Family animationFamily;
    public static Family textFamily;
    public static Family cursorFamily;
    public static Family resizeFamily;
    public static Family pauseFamily;
    public static Family uiTranslateFamily;
    public static Family worldFamily;

    public static Array<Family> families;

    public static void init() {
        renderableFamily = Family.all(TransformComponent.class, RenderableComponent.class).one(TextureRegionComponent.class, TextComponent.class).get();
        inputFamily = Family.all(ClickableComponent.class).get();
        buttonFamily = Family.all(ButtonComponent.class, ClickableComponent.class, TransformComponent.class).get();
        animationFamily = Family.all(AnimationComponent.class, TransformComponent.class).get();
        textFamily = Family.all(RenderableComponent.class, TransformComponent.class, TextComponent.class, AnimationComponent.class).get();
        cursorFamily = Family.all(CursorComponent.class).get();
        resizeFamily = Family.all(ResizeComponent.class).get();
        pauseFamily = Family.all(PauseComponent.class).get();
        uiTranslateFamily = Family.all(UITranslateComponent.class).get();
        worldFamily = Family.all().get();

        families = new Array<>();
        families.addAll(
                renderableFamily, animationFamily, buttonFamily,
                textFamily, cursorFamily, pauseFamily,
                resizeFamily, inputFamily, uiTranslateFamily, worldFamily
        );
    }
}
