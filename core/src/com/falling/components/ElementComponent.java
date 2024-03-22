package com.falling.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class ElementComponent implements Component, Poolable {
    public int colorBits = 0;
    public ElementType elementType = null;
    public MatterType matterType = null;

	@Override
	public void reset() {
        colorBits = 0;
        elementType = null;
        matterType = null;
	}

    public enum ElementType {
        SAND, WATER, WOOD
    }

    public enum MatterType {
        SOLID, GAS, FLUID
    }
}
