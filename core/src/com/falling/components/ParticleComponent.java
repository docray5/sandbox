package com.falling.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Pool;

public class ParticleComponent implements Component, Pool.Poolable {
    public float lifeTime = 1;
    public float passed = 0;
    public float colorAccel = 1;
    public Color targetColor = new Color(1, 1, 1, 1);

    @Override
    public void reset() {
        lifeTime = 1;
        passed = 0;
        targetColor.set(1, 1,1 ,1);
        colorAccel = 1;
    }
}
