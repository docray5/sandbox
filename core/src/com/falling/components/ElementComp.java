package com.falling.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class ElementComp implements Component, Poolable {
    public int colorBits = 0;
    public ElementType elementType = null;
    public MatterType matterType = null;
    public float maxSpeed = 8;
    public float acceleration = 0.4f; // like a gravity multiplied by mass
    public float velocity = 0;
    public int spread = 0;

	@Override
	public void reset() {
        colorBits = 0;
        elementType = null;
        matterType = null;
        maxSpeed = 8;
        acceleration = 0.4f;
        velocity = 0;
        spread = 0;
	}

    public enum ElementType {
        SAND, WATER, WOOD
    }

    public enum MatterType {
        SOLID, GAS, FLUID, POWDER
    }
}
