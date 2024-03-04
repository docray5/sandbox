package com.falling.vfx;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.falling.assets.Assets;

import static com.falling.Core.*;

public class Blur {
    private final ShaderProgram blurShader;
    private FrameBuffer fbo1, fbo2;
    private float radius;
    private final float accel;
    private boolean direction = true;
    private boolean animating;
    private boolean active;
    private Texture fboTexture;

    public Blur(Assets assets) {
        accel = 4f;
        radius = 0;

        ShaderProgram.pedantic = false;
        blurShader = new ShaderProgram(assets.getShader("blurVert"), assets.getShader("blurFrag"));
        if (!blurShader.isCompiled()) { System.out.println(blurShader.getLog()); }
        blurShader.bind();
        blurShader.setUniformf("resolution", worldWidth);
        blurShader.setUniformf("radius", radius);
        blurShader.setUniformf("dir", 0f, 0f);

        fbo1 = new FrameBuffer(Pixmap.Format.RGBA8888, (int) worldWidth/2, (int) worldHeight/2, false);
        fbo2 = new FrameBuffer(Pixmap.Format.RGBA8888, (int) worldWidth/2, (int) worldHeight/2, false);
        fboTexture = fbo2.getColorBufferTexture();
    }

    public void blur(SpriteBatch batch, FrameBuffer fbo, float deltaTime) {
        animation(deltaTime);

        batch.setShader(blurShader);
        for (int i = 0; i < 4; i++) {
            fbo1.begin();
            batch.begin();
            blurShader.setUniformf("dir", 1f, 0f);
            blurShader.setUniformf("radius", radius);
            blurShader.setUniformf("resolution", worldWidth);
            batch.draw(i==0 ? fbo.getColorBufferTexture() : fbo2.getColorBufferTexture(), cameraPos.x - worldWidth/2f, cameraPos.y - worldHeight/2f, worldWidth, worldHeight, 0, 0, 1, 1);
            batch.end();
            fbo1.end();

            fbo2.begin();
            batch.begin();
            blurShader.setUniformf("dir", 0f, 1f);
            blurShader.setUniformf("resolution", worldHeight);
            batch.draw(fbo1.getColorBufferTexture(), cameraPos.x - worldWidth/2f, cameraPos.y - worldHeight/2f, worldWidth, worldHeight, 0, 0, 1, 1);
            batch.end();
            fbo2.end();
        }

        batch.setShader(null);
    }

    public Texture getBlurredTexture() {
        return fboTexture;
    }

    public void dispose() {
        fbo1.dispose();
        fbo2.dispose();
    }

    private void animation(float deltaTime) {
        if (animating) {
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
    }

    public boolean isActive() {
        return active;
    }

    public void resize() {
        fbo1.dispose();
        fbo2.dispose();
        fbo1 = null;
        fbo2 = null;

        fbo1 = new FrameBuffer(Pixmap.Format.RGBA8888, (int) worldWidth/2, (int) worldHeight/2, false);
        fbo2 = new FrameBuffer(Pixmap.Format.RGBA8888, (int) worldWidth/2, (int) worldHeight/2, false);

        fboTexture.dispose();
        fboTexture = null;
        fboTexture = fbo2.getColorBufferTexture();
    }
}
