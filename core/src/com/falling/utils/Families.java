package com.falling.utils;

import com.badlogic.ashley.core.Family;
import com.falling.components.*;

public class Families {
    public static Family renderableFamily;
    public static Family clickableFamily;
    public static Family animationFamily;
    public static Family textFamily;
    public static Family cursorFamily;
    public static Family resizeFamily;
    public static Family uiTranslateFamily;
    public static Family worldFamily;

    public static void init() {
        renderableFamily = Family.all(TransformComponent.class, RenderableComponent.class).one(TextureRegionComponent.class, TextComponent.class, NinepatchComponent.class).get();
        clickableFamily = Family.all(ClickableComponent.class, TransformComponent.class).get();
        animationFamily = Family.all(AnimationComponent.class, TransformComponent.class).get();
        textFamily = Family.all(RenderableComponent.class, TransformComponent.class, TextComponent.class).get();
        cursorFamily = Family.all(CursorComponent.class).get();
        resizeFamily = Family.all(ResizeComponent.class).get();
        uiTranslateFamily = Family.all(UITranslateComponent.class).get();
        worldFamily = Family.all().get();
    }
}
