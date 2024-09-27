package com.falling.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.falling.assets.Assets;
import com.falling.components.*;
import com.falling.events.Event;
import com.falling.events.Publisher;
import com.falling.utils.PriorityComparator;
import com.falling.vfx.Blur;

import static com.badlogic.gdx.Gdx.gl;
import static com.falling.Core.*;
import static com.falling.utils.Families.renderableFamily;
import static com.falling.utils.Mappers.*;

public class RenderSystem extends SortedIteratingSystem {
    private final SpriteBatch spriteBatch;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private FrameBuffer fboMain;
    private final TextureRegion fboMainTextureRegion;
    private final TextureRegion toBlurRegion;
    private FrameBuffer fboMasks;
    private final TextureRegion fboMasksTexture;
    private final Array<Entity> renderQueue;
    private final Array<Entity> renderAfterVFXQueue;
    private final Array<Entity> blurQueue;
    private final Array<Entity> maskQueue;
    private Blur blur;
    private float oldWorldWidth;
    private float oldWorldHeight;
    private FrameBuffer fboMain2;
    private final TextureRegion fboMain2TextureRegion;
    private ShaderProgram whiteShader;

    private Entity entityTmp;
    private TransformComp transformTmp;
    private TextureRegionComp regionTmp;
    private TextComp textTmp;
    private RenderableComp renderableTmp;
    private float widthTmp;
    private float heightTmp;
    private float drawX;
    private float drawY;
    private NinepatchComp ninepatchTmp;
    private BlurComp blurCompTmp;
    private MaskComp maskCompTmp;

    public final Publisher publisher = new Publisher();

    private FPSLogger fpsLogger;

    public RenderSystem(int priority) {
        super(renderableFamily, new PriorityComparator(), priority);

        spriteBatch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, worldWidth, worldHeight);
        viewport = new ExtendViewport(worldWidth, worldHeight, camera);
        cameraPos.set(camera.position);

        if (pixelate == true) {
            fboMain = new FrameBuffer(Pixmap.Format.RGBA8888, (int) worldWidth, (int) worldHeight, false);
            fboMain2 = new FrameBuffer(Pixmap.Format.RGBA8888, (int) worldWidth, (int) worldHeight, false);
            fboMasks = new FrameBuffer(Pixmap.Format.RGBA8888, (int) worldWidth, (int) worldHeight, false);
        } else {
            fboMain = new FrameBuffer(Pixmap.Format.RGBA8888, screenWidth, screenHeight, false);
            fboMain2 = new FrameBuffer(Pixmap.Format.RGBA8888, screenWidth, screenHeight, false);
            fboMasks = new FrameBuffer(Pixmap.Format.RGBA8888, screenWidth, screenHeight, false);
        }

        fboMain.getColorBufferTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Nearest);
        fboMainTextureRegion = new TextureRegion(fboMain.getColorBufferTexture());
        fboMainTextureRegion.flip(false, true);

        fboMasks.getColorBufferTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Nearest);
        fboMasksTexture = new TextureRegion(fboMasks.getColorBufferTexture());
        fboMasksTexture.flip(false, true);

        fboMain2.getColorBufferTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Nearest);
        fboMain2TextureRegion = new TextureRegion(fboMain2.getColorBufferTexture());
        fboMain2TextureRegion.flip(false, true);

        toBlurRegion = new TextureRegion(fboMain.getColorBufferTexture(), 16, 16, 32, 32);
        toBlurRegion.flip(false, true);

        renderQueue = new Array<>();
        renderAfterVFXQueue = new Array<>();
        blurQueue = new Array<>();
        maskQueue = new Array<>();

        oldWorldWidth = worldWidth;
        oldWorldHeight = worldHeight;
        
        fpsLogger = new FPSLogger();
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        camera.position.set(cameraPos);

        // render main scene to an fbo
        camera.update();
        spriteBatch.setProjectionMatrix(camera.combined);
        fboMain.begin();
        spriteBatch.begin();
        gl.glClearColor(0, 0, 0, 1);
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderQueue(renderQueue);

        spriteBatch.end();
        fboMain.end();
        renderQueue.clear();

        // === Mini Blur ===
        if (blur != null)
            runMiniBlur();
        blurQueue.clear();

        // === Draw Masks ===
        fboMasks.begin();
        spriteBatch.begin();
        gl.glClearColor(0, 0, 0, 0);
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        maskThem();

        spriteBatch.end();
        fboMasks.end();

        // === Draw Everything to another frame buffer for further post processing ===
        fboMain2.begin();
        spriteBatch.begin();
        gl.glClearColor(0, 0, 0, 1);
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        spriteBatch.draw(fboMainTextureRegion, cameraPos.x - viewport.getWorldWidth()/2f, cameraPos.y - viewport.getWorldHeight()/2f, viewport.getWorldWidth(), viewport.getWorldHeight());

        spriteBatch.draw(fboMasksTexture, cameraPos.x - viewport.getWorldWidth()/2f, cameraPos.y - viewport.getWorldHeight()/2f, viewport.getWorldWidth(), viewport.getWorldHeight());
        renderQueue(maskQueue);

        spriteBatch.end();
        fboMain2.end();
        maskQueue.clear();

        // === Run blur over the screen ===
        if (blur != null && blur.isActive()) 
            blur.blur(spriteBatch, fboMain2TextureRegion.getTexture(), deltaTime);
        
        // === Draw Blur or Main Frame Buffer ===
        spriteBatch.begin();
        gl.glClearColor(0, 0, 0, 1);
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (blur != null && blur.isActive()) spriteBatch.draw(blur.getBlurredTexture(), cameraPos.x - viewport.getWorldWidth()/2f, cameraPos.y - viewport.getWorldHeight()/2f, viewport.getWorldWidth(), viewport.getWorldHeight(), 0, 0, 1, 1);
        else spriteBatch.draw(fboMain2TextureRegion, cameraPos.x - viewport.getWorldWidth()/2f, cameraPos.y - viewport.getWorldHeight()/2f, viewport.getWorldWidth(), viewport.getWorldHeight());

        // render objects after vfx
        renderQueue(renderAfterVFXQueue);
        spriteBatch.end();
        renderAfterVFXQueue.clear();

        fpsLogger.log();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (!renderableMapper.get(entity).render) return;

        if (blurMapper.has(entity)) blurQueue.add(entity);

        if (renderableMapper.get(entity).afterVfx) renderAfterVFXQueue.add(entity);
        else if (maskMapper.has(entity)) maskQueue.add(entity);
        else renderQueue.add(entity);
    }

    private void runMiniBlur() {
        if (!updateBlur) return;
        if (!blur.isActive()) updateBlur = false;

        for (int i = blurQueue.size-1; i >= 0; i--) {
            entityTmp = blurQueue.get(i);
            blurCompTmp = blurMapper.get(entityTmp);
            drawX = blurCompTmp.regionPos.x;
            drawY = blurCompTmp.regionPos.y;
            widthTmp = blurCompTmp.fboWidth;
            heightTmp = blurCompTmp.fboHeight;
            if (renderableMapper.get(entityTmp).center) {
                drawX -= widthTmp/2f;
                drawY -= heightTmp/2f;
            }

            centerBlur();
            
            if (pixelate) {
                toBlurRegion.setRegion((int) (drawX), (int) (drawY), (int) (blurCompTmp.fboWidth), (int) (blurCompTmp.fboHeight));
            } else {
                // we are scaling because the fbo is of screen size
                toBlurRegion.setRegion((int) (drawX*windowScaleX), (int) (drawY*windowScaleY), (int) (blurCompTmp.fboWidth*windowScaleX), (int) (blurCompTmp.fboHeight*windowScaleY));
            }
            spriteBatch.setProjectionMatrix(blurCompTmp.matrix4);

            blur.miniBlur(spriteBatch, toBlurRegion, blurCompTmp);
        }
        spriteBatch.setProjectionMatrix(camera.combined);
    }

    private void maskThem() {
        for (int i = 0; i < maskQueue.size; i++) {
            entityTmp = maskQueue.get(i);

            spriteBatch.flush();

            Gdx.gl.glColorMask(false, false, false, true);
            spriteBatch.setBlendFunction(GL20.GL_ONE, GL20.GL_ZERO);

            /* Draw alpha masks. */
            if (maskMapper.get(entityTmp).autoMask) {
                if (texRegionMapper.has(entityTmp)) {
                    if (whiteShader!=null) spriteBatch.setShader(whiteShader);
                    renderRegion();
                    spriteBatch.setShader(null);
                }
                else if (textMapper.has(entityTmp))
                    renderText(true);
                else if (ninepatchMapper.has(entityTmp)) {
                    if (whiteShader!=null) spriteBatch.setShader(whiteShader);
                    renderNinePatch();
                    spriteBatch.setShader(null);
                }
            } else {
                renderMask();
            }

            spriteBatch.flush();
            Gdx.gl.glColorMask(true, true, true, true);
            spriteBatch.setBlendFunction(GL20.GL_DST_ALPHA, GL20.GL_ZERO);

            /* Draw our sprite to be masked. */
            if (blur != null && blurMapper.has(entityTmp) && blurMapper.get(entityTmp).blurBackground) {
                blurCompTmp = blurMapper.get(entityTmp);
                centerBlur();
                spriteBatch.draw(blurCompTmp.textureRegion, drawX, drawY,
                    blurCompTmp.fboWidth, blurCompTmp.fboHeight, blurCompTmp.fboWidth, blurCompTmp.fboHeight,
                    transformTmp.scale.x, transformTmp.scale.y, transformTmp.rotation);
            }
        }
    }

    private void renderQueue(Array<Entity> queue) {
        for (int i = 0; i < queue.size; i++) {
            entityTmp = queue.get(i);

            if (texRegionMapper.has(entityTmp))
                renderRegion();
            else if (textMapper.has(entityTmp))
                renderText(false);
            else if (ninepatchMapper.has(entityTmp))
                renderNinePatch();
        }
    }

    private void renderMask() {
        renderableTmp = renderableMapper.get(entityTmp);
        transformTmp = transformMapper.get(entityTmp);

        drawX = transformTmp.pos.x;
        drawY = transformTmp.pos.y;

        maskCompTmp = maskMapper.get(entityTmp);

        widthTmp = maskCompTmp.textureRegion.getRegionWidth();
        heightTmp = maskCompTmp.textureRegion.getRegionHeight();

        if (renderableTmp.center) {
            drawX -= widthTmp/2f;
            drawY -= heightTmp/2f;
        }

        spriteBatch.draw(maskCompTmp.textureRegion, drawX, drawY,
                maskCompTmp.originX, maskCompTmp.originY, widthTmp, heightTmp,
                transformTmp.scale.x, transformTmp.scale.y, transformTmp.rotation);
    }

    private void renderNinePatch() {
        renderableTmp = renderableMapper.get(entityTmp);
        transformTmp = transformMapper.get(entityTmp);

        drawX = transformTmp.pos.x;
        drawY = transformTmp.pos.y;

        ninepatchTmp = ninepatchMapper.get(entityTmp);

        widthTmp = ninepatchTmp.size.x;
        heightTmp = ninepatchTmp.size.y;
        
        if (renderableTmp.center) {
            drawX -= widthTmp/2f;
            drawY -= heightTmp/2f;
        }

        spriteBatch.setColor(renderableTmp.color);
        ninepatchTmp.ninePatch.draw(spriteBatch, drawX, drawY,
            widthTmp/2f, heightTmp/2f,
            ninepatchTmp.size.x, ninepatchTmp.size.y,
            transformTmp.scale.x, transformTmp.scale.y, transformTmp.rotation);

        spriteBatch.setColor(1, 1, 1, 1);
    }

    private void renderText(boolean fullWhite) {
        renderableTmp = renderableMapper.get(entityTmp);
        transformTmp = transformMapper.get(entityTmp);

        drawX = transformTmp.pos.x;
        drawY = transformTmp.pos.y;


        textTmp = textMapper.get(entityTmp);

        textTmp.font.getData().setScale(transformTmp.scale.x, transformTmp.scale.y);
        if (fullWhite) textTmp.font.setColor(Color.WHITE);
        else textTmp.font.setColor(renderableTmp.color);
        textTmp.glyphLayout.setText(textTmp.font, textTmp.text);

        widthTmp = textTmp.glyphLayout.width;
        heightTmp = textTmp.glyphLayout.height;

        textTmp.font.draw(spriteBatch, textTmp.text, drawX - textTmp.glyphLayout.width/2f, drawY - textTmp.glyphLayout.height/2f);
    }

    private void renderRegion() {
        renderableTmp = renderableMapper.get(entityTmp);
        transformTmp = transformMapper.get(entityTmp);

        drawX = transformTmp.pos.x;
        drawY = transformTmp.pos.y;

        regionTmp = texRegionMapper.get(entityTmp);

        widthTmp = regionTmp.textureRegion.getRegionWidth();
        heightTmp = regionTmp.textureRegion.getRegionHeight();

        if (renderableTmp.center) {
            drawX -= widthTmp/2f;
            drawY -= heightTmp/2f;
        }

        // ==== Draw texture ====
        spriteBatch.setColor(renderableTmp.color);
        spriteBatch.draw(regionTmp.textureRegion, drawX, drawY,
                regionTmp.originX, regionTmp.originY, widthTmp, heightTmp,
                transformTmp.scale.x, transformTmp.scale.y, transformTmp.rotation);

        spriteBatch.setColor(1, 1, 1, 1);
    }

    public void resize(int width, int height) {
        viewport.update(width, height);
        worldWidth = viewport.getWorldWidth();
        worldHeight = viewport.getWorldHeight();

        if (pixelate) {
            screenWidth = (int) viewport.getWorldWidth();
            screenHeight = (int) viewport.getWorldHeight();
        } else {
            screenWidth = viewport.getScreenWidth();
            screenHeight = viewport.getScreenHeight();
        }

        if (worldWidth == oldWorldWidth && worldHeight == oldWorldHeight)
            return;

        windowScaleX = screenWidth/worldWidth;
        windowScaleY = screenHeight/worldHeight;

        lrGutter = (worldWidth - startWorldWidth)/2;
        tbGutter = (worldHeight - startWorldHeight)/2;

        xGutOffset = (worldWidth - oldWorldWidth)/2;
        yGutOffset = (worldHeight - oldWorldHeight)/2;

        oldWorldWidth = worldWidth;
        oldWorldHeight = worldHeight;

        fboMain.dispose();
        fboMain = null;

        fboMain2.dispose();
        fboMain2 = null;

        fboMasks.dispose();
        fboMasks = null;

        fboMain = new FrameBuffer(Pixmap.Format.RGBA8888, screenWidth == 0 ? (int) worldWidth : screenWidth, screenHeight == 0 ? (int) worldHeight : screenHeight, false);
        fboMain.getColorBufferTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Nearest);

        fboMain2 = new FrameBuffer(Pixmap.Format.RGBA8888, screenWidth == 0 ? (int) worldWidth : screenWidth, screenHeight == 0 ? (int) worldHeight : screenHeight, false);
        fboMain2.getColorBufferTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Nearest);

        fboMasks = new FrameBuffer(Pixmap.Format.RGBA8888, screenWidth == 0 ? (int) worldWidth : screenWidth, screenHeight == 0 ? (int) worldHeight : screenHeight, false);
        fboMasks.getColorBufferTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Nearest);

        fboMainTextureRegion.setRegion(fboMain.getColorBufferTexture());
        fboMainTextureRegion.flip(false, true);

        fboMain2TextureRegion.setRegion(fboMain2.getColorBufferTexture());
        fboMain2TextureRegion.flip(false, true);

        toBlurRegion.setRegion(fboMain.getColorBufferTexture());
        toBlurRegion.flip(false, true);

        fboMasksTexture.setRegion(fboMasks.getColorBufferTexture());
        fboMasksTexture.flip(false, true);

        updateBlur = true;

        if (blur != null) blur.resizeNext();

        publisher.notify(null, Event.RESIZE);
    }

    /** Centering our buffer if its bigger than mask */
    private void centerBlur() {
        if (maskMapper.has(entityTmp)) {
            drawX -= (blurCompTmp.fboWidth - widthTmp)/2f;
            drawY -= (blurCompTmp.fboHeight - widthTmp)/2f;
        }
    }

    public void resizeBlurComp(BlurComp blurComponent, int newWidth, int newHeight) {
        blur.resizeMiniBlur(blurComponent, newWidth, newHeight);
    }

    public void init(Assets assets) {
        blur = new Blur(assets);
        whiteShader = new ShaderProgram(assets.getShader("blurVert"), assets.getShader("whiteFrag"));
        if (!whiteShader.isCompiled()) System.out.println(whiteShader.getLog());
        whiteShader.bind();
        whiteShader.setUniformf("COLOR", new Vector3(1f, 1f, 1f));
    }

    public void dispose() {
        spriteBatch.dispose();
        fboMain.dispose();
        blur.dispose();
    }

    /**USED ONLY ONCE*/
    public Viewport getViewport() {
        return viewport;
    }

    public void shiftBlur() {
        blur.shift();
    }
}
