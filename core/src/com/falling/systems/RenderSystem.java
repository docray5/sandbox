package com.falling.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
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
    private Texture fboMainTexture;
    private final Array<Entity> renderQueue;
    private final Array<Entity> renderAfterVFXQueue;
    private Blur blur;
    private float oldWorldWidth;
    private float oldWorldHeight;

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

    private Texture mask;

    public final Publisher publisher = new Publisher();

    public RenderSystem(int priority) {
        super(renderableFamily, new PriorityComparator(), priority);

        spriteBatch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, worldWidth, worldHeight);
        viewport = new ExtendViewport(worldWidth, worldHeight, camera);
        cameraPos.set(camera.position);

        if (pixelate == true)
            fboMain = new FrameBuffer(Pixmap.Format.RGBA8888, (int) worldWidth, (int) worldHeight, false);
        else
            fboMain = new FrameBuffer(Pixmap.Format.RGBA8888, screenWidth, screenHeight, false);
        fboMain.getColorBufferTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Nearest);
        fboMainTextureRegion = new TextureRegion(fboMain.getColorBufferTexture());
        fboMainTextureRegion.flip(false, true);
        fboMainTexture = fboMain.getColorBufferTexture();

        toBlurRegion = new TextureRegion(fboMain.getColorBufferTexture(), 16, 16, 32, 32);
        toBlurRegion.flip(false, true);

        renderQueue = new Array<>();
        renderAfterVFXQueue = new Array<>();

        oldWorldWidth = worldWidth;
        oldWorldHeight = worldHeight;
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

        // Run blur over the fbo if it is enabled
        if (blur != null && blur.isActive()) {
            // blur.blur(spriteBatch, fboMainTexture, deltaTime);
            blur.miniBlur(spriteBatch, toBlurRegion);
            spriteBatch.setProjectionMatrix(camera.combined);
        }

        // draw the fbo to the screen or draw blur to the screen if it is enabled.
        spriteBatch.begin();
        gl.glClearColor(0, 0, 0, 1);
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        spriteBatch.draw(fboMainTextureRegion, cameraPos.x - viewport.getWorldWidth()/2f, cameraPos.y - viewport.getWorldHeight()/2f, viewport.getWorldWidth(), viewport.getWorldHeight());
        // if (blur != null && blur.isActive()) spriteBatch.draw(blur.getBlurredTexture(), cameraPos.x - viewport.getWorldWidth()/2f, cameraPos.y - viewport.getWorldHeight()/2f, viewport.getWorldWidth(), viewport.getWorldHeight(), 0, 0, 1, 1);
        // if (blur != null && blur.isActive()) spriteBatch.draw(blur.getMiniBlurredTexture(), 16, 16, 32, 32);
        
        spriteBatch.end();

        spriteBatch.begin();

        if (blur != null && blur.isActive()) {

            Gdx.gl.glColorMask(false, false, false, true);

            /* Change the blending function for our alpha map. */
            spriteBatch.setBlendFunction(GL20.GL_ONE, GL20.GL_ZERO);

            /* Draw alpha masks. */
            spriteBatch.draw(mask, 16, 16);

            /* This blending function makes it so we subtract instead of adding to the alpha map. */
            spriteBatch.setBlendFunction(GL20.GL_ZERO, GL20.GL_SRC_ALPHA);

            /* Remove the masked sprite's inverse alpha from the map. */
            spriteBatch.draw(blur.getMiniBlurredTexture(), 16, 16, 32, 32);


            /* Flush the batch to the GPU. */
            spriteBatch.flush();



            /* Now that the buffer has our alpha, we simply draw the sprite with the mask applied. */
            Gdx.gl.glColorMask(true, true, true, true);

            /* Change the blending function so the rendered pixels alpha blend with our alpha map. */
            spriteBatch.setBlendFunction(GL20.GL_DST_ALPHA, GL20.GL_ONE_MINUS_DST_ALPHA);

            /* Draw our sprite to be masked. */
            spriteBatch.draw(blur.getMiniBlurredTexture(), 16, 16, 32, 32);

            /* Remember to flush before changing GL states again. */
            spriteBatch.flush();
            
            spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        }

        // render objects after vfx
        renderQueue(renderAfterVFXQueue);

        spriteBatch.end();
        renderAfterVFXQueue.clear();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (!renderableMapper.get(entity).render) return;
        if (renderableMapper.get(entity).afterVfx) renderAfterVFXQueue.add(entity);
        else renderQueue.add(entity);
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

        lrGutter = (worldWidth - startWorldWidth)/2;
        tbGutter = (worldHeight - startWorldHeight)/2;

        xGutOffset = (worldWidth - oldWorldWidth)/2;
        yGutOffset = (worldHeight - oldWorldHeight)/2;

        oldWorldWidth = worldWidth;
        oldWorldHeight = worldHeight;

        fboMainTexture.dispose();
        fboMainTexture = null;

        fboMain.dispose();
        fboMain = null;

        fboMain = new FrameBuffer(Pixmap.Format.RGBA8888, screenWidth == 0 ? (int) worldWidth : screenWidth, screenHeight == 0 ? (int) worldHeight : screenHeight, false);
        fboMain.getColorBufferTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Nearest);

        fboMainTextureRegion.setRegion(fboMain.getColorBufferTexture());
        fboMainTextureRegion.flip(false, true);

        toBlurRegion.setRegion(fboMain.getColorBufferTexture());
        toBlurRegion.flip(false, true);

        fboMainTexture = fboMain.getColorBufferTexture();

        if (blur != null) blur.resize();

        publisher.notify(null, Event.RESIZE);
    }

    private void renderQueue(Array<Entity> queue) {
        for (int i = 0; i < queue.size; i++) {
            entityTmp = queue.get(i);

            renderableTmp = renderableMapper.get(entityTmp);
            transformTmp = transformMapper.get(entityTmp);

            drawX = transformTmp.pos.x;
            drawY = transformTmp.pos.y;

            if (texRegionMapper.has(entityTmp)) {
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
            } else if (textMapper.has(entityTmp)) {
                // Draw text

                textTmp = textMapper.get(entityTmp);

                textTmp.font.getData().setScale(transformTmp.scale.x, transformTmp.scale.y);
                textTmp.font.setColor(renderableTmp.color);
                textTmp.glyphLayout.setText(textTmp.font, textTmp.text);

                textTmp.font.draw(spriteBatch, textTmp.text, drawX - textTmp.glyphLayout.width/2f, drawY - textTmp.glyphLayout.height/2f);
            } else if (ninepatchMapper.has(entityTmp)) {
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
        }
    }

    public void init(Assets assets) {
        blur = new Blur(assets);
        mask = assets.getTexture("mask");
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
