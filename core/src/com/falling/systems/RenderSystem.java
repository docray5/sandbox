package com.falling.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.systems.SortedIteratingSystem;
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
    private final TextureRegion fboMainTexture;
    private final Array<Entity> renderQueue;
    private final Array<Entity> renderAfterVFXQueue;
    private Blur blur;
    private float oldWorldWidth;
    private float oldWorldHeight;

    private Entity entityTmp;
    private TransformComponent transformTmp;
    private TextureRegionComponent regionTmp;
    private TextComponent textTmp;
    private RenderableComponent renderableTmp;
    private float widthTmp;
    private float heightTmp;
    private float drawX;
    private float drawY;

    public RenderSystem(int priority) {
        super(renderableFamily, new PriorityComparator(), priority);

        spriteBatch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, worldWidth, worldHeight);
        viewport = new ExtendViewport(worldWidth, worldHeight, camera);
        cameraPos.set(camera.position);

        fboMain = new FrameBuffer(Pixmap.Format.RGBA8888, screenWidth, screenHeight, false);
        fboMain.getColorBufferTexture().setFilter(Texture.TextureFilter.Linear,
                Texture.TextureFilter.Nearest);
        fboMainTexture = new TextureRegion(fboMain.getColorBufferTexture());
        fboMainTexture.flip(false, true);

        renderQueue = new Array<>();
        renderAfterVFXQueue = new Array<>();

        oldWorldWidth = worldWidth;
        oldWorldHeight = worldHeight;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        camera.position.set(cameraPos);

        // render main scene to a fbo
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

        if (blur != null && blur.isActive()) blur.blur(spriteBatch, fboMain, deltaTime);

        // draw the whole scene
        spriteBatch.begin();
        if (blur != null && blur.isActive()) spriteBatch.draw(blur.getBlurredTexture(), cameraPos.x - viewport.getWorldWidth()/2f, cameraPos.y - viewport.getWorldHeight()/2f, viewport.getWorldWidth(), viewport.getWorldHeight(), 0, 0, 1, 1);
        else spriteBatch.draw(fboMainTexture, cameraPos.x - viewport.getWorldWidth()/2f, cameraPos.y - viewport.getWorldHeight()/2f, viewport.getWorldWidth(), viewport.getWorldHeight());

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
        screenWidth = (int) viewport.getWorldWidth();
        screenHeight = (int) viewport.getWorldHeight();

        if (worldWidth == oldWorldWidth && worldHeight == oldWorldHeight)
            return;

        lrGutter = (worldWidth - startWorldWidth)/2;
        tbGutter = (worldHeight - startWorldHeight)/2;

        xGutOffset = (worldWidth - oldWorldWidth)/2;
        yGutOffset = (worldHeight - oldWorldHeight)/2;

        oldWorldWidth = worldWidth;
        oldWorldHeight = worldHeight;

        fboMain.dispose();
        fboMain = null;

        fboMain = new FrameBuffer(Pixmap.Format.RGBA8888, screenWidth == 0 ? (int) worldWidth : screenWidth, screenHeight == 0 ? (int) worldHeight : screenHeight, false);
        fboMain.getColorBufferTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Nearest);

        fboMainTexture.setRegion(fboMain.getColorBufferTexture());
        fboMainTexture.flip(false, true);

        if (blur != null) blur.resize();

        if (getEngine().getSystem(WorldSystem.class) != null) getEngine().getSystem(WorldSystem.class).resize();
        if (getEngine().getSystem(ResizeableSystem.class) != null) getEngine().getSystem(ResizeableSystem.class).resize();
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
                textTmp = textMapper.get(entityTmp);

                textTmp.font.getData().setScale(transformTmp.scale.x, transformTmp.scale.y);
                textTmp.font.setColor(renderableTmp.color);
                textTmp.glyphLayout.setText(textTmp.font, textTmp.text);

                textTmp.font.draw(spriteBatch, textTmp.text, drawX - textTmp.glyphLayout.width/2f, drawY - textTmp.glyphLayout.height/2f);
            }
        }
    }

    public void init(Assets assets) {
        blur = new Blur(assets);
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
