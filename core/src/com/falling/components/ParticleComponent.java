package com.falling.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class ParticleComponent implements Component, Poolable {
    public int colorBits = 0;
    public ParticleType particleType = null;
    public MatterType matterType = null;

	@Override
	public void reset() {
        colorBits = 0;
        particleType = null;
        matterType = null;
	}

    public enum ParticleType {
        SAND, WATER, WOOD
    }

    public enum MatterType {
        SOLID, GAS, FLUID
    }
}
