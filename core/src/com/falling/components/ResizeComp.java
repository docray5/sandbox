package com.falling.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class ResizeComp implements Component, Pool.Poolable {
    public STICK_TYPE stick = null;

    @Override
    public void reset() {
        stick = null;
    }

    public enum STICK_TYPE {
        LEFT_BOTTOM, RIGHT_BOTTOM, LEFT_TOP, RIGHT_TOP, LEFT, RIGHT, TOP, BOTTOM
    }
}
