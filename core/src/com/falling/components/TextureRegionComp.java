package com.falling.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Pool;

public class TextureRegionComp implements Component, Pool.Poolable {
    /**Use setRegion because this guy is not reset*/
    public TextureRegion textureRegion = new TextureRegion();
    public int width = 0; // helper for width as it's changed only when you change your texture plus you get more control
    public int height = 0; // same as above
    /** pivot point for rotation */
    public float originX = 0;
    /** pivot point for rotation */
    public float originY = 0;

    @Override
    public void reset() {
        originX = 0;
        originY = 0;
        width = 0;
        height = 0;
    }
}
