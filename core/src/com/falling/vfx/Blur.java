package com.falling.vfx;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.falling.assets.Assets;
import com.falling.components.BlurComp;

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

        updateBlur = true;
    }

    public void miniBlur(SpriteBatch batch, TextureRegion textureToBlur, BlurComp blurComp) {
        blurComp.fbo2.begin();
        batch.begin();
        batch.draw(textureToBlur, 0, 0, blurComp.fboWidth, blurComp.fboHeight);
        batch.end();
        blurComp.fbo2.end();
    
        // TODO make it so all the blurring is happening with one shader pass instead of n shader passes
        batch.setShader(blurShader);

        for (int i = 0; i < 1; i++) {
            blurComp.fbo1.begin();
            batch.begin();
            blurShader.setUniformf("dir", 1f, 0f);
            blurShader.setUniformf("radius", 1f);
            blurShader.setUniformf("resolution", blurComp.fboWidth);

            batch.draw(blurComp.fboTexture2, 0, 0, blurComp.fboWidth, blurComp.fboHeight, 0, 0, 1, 1);

            batch.end();
            blurComp.fbo1.end();

            blurComp.fbo2.begin();
            batch.begin();
            blurShader.setUniformf("dir", 0f, 1f);
            blurShader.setUniformf("radius", 1);
            blurShader.setUniformf("resolution", blurComp.fboHeight);
            batch.draw(blurComp.fboTexture1, 0, 0, blurComp.fboWidth, blurComp.fboHeight, 0, 0, 1, 1);
            batch.end();
            blurComp.fbo2.end();
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
    }

    public void resizeMiniBlur() {
        // testFbo1Texture.dispose();
        // testFbo1Texture = null;
        //
        // testFbo2Texture.dispose();
        // testFbo2Texture = null;
        //
        // testFbo1.dispose();
        // testFbo2.dispose();
        // testFbo1 = null;
        // testFbo2 = null;
        //
        // testFbo1 = new FrameBuffer(Pixmap.Format.RGBA8888, (int) size.x, (int) size.y, false);
        // testFbo2 = new FrameBuffer(Pixmap.Format.RGBA8888, (int) size.x, (int) size.y, false);
        //
        // testFbo1Texture = testFbo1.getColorBufferTexture();
        // testFbo2Texture = testFbo2.getColorBufferTexture();
    }
}
