package com.falling.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Pool;

public class TextureRegionComponent implements Component, Pool.Poolable {
    /**Use setRegion because this guy is not reset*/
    public TextureRegion textureRegion = new TextureRegion();
    public float originX = 0;
    public float originY = 0;

    @Override
    public void reset() {
        originX = 0;
        originY = 0;
    }
}
