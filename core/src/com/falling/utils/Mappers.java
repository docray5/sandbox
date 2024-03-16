package com.falling.utils;

import com.badlogic.ashley.core.ComponentMapper;
import com.falling.components.*;

public class Mappers {
    public static ComponentMapper<TransformComponent> transformMapper;
    public static ComponentMapper<TextureRegionComponent> texRegionMapper;
    public static ComponentMapper<ClickableComponent> clickableMapper;
    public static ComponentMapper<ButtonComponent> buttonMapper;
    public static ComponentMapper<AnimationComponent> animationMapper;
    public static ComponentMapper<TextComponent> textMapper;
    public static ComponentMapper<RenderableComponent> renderableMapper;
    public static ComponentMapper<CursorComponent> cursorMapper;
    public static ComponentMapper<PixmapComponent> pixmapMapper;
    public static ComponentMapper<ResizeComponent> resizeMapper;
    public static ComponentMapper<TypeComponent> typeMapper;
    public static ComponentMapper<PauseComponent> pauseMapper;
    public static ComponentMapper<ParticleComponent> particleMapper;
    public static ComponentMapper<UITranslateComponent> uiTranslateMapper;

    public static void init() {
        transformMapper = ComponentMapper.getFor(TransformComponent.class);
        texRegionMapper = ComponentMapper.getFor(TextureRegionComponent.class);
        animationMapper = ComponentMapper.getFor(AnimationComponent.class);
        clickableMapper = ComponentMapper.getFor(ClickableComponent.class);
        textMapper = ComponentMapper.getFor(TextComponent.class);
        renderableMapper = ComponentMapper.getFor(RenderableComponent.class);
        cursorMapper = ComponentMapper.getFor(CursorComponent.class);
        pixmapMapper = ComponentMapper.getFor(PixmapComponent.class);
        resizeMapper = ComponentMapper.getFor(ResizeComponent.class);
        typeMapper = ComponentMapper.getFor(TypeComponent.class);
        pauseMapper = ComponentMapper.getFor(PauseComponent.class);
        particleMapper = ComponentMapper.getFor(ParticleComponent.class);
        buttonMapper = ComponentMapper.getFor(ButtonComponent.class);
        uiTranslateMapper = ComponentMapper.getFor(UITranslateComponent.class);
    }
}
