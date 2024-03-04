package com.falling.utils;

import com.badlogic.ashley.core.ComponentMapper;
import com.falling.components.*;

public class Mappers {
    public static ComponentMapper<TransformComponent> transformMapper;
    public static ComponentMapper<TextureRegionComponent> texRegionMapper;
    public static ComponentMapper<AnimationComponent> animationMapper;
    public static ComponentMapper<TextComponent> textMapper;
    public static ComponentMapper<RenderableComponent> renderableMapper;
    public static ComponentMapper<CursorComponent> cursorMapper;
    public static ComponentMapper<PixmapComponent> pixmapMapper;
    public static ComponentMapper<ResizeComponent> resizeMapper;
    public static ComponentMapper<TypeComponent> typeMapper;
    public static ComponentMapper<PauseComponent> pauseMapper;

    public static void init() {
        transformMapper = ComponentMapper.getFor(TransformComponent.class);
        texRegionMapper = ComponentMapper.getFor(TextureRegionComponent.class);
        animationMapper = ComponentMapper.getFor(AnimationComponent.class);
        textMapper = ComponentMapper.getFor(TextComponent.class);
        renderableMapper = ComponentMapper.getFor(RenderableComponent.class);
        cursorMapper = ComponentMapper.getFor(CursorComponent.class);
        pixmapMapper = ComponentMapper.getFor(PixmapComponent.class);
        resizeMapper = ComponentMapper.getFor(ResizeComponent.class);
        typeMapper = ComponentMapper.getFor(TypeComponent.class);
        pauseMapper = ComponentMapper.getFor(PauseComponent.class);
    }
}
