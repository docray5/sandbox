package com.falling.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class ResizeComponent implements Component, Pool.Poolable {
    public STICK_TYPE stick = null;
    public float lastLRGutter = 0;
    public float lastTBGutter = 0;

    @Override
    public void reset() {
        stick = null;
        lastLRGutter = 0;
        lastTBGutter = 0;
    }

    public enum STICK_TYPE {
        LEFT_BOTTOM, RIGHT_BOTTOM, LEFT_TOP, RIGHT_TOP, LEFT, RIGHT, TOP, BOTTOM
    }
}
