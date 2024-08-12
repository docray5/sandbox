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
    public static Family sceneFamily;
    public static Family worldFamily;

    public static void init() {
        renderableFamily = Family.all(TransformComp.class, RenderableComp.class).one(TextureRegionComp.class, TextComp.class, NinepatchComp.class).get();
        clickableFamily = Family.all(ClickableComp.class, TransformComp.class).get();
        animationFamily = Family.all(AnimationComp.class, TransformComp.class).get();
        textFamily = Family.all(RenderableComp.class, TransformComp.class, TextComp.class).get();
        cursorFamily = Family.all(CursorComp.class).get();
        resizeFamily = Family.all(ResizeComp.class).get();
        sceneFamily = Family.all(SceneComp.class).get();
        worldFamily = Family.all().get();
    }
}
