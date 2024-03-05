package com.falling.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class ElementComponent implements Component, Poolable {
    public int colorBits = 0;
    public ElementType elementType = null;
    public MatterType matterType = null;

	@Override
	public void reset() {
	}

    public enum ElementType {
        SAND
    }

    public enum MatterType {
        SOLID, GAS, FLUID
    }
}
