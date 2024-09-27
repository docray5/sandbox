package com.falling.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * BlurComp
 */
public class BlurComp implements Component, Poolable {

    public boolean blurBackground;
    public Matrix4 matrix4 = new Matrix4();
    public FrameBuffer fbo1;
    public FrameBuffer fbo2;
    public Texture fboTexture1;
    /** Your blurred Texture */
    public Texture fboTexture2;
    public int iterations = 2;
    public int fboWidth;
    public int fboHeight;
    public Vector2 regionPos = new Vector2();
    public TextureRegion textureRegion = new TextureRegion();

	@Override
	public void reset() {
        blurBackground = false;
        matrix4.setToOrtho2D(0, 0, 0, 0);
        if (fbo1 != null) fbo1.dispose();
        fbo1 = null;
        if (fbo2 != null) fbo2.dispose();
        fbo2 = null;
        iterations = 2;
        fboWidth = 0;
        fboHeight = 0;
        regionPos.set(0, 0);
	}
}
