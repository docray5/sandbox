package com.falling.factories;

import com.badlogic.ashley.core.*;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.falling.assets.Assets;
import com.falling.commands.Command;
import com.falling.components.*;
import com.falling.components.ElementComponent.ElementType;
import com.falling.components.ElementComponent.MatterType;
import com.falling.components.VecAnimatorComponent.AnimatorType;
import com.falling.factories.builders.RenderableBuilder;

public class Factory {
    private final Engine engine;
    protected final Assets assets;

    private Entity entity;
    private VecAnimatorComponent animatorComp;
    private AnimationComponent animationComp;
    private CursorComponent cursorComp;
    private final RenderableBuilder renderableBuilder;
    private ResizeComponent resizeComp;
    private TextComponent textComp;
    private TextureRegionComponent regionComp;
    private TransformComponent transformComp;
    private TypeComponent typeComp;
    private PixmapComponent pixmapComp;
    private ElementComponent elementComp;
    private ClickableComponent clickableComp;

    private boolean creating;

    /**
     * Disclaimer: Always start with createEntity and End with endEntity()
     * Some Components have their own Builders (same as factory lol)
     */
    public Factory(Engine engine, Assets assets) {
        this.engine = engine;
        this.assets = assets;
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

    public Factory addAnimation(VecAnimatorComponent... animatorComps) {
        if (!creating) return null;
        animationComp = engine.createComponent(AnimationComponent.class);

        for (int i = 0; i < animatorComps.length; i++) {
            animationComp.animators.add(animatorComps[i]);
        }

        entity.add(animationComp);
        return this;
    }

    /** Put After setting transform */
    public VecAnimatorComponent createPosAnimator(float duration, Command commandOnFinish, Interpolation interpolation) {
        return createPosAnimator(duration, commandOnFinish, interpolation, false);
    }

    /** Put After setting transform */
    public VecAnimatorComponent createScaleAnimator(float duration, Command commandOnFinish, Interpolation interpolation) {
        return createScaleAnimator(duration, commandOnFinish, interpolation, false);
    }

    /** Put After setting transform */
    public VecAnimatorComponent createPosAnimator(float duration, Command commandOnFinish, Interpolation interpolation, boolean cmdOnEveryFinish) {
        animatorComp = engine.createComponent(VecAnimatorComponent.class);
        animatorComp.animatedVecPointer = transformComp.pos;
        animatorComp.type = AnimatorType.POS;
        animatorComp.duration = duration;
        animatorComp.commandOnFinish = commandOnFinish;
        animatorComp.interpolation = interpolation;
        animatorComp.cmdOnEveryFinish = cmdOnEveryFinish;
        return animatorComp;
    }

    /** Put After setting transform */
    public VecAnimatorComponent createScaleAnimator(float duration, Command commandOnFinish, Interpolation interpolation, boolean cmdOnEveryFinish) {
        animatorComp = engine.createComponent(VecAnimatorComponent.class);
        animatorComp.animatedVecPointer = transformComp.scale;
        animatorComp.type = AnimatorType.SCALE;
        animatorComp.duration = duration;
        animatorComp.commandOnFinish = commandOnFinish;
        animatorComp.interpolation = interpolation;
        animatorComp.cmdOnEveryFinish = cmdOnEveryFinish;
        return animatorComp;
    }

    public Factory addCursor() {
        if (!creating) return null;
        cursorComp = engine.createComponent(CursorComponent.class);

        entity.add(cursorComp);
        return this;
    }

    public RenderableBuilder addRenderable() {
        if (!creating) return null;
        return renderableBuilder.createComponent();
    }

    public Factory addResize(ResizeComponent.STICK_TYPE stick) {
        if (!creating) return null;
        resizeComp = engine.createComponent(ResizeComponent.class);
        resizeComp.stick = stick;

        entity.add(resizeComp);
        return this;
    }

   public Factory addText(String text) {
        if (!creating) return null;
        textComp = engine.createComponent(TextComponent.class);
        textComp.text = text;
        textComp.font = assets.getFont("alagard.fnt");

        entity.add(textComp);
        return this;
    }

    public Factory addText(String text, String fontFN) {
        if (!creating) return null;
        textComp = engine.createComponent(TextComponent.class);
        textComp.text = text;
        textComp.font = assets.getFont(fontFN);

        entity.add(textComp);
        return this;
    }

    public Factory addTextureRegion(String textureFN) {
        if (!creating) return null;
        regionComp = engine.createComponent(TextureRegionComponent.class);
        if (regionComp.textureRegion.getTexture() != assets.getTexture(textureFN)) {
            regionComp.textureRegion.setRegion(assets.getTexture(textureFN));
            regionComp.originX = regionComp.textureRegion.getRegionWidth()/2f;
            regionComp.originY = regionComp.textureRegion.getRegionHeight()/2f;
        }

        entity.add(regionComp);
        return this;
    }

    public Factory addTextureRegion(String textureFN, boolean originX, boolean originY) {
        if (!creating) return null;
        regionComp = engine.createComponent(TextureRegionComponent.class);
        if (regionComp.textureRegion.getTexture() != assets.getTexture(textureFN)) {
            regionComp.textureRegion.setRegion(assets.getTexture(textureFN));
            if (originX) regionComp.originX = regionComp.textureRegion.getRegionWidth()/2f;
            if (originY) regionComp.originY = regionComp.textureRegion.getRegionHeight()/2f;
        }

        entity.add(regionComp);
        return this;
    }

    public Factory addTextureRegion() {
        if (!creating) return null;
        regionComp = engine.createComponent(TextureRegionComponent.class);

        entity.add(regionComp);
        return this;
    }

    public Factory addTransform(float x, float y) {
        if (!creating) return null;
        transformComp = engine.createComponent(TransformComponent.class);
        transformComp.pos.set(x, y);

        entity.add(transformComp);
        return this;
    }

    public Factory addTransform(float x, float y, float scale, float rotation) {
        if (!creating) return null;
        transformComp = engine.createComponent(TransformComponent.class);
        transformComp.pos.set(x, y);
        transformComp.scale.set(scale, scale);
        transformComp.rotation = rotation;

        entity.add(transformComp);
        return this;
    }

    public Factory addTransform(Vector2 pos) {
        if (!creating) return null;
        transformComp = engine.createComponent(TransformComponent.class);
        transformComp.pos.set(pos);

        entity.add(transformComp);
        return this;
    }

    public Factory addTransform(Vector2 pos, float scale, float rotation) {
        if (!creating) return null;
        transformComp = engine.createComponent(TransformComponent.class);
        transformComp.pos.set(pos);
        transformComp.scale.set(scale, scale);
        transformComp.rotation = rotation;

        entity.add(transformComp);
        return this;
    }

    public Factory addType(TypeComponent.Type type) {
        if (!creating) return null;
        typeComp = engine.createComponent(TypeComponent.class);
        typeComp.type = type;

        entity.add(typeComp);
        return this;
    }

    public Factory addPixmap() {
        if (!creating) return null;
        pixmapComp = engine.createComponent(PixmapComponent.class);

        entity.add(pixmapComp);
        return this;
    }

    public Factory addElement(int colorBits, ElementType elementType, MatterType matterType, float maxSpeed, float accel, int spread) {
        if (!creating) return null;
        elementComp = engine.createComponent(ElementComponent.class);
        elementComp.colorBits = colorBits;
        elementComp.elementType = elementType;
        elementComp.matterType = matterType;
        elementComp.maxSpeed = maxSpeed;
        elementComp.acceleration = accel;
        elementComp.spread = spread;

        entity.add(elementComp);
        return this;
    }

    /**This one Needs to be AFTER!! transform and region components!!!*/
    public Factory addClickableAuto(Command onClickCommand, Command onTouchDownCommand, Command onTouchUpCommand, Command onMouseOverCommand, Command onMouseOffCommand, int priority, int vibrationMs) {
        if (!creating) return null;
        clickableComp = engine.createComponent(ClickableComponent.class);
        clickableComp.hitBox.set(transformComp.pos.x, transformComp.pos.y, regionComp.textureRegion.getRegionWidth(), regionComp.textureRegion.getRegionHeight());
        clickableComp.onClickCommand = onClickCommand;
        clickableComp.onTouchDownCommand = onTouchDownCommand;
        clickableComp.onTouchUpCommand = onTouchUpCommand;
        clickableComp.onMouseOverCommand = onMouseOverCommand;
        clickableComp.onMouseOffCommand = onMouseOffCommand;
        clickableComp.priority = priority;
        clickableComp.vibrationMs = vibrationMs;

        entity.add(clickableComp);
        return this;
    }

    public Factory addClickable(float x, float y, float width, float height, Command onClickCommand, Command onTouchDownCommand, Command onTouchUpCommand, Command onMouseOverCommand, Command onMouseOffCommand, int priority, int vibrationMs) {
        if (!creating) return null;
        clickableComp = engine.createComponent(ClickableComponent.class);
        clickableComp.hitBox.set(x, y, width, height);
        clickableComp.onClickCommand = onClickCommand;
        clickableComp.onTouchDownCommand = onTouchDownCommand;
        clickableComp.onTouchUpCommand = onTouchUpCommand;
        clickableComp.onMouseOverCommand = onMouseOverCommand;
        clickableComp.onMouseOffCommand = onMouseOffCommand;
        clickableComp.priority = priority;
        clickableComp.vibrationMs = vibrationMs;

        entity.add(clickableComp);
        return this;
    }
}
