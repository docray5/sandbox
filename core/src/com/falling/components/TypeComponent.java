package com.falling.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

/**
 * Type component for unique cases that need different type of processing
 */
public class TypeComponent implements Component, Pool.Poolable {
    public Type type = null;

    @Override
    public void reset() {
        type = null;
    }

    public enum Type {
        FULLSCREEN_CLICK
    }
}
