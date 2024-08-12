package com.falling.utils;

import com.badlogic.ashley.core.ComponentMapper;
import com.falling.components.*;

public class Mappers {
    public static ComponentMapper<TransformComp> transformMapper;
    public static ComponentMapper<TextureRegionComp> texRegionMapper;
    public static ComponentMapper<ClickableComp> clickableMapper;
    public static ComponentMapper<AnimationComp> animationMapper;
    public static ComponentMapper<TextComp> textMapper;
    public static ComponentMapper<RenderableComp> renderableMapper;
    public static ComponentMapper<CursorComp> cursorMapper;
    public static ComponentMapper<PixmapComp> pixmapMapper;
    public static ComponentMapper<ResizeComp> resizeMapper;
    public static ComponentMapper<TypeComp> typeMapper;
    public static ComponentMapper<ElementComp> elementMapper;
    public static ComponentMapper<SceneComp> sceneMapper;
    public static ComponentMapper<NinepatchComp> ninepatchMapper;

    public static void init() {
        transformMapper = ComponentMapper.getFor(TransformComp.class);
        texRegionMapper = ComponentMapper.getFor(TextureRegionComp.class);
        animationMapper = ComponentMapper.getFor(AnimationComp.class);
        clickableMapper = ComponentMapper.getFor(ClickableComp.class);
        textMapper = ComponentMapper.getFor(TextComp.class);
        renderableMapper = ComponentMapper.getFor(RenderableComp.class);
        cursorMapper = ComponentMapper.getFor(CursorComp.class);
        pixmapMapper = ComponentMapper.getFor(PixmapComp.class);
        resizeMapper = ComponentMapper.getFor(ResizeComp.class);
        typeMapper = ComponentMapper.getFor(TypeComp.class);
        elementMapper = ComponentMapper.getFor(ElementComp.class);
        sceneMapper = ComponentMapper.getFor(SceneComp.class);
        ninepatchMapper = ComponentMapper.getFor(NinepatchComp.class);
    }
}
