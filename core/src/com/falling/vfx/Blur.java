package com.falling.vfx;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.falling.assets.Assets;

import static com.badlogic.gdx.Gdx.gl;
import static com.falling.Core.*;

public class Blur {
    private final ShaderProgram blurShader;
    private FrameBuffer fbo1, fbo2;
    private float radius;
    private int iterations;
    private final float accel;
    private boolean direction = true;
    private boolean animating;
    private boolean active;
    private Texture fbo1Texture;
    private Texture fbo2Texture;

    private Texture testFbo1Texture;
    private Texture testFbo2Texture;
    private FrameBuffer testFbo1, testFbo2;
    private Vector2 pos;
    private Vector2 size;
    private Matrix4 matrix4;

    public Blur(Assets assets) {
        accel = 4f;
        radius = 0;
        iterations = 4;

        ShaderProgram.pedantic = false;
        blurShader = new ShaderProgram(assets.getShader("blurVert"), assets.getShader("blurFrag"));
        if (!blurShader.isCompiled()) { System.out.println(blurShader.getLog()); }
        blurShader.bind();
        blurShader.setUniformf("resolution", worldWidth);
        blurShader.setUniformf("radius", radius);
        blurShader.setUniformf("dir", 0f, 0f);

        fbo1 = new FrameBuffer(Pixmap.Format.RGBA8888, (int) worldWidth/2, (int) worldHeight/2, false);
        fbo2 = new FrameBuffer(Pixmap.Format.RGBA8888, (int) worldWidth/2, (int) worldHeight/2, false);
        fbo1Texture = fbo1.getColorBufferTexture();
        fbo2Texture = fbo2.getColorBufferTexture();

        size = new Vector2(32, 32);
        pos = new Vector2(16, 16);

        testFbo1 = new FrameBuffer(Pixmap.Format.RGBA8888, (int) size.x, (int) size.y, false);
        testFbo2 = new FrameBuffer(Pixmap.Format.RGBA8888, (int) size.x, (int) size.y, false);

        testFbo1Texture = testFbo1.getColorBufferTexture();
        testFbo2Texture = testFbo2.getColorBufferTexture();
        matrix4 = new Matrix4();
        matrix4.setToOrtho2D(0, 0, (int)size.x, (int)size.y);
    }

    public void miniBlur(SpriteBatch batch, TextureRegion textureToBlur) {
        if (!updateBlur) return;
        updateBlur = false;

        // TODO the region is returing a too big size and we need to scale it then
        textureToBlur.setRegion((int)(pos.x)*4, (int)(pos.y)*4, (int)size.x*4, (int)size.y*4);

        batch.setProjectionMatrix(matrix4);

        for (int i = 0; i < iterations; i++) {
            testFbo1.begin();
            batch.begin();
            blurShader.setUniformf("dir", 1f, 0f);
            blurShader.setUniformf("radius", 1);
            blurShader.setUniformf("resolution", size.x);

            if (i==0) batch.draw(textureToBlur, 0, 0, size.x, size.y);
            else batch.draw(testFbo2Texture, 0, 0, size.x, size.y, 0, 0, 1, 1);

            batch.end();
            testFbo1.end();

            batch.setShader(blurShader);
            testFbo2.begin();
            batch.begin();
            blurShader.setUniformf("dir", 0f, 1f);
            blurShader.setUniformf("resolution", size.y);
            batch.draw(testFbo1Texture, 0, 0, size.x, size.y, 0, 0, 1, 1);
            batch.end();
            testFbo2.end();
        }

        batch.setShader(null);
    }

    public void blur(SpriteBatch batch, Texture fboTexture, float deltaTime) {
        if (!updateBlur) return;
        updateBlur = false;

        animation(deltaTime);

        batch.setShader(blurShader);
        for (int i = 0; i < iterations; i++) {
            fbo1.begin();
            batch.begin();
            blurShader.setUniformf("dir", 1f, 0f);
            blurShader.setUniformf("radius", radius);
            blurShader.setUniformf("resolution", worldWidth);
            batch.draw(i==0 ? fboTexture : fbo2Texture, cameraPos.x - worldWidth/2f, cameraPos.y - worldHeight/2f, worldWidth, worldHeight, 0, 0, 1, 1);
            batch.end();
            fbo1.end();

            fbo2.begin();
            batch.begin();
            blurShader.setUniformf("dir", 0f, 1f);
            blurShader.setUniformf("resolution", worldHeight);
            batch.draw(fbo1Texture, cameraPos.x - worldWidth/2f, cameraPos.y - worldHeight/2f, worldWidth, worldHeight, 0, 0, 1, 1);
            batch.end();
            fbo2.end();
        }

        batch.setShader(null);
    }

    public Texture getBlurredTexture() {
        return fbo2Texture;
    }

    public Texture getMiniBlurredTexture() {
        return testFbo2Texture;
    }

    public void dispose() {
        fbo1.dispose();
        fbo2.dispose();
    }

    private void animation(float deltaTime) {
        if (animating) {
            updateBlur = true;
            if (direction) {
                if (radius > 0) radius -= accel * deltaTime;
                if (radius < 0.1f) active = false;
                if (radius < 0) {
                    radius = 0;
                    animating = false;
                }
            }
            else {
                if (radius < 2) radius += accel * deltaTime;
                if (radius > 2) {
                    radius = 2;
                    animating = false;
                }
            }
        }
    }

    public void shift() {
        animating = true;
        direction = !direction;
        active = true;
        updateBlur = true;
    }

    public boolean isActive() {
        return active;
    }

    public void resize() {
        updateBlur = true;

        fbo1Texture.dispose();
        fbo1Texture = null;

        fbo2Texture.dispose();
        fbo2Texture = null;

        fbo1.dispose();
        fbo2.dispose();
        fbo1 = null;
        fbo2 = null;

        fbo1 = new FrameBuffer(Pixmap.Format.RGBA8888, (int) worldWidth/2, (int) worldHeight/2, false);
        fbo2 = new FrameBuffer(Pixmap.Format.RGBA8888, (int) worldWidth/2, (int) worldHeight/2, false);

        fbo1Texture = fbo1.getColorBufferTexture();
        fbo2Texture = fbo2.getColorBufferTexture();

        testFbo1Texture.dispose();
        testFbo1Texture = null;

        testFbo2Texture.dispose();
        testFbo2Texture = null;

        testFbo1.dispose();
        testFbo2.dispose();
        testFbo1 = null;
        testFbo2 = null;

        testFbo1 = new FrameBuffer(Pixmap.Format.RGBA8888, (int) size.x, (int) size.y, false);
        testFbo2 = new FrameBuffer(Pixmap.Format.RGBA8888, (int) size.x, (int) size.y, false);

        testFbo1Texture = testFbo1.getColorBufferTexture();
        testFbo2Texture = testFbo2.getColorBufferTexture();
    }
}
