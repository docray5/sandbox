package com.falling.factories;

import com.badlogic.ashley.core.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.falling.assets.Assets;
import com.falling.components.*;
import com.falling.events.Event;
import com.falling.factories.builders.AnimationBuilder;
import com.falling.factories.builders.RenderableBuilder;

public class Factory {
    private final Engine engine;
    protected final Assets assets;

    private Entity entity;
    private final AnimationBuilder animationBuilder;
    private CursorComponent cursorComponent;
    private PauseComponent pauseComponent;
    private final RenderableBuilder renderableBuilder;
    private ResizeComponent resizeComponent;
    private TextComponent textComponent;
    private TextureRegionComponent regionComponent;
    private TransformComponent transformComponent;
    private TypeComponent typeComponent;
    private PixmapComponent pixmapComponent;

    private boolean creating;

    /**
     * Disclaimer: Always start with createEntity and End with endEntity()
     * Some Components have their own Builders (same as factory lol)
     */
    public Factory(Engine engine, Assets assets) {
        this.engine = engine;
        this.assets = assets;
        animationBuilder = new AnimationBuilder(engine, this);
        renderableBuilder = new RenderableBuilder(engine, this);
    }

    public Factory createEntity() {
        if (creating) return null;
        entity = engine.createEntity();
        creating = true;
        return this;
    }

    public Entity endEntity() {
        if (!creating) return null;
        engine.addEntity(entity);
        creating = false;
        return entity;
    }

    public Entity getEntity() {
        if (!creating) return null;
        return entity;
    }

    /**Adds a region component with first frame of the animation*/
    /**@return Animation builder for easy chaining haha*/
    public AnimationBuilder addAnimation() {
        if (!creating) return null;
        return animationBuilder.createAnimationComponent();
    }

    public Factory addCursor() {
        if (!creating) return null;
        cursorComponent = engine.createComponent(CursorComponent.class);

        entity.add(cursorComponent);
        return this;
    }

    public Factory addPause(Class<?extends EntitySystem> system) {
        if (!creating) return null;
        pauseComponent = engine.createComponent(PauseComponent.class);
        pauseComponent.system = system;

        entity.add(pauseComponent);
        return this;
    }

    public RenderableBuilder addRenderable() {
        if (!creating) return null;
        return renderableBuilder.createComponent();
    }

    public Factory addResize(ResizeComponent.STICK_TYPE stick) {
        if (!creating) return null;
        resizeComponent = engine.createComponent(ResizeComponent.class);
        resizeComponent.stick = stick;

        entity.add(resizeComponent);
        return this;
    }

   public Factory addText(String text) {
        if (!creating) return null;
        textComponent = engine.createComponent(TextComponent.class);
        textComponent.text = text;
        textComponent.font = assets.getFont("alagard.fnt");

        entity.add(textComponent);
        return this;
    }

    public Factory addText(String text, String fontFN) {
        if (!creating) return null;
        textComponent = engine.createComponent(TextComponent.class);
        textComponent.text = text;
        textComponent.font = assets.getFont(fontFN);

        entity.add(textComponent);
        return this;
    }

    public Factory addTextureRegion(String textureFN) {
        if (!creating) return null;
        regionComponent = engine.createComponent(TextureRegionComponent.class);
        if (regionComponent.textureRegion.getTexture() != assets.getTexture(textureFN)) {
            regionComponent.textureRegion.setRegion(assets.getTexture(textureFN));
            regionComponent.originX = regionComponent.textureRegion.getRegionWidth()/2f;
            regionComponent.originY = regionComponent.textureRegion.getRegionHeight()/2f;
        }

        entity.add(regionComponent);
        return this;
    }

    public Factory addTextureRegion(String textureFN, boolean originX, boolean originY) {
        if (!creating) return null;
        regionComponent = engine.createComponent(TextureRegionComponent.class);
        if (regionComponent.textureRegion.getTexture() != assets.getTexture(textureFN)) {
            regionComponent.textureRegion.setRegion(assets.getTexture(textureFN));
            if (originX) regionComponent.originX = regionComponent.textureRegion.getRegionWidth()/2f;
            if (originY) regionComponent.originY = regionComponent.textureRegion.getRegionHeight()/2f;
        }

        entity.add(regionComponent);
        return this;
    }

    public Factory addTextureRegion() {
        if (!creating) return null;
        regionComponent = engine.createComponent(TextureRegionComponent.class);

        entity.add(regionComponent);
        return this;
    }

    public Factory addTransform(float x, float y) {
        if (!creating) return null;
        transformComponent = engine.createComponent(TransformComponent.class);
        transformComponent.pos.set(x, y);

        entity.add(transformComponent);
        return this;
    }

    public Factory addTransform(float x, float y, float scale, float rotation) {
        if (!creating) return null;
        transformComponent = engine.createComponent(TransformComponent.class);
        transformComponent.pos.set(x, y);
        transformComponent.scale.set(scale, scale);
        transformComponent.rotation = rotation;

        entity.add(transformComponent);
        return this;
    }

    public Factory addTransform(Vector2 pos) {
        if (!creating) return null;
        transformComponent = engine.createComponent(TransformComponent.class);
        transformComponent.pos.set(pos);

        entity.add(transformComponent);
        return this;
    }

    public Factory addTransform(Vector2 pos, float scale, float rotation) {
        if (!creating) return null;
        transformComponent = engine.createComponent(TransformComponent.class);
        transformComponent.pos.set(pos);
        transformComponent.scale.set(scale, scale);
        transformComponent.rotation = rotation;

        entity.add(transformComponent);
        return this;
    }

    public Factory addType(TypeComponent.Type type) {
        if (!creating) return null;
        typeComponent = engine.createComponent(TypeComponent.class);
        typeComponent.type = type;

        entity.add(typeComponent);
        return this;
    }

    public Factory addPixmap() {
        if (!creating) return null;
        pixmapComponent = engine.createComponent(PixmapComponent.class);

        entity.add(pixmapComponent);
        return this;
    }
}
