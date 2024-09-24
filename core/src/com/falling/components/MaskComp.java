package com.falling.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * MaskComp
 */
public class MaskComp implements Component, Poolable {

    /**Use setRegion because this guy is not reset*/
    public TextureRegion textureRegion = new TextureRegion();
    public float originX;
    public float originY;

	@Override
	public void reset() {
        originX = 0;
        originY = 0;
	}
}
