package com.falling.factories;

import com.badlogic.ashley.core.*;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.falling.assets.Assets;
import com.falling.commands.Command;
import com.falling.components.*;
import com.falling.components.ElementComp.ElementType;
import com.falling.components.ElementComp.MatterType;
import com.falling.components.VecAnimatorComp.AnimatorType;
import com.falling.factories.builders.RenderableBuilder;
import com.falling.components.SceneComp.Scene;

public class Factory {
    private final Engine engine;
    protected final Assets assets;

    private Entity entity;
    private VecAnimatorComp animatorComp;
    private AnimationComp animationComp;
    private CursorComp cursorComp;
    private final RenderableBuilder renderableBuilder;
    private ResizeComp resizeComp;
    private TextComp textComp;
    private TextureRegionComp regionComp;
    private TransformComp transformComp;
    private TypeComp typeComp;
    private PixmapComp pixmapComp;
    private ElementComp elementComp;
    private ClickableComp clickableComp;
    private NinepatchComp ninepatchComp;
    private SceneComp sceneComp;
    private BlurComp blurComp;
    private MaskComp maskComp;
    private FrameBufferComp frameBufferComp;

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

    public Factory addAnimation(VecAnimatorComp... animatorComps) {
        if (!creating) return null;
        animationComp = engine.createComponent(AnimationComp.class);

        for (int i = 0; i < animatorComps.length; i++) {
            animationComp.animators.add(animatorComps[i]);
        }

        entity.add(animationComp);
        return this;
    }

    /** Put After setting transform and or NinePatch */
    public VecAnimatorComp createAnimator(AnimatorType type, float duration, Command commandOnFinish, Interpolation interpolation) {
        return createAnimator(type, duration, commandOnFinish, interpolation, false);
    }

    /** Put After setting transform and or NinePatch */
    public VecAnimatorComp createAnimator(AnimatorType type, float duration, Command commandOnFinish, Interpolation interpolation, boolean cmdOnEveryFinish) {
        animatorComp = engine.createComponent(VecAnimatorComp.class);

        switch (type) {
            case POS:
                animatorComp.animatedVecPointer = transformComp.pos;
                break;
            case SCALE:
                animatorComp.animatedVecPointer = transformComp.scale;
                break;
            case SIZE:
                animatorComp.animatedVecPointer = ninepatchComp.size;
            default:
                break;
        }

        animatorComp.type = type;
        animatorComp.duration = duration;
        animatorComp.commandOnFinish = commandOnFinish;
        animatorComp.interpolation = interpolation;
        animatorComp.cmdOnEveryFinish = cmdOnEveryFinish;
        return animatorComp;
    }

    /** what the fk???? */
    public Factory addCursor() {
        if (!creating) return null;
        cursorComp = engine.createComponent(CursorComp.class);

        entity.add(cursorComp);
        return this;
    }

    public RenderableBuilder addRenderable() {
        if (!creating) return null;
        return renderableBuilder.createComponent();
    }

    public Factory addResize(ResizeComp.STICK_TYPE stick) {
        if (!creating) return null;
        resizeComp = engine.createComponent(ResizeComp.class);
        resizeComp.stick = stick;

        entity.add(resizeComp);
        return this;
    }

   public Factory addText(String text) {
        if (!creating) return null;
        textComp = engine.createComponent(TextComp.class);
        textComp.text = text;
        textComp.font = assets.getFont("alagard.fnt");

        entity.add(textComp);
        return this;
    }

    public Factory addText(String text, String fontFN) {
        if (!creating) return null;
        textComp = engine.createComponent(TextComp.class);
        textComp.text = text;
        textComp.font = assets.getFont(fontFN);

        entity.add(textComp);
        return this;
    }

    public Factory addTextureRegion(String textureFN) {
        if (!creating) return null;
        regionComp = engine.createComponent(TextureRegionComp.class);
        if (regionComp.textureRegion.getTexture() != assets.getTexture(textureFN)) {
            regionComp.textureRegion.setRegion(assets.getTexture(textureFN));
            regionComp.originX = regionComp.textureRegion.getRegionWidth()/2f;
            regionComp.originY = regionComp.textureRegion.getRegionHeight()/2f;
            regionComp.width = regionComp.textureRegion.getRegionWidth();
            regionComp.height = regionComp.textureRegion.getRegionHeight();
        }

        entity.add(regionComp);
        return this;
    }

    public Factory addTextureRegion(String textureFN, boolean originX, boolean originY) {
        if (!creating) return null;
        regionComp = engine.createComponent(TextureRegionComp.class);
        if (regionComp.textureRegion.getTexture() != assets.getTexture(textureFN)) {
            regionComp.textureRegion.setRegion(assets.getTexture(textureFN));
            if (originX) regionComp.originX = regionComp.textureRegion.getRegionWidth()/2f;
            if (originY) regionComp.originY = regionComp.textureRegion.getRegionHeight()/2f;
            regionComp.width = regionComp.textureRegion.getRegionWidth();
            regionComp.height = regionComp.textureRegion.getRegionHeight();
        }

        entity.add(regionComp);
        return this;
    }

    /** Note: You must manually set width, height parameters and use setRegion() */
    public Factory addTextureRegion() {
        if (!creating) return null;
        regionComp = engine.createComponent(TextureRegionComp.class);

        entity.add(regionComp);
        return this;
    }

    public Factory addTransform(float x, float y) {
        if (!creating) return null;
        transformComp = engine.createComponent(TransformComp.class);
        transformComp.pos.set(x, y);

        entity.add(transformComp);
        return this;
    }

    public Factory addTransform(float x, float y, float scale, float rotation) {
        if (!creating) return null;
        transformComp = engine.createComponent(TransformComp.class);
        transformComp.pos.set(x, y);
        transformComp.scale.set(scale, scale);
        transformComp.rotation = rotation;

        entity.add(transformComp);
        return this;
    }

    public Factory addTransform(Vector2 pos) {
        if (!creating) return null;
        transformComp = engine.createComponent(TransformComp.class);
        transformComp.pos.set(pos);

        entity.add(transformComp);
        return this;
    }

    public Factory addTransform(Vector2 pos, float scale, float rotation) {
        if (!creating) return null;
        transformComp = engine.createComponent(TransformComp.class);
        transformComp.pos.set(pos);
        transformComp.scale.set(scale, scale);
        transformComp.rotation = rotation;

        entity.add(transformComp);
        return this;
    }

    public Factory addType(TypeComp.Type type) {
        if (!creating) return null;
        typeComp = engine.createComponent(TypeComp.class);
        typeComp.type = type;

        entity.add(typeComp);
        return this;
    }

    public Factory addPixmap() {
        if (!creating) return null;
        pixmapComp = engine.createComponent(PixmapComp.class);

        entity.add(pixmapComp);
        return this;
    }

    public Factory addElement(int colorBits, ElementType elementType, MatterType matterType, float maxSpeed, float accel, int spread) {
        if (!creating) return null;
        elementComp = engine.createComponent(ElementComp.class);
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
        clickableComp = engine.createComponent(ClickableComp.class);
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
        clickableComp = engine.createComponent(ClickableComp.class);
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

    public Factory addNinePatch(String textureFn, int left, int right, int top, int bottom, float width, float height) {
        if (!creating) return null;
        ninepatchComp = engine.createComponent(NinepatchComp.class);

        ninepatchComp.ninePatch = new NinePatch(assets.getTexture(textureFn), left, right, top, bottom);
        ninepatchComp.size.x = width;
        ninepatchComp.size.y = height;

        entity.add(ninepatchComp);
        return this;
    }

    /** width and height is set to width and height of the texture */
    public Factory addNinePatch(String textureFn, int left, int right, int top, int bottom) {
        if (!creating) return null;
        ninepatchComp = engine.createComponent(NinepatchComp.class);

        ninepatchComp.ninePatch = new NinePatch(assets.getTexture(textureFn), left, right, top, bottom);
        ninepatchComp.size.x = ninepatchComp.ninePatch.getTotalWidth();
        ninepatchComp.size.y = ninepatchComp.ninePatch.getTotalHeight();

        entity.add(ninepatchComp);
        return this;
    }

    public Factory addSceneComponent(Scene beloningScene, Command openCmd, Command closeCmd) {
        if (!creating) return null;
        sceneComp = engine.createComponent(SceneComp.class);

        sceneComp.belongingScene = beloningScene;
        sceneComp.openCmd = openCmd;
        sceneComp.closeCmd = closeCmd;

        entity.add(sceneComp);
        return this;
    }

    /**
     * for fbos larger than mask set center in renderable to true for correct display of blur
     */
    public Factory addBlurComp(boolean blurBackground, int fboWidth, int fboHeight) {
        if (!creating) return null;
        blurComp = engine.createComponent(BlurComp.class);
    
        blurComp.blurBackground = blurBackground;
        blurComp.fboWidth = fboWidth;
        blurComp.fboHeight = fboHeight;
        blurComp.matrix4.setToOrtho2D(0, 0, fboWidth, fboHeight);
        blurComp.fbo1 = new FrameBuffer(Pixmap.Format.RGBA8888, fboWidth, fboHeight, false);
        blurComp.fbo2 = new FrameBuffer(Pixmap.Format.RGBA8888, fboWidth, fboHeight, false);

        blurComp.fboTexture1 = blurComp.fbo1.getColorBufferTexture();
        blurComp.fboTexture2 = blurComp.fbo2.getColorBufferTexture();

        blurComp.regionPos.set(transformComp.pos);

        blurComp.textureRegion.setRegion(blurComp.fboTexture2);

        entity.add(blurComp);
        return this;
    }

    public Factory addMaskComp(String textureFN) {
        if (!creating) return null;
        maskComp = engine.createComponent(MaskComp.class);
        if (maskComp.textureRegion.getTexture() != assets.getTexture(textureFN)) {
            maskComp.textureRegion.setRegion(assets.getTexture(textureFN));
            maskComp.originX = maskComp.textureRegion.getRegionWidth()/2f;
            maskComp.originY = maskComp.textureRegion.getRegionHeight()/2f;
        }

        entity.add(maskComp);
        return this;
    }

    public Factory addAutoMaskComp() {
        if (!creating) return null;
        maskComp = engine.createComponent(MaskComp.class);

        maskComp.autoMask = true;

        entity.add(maskComp);
        return this;
    }

    public Factory addFrameBufferComp(int width, int height) {
        if (!creating) return null;
        frameBufferComp = engine.createComponent(FrameBufferComp.class);

        frameBufferComp.fbo = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);

        entity.add(frameBufferComp);
        return this;
    }
}
