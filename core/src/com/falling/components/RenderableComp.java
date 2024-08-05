package com.falling.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Pool;

public class RenderableComp implements Component, Pool.Poolable {
    public Color color = new Color(1, 1, 1, 1);
    /**lowest will be drawn first*/
    public int priority = 0;
    public boolean render = true;
    public boolean afterVfx = false;
    public boolean center = false;

    @Override
    public void reset() {
        color.set(1, 1, 1, 1);
        priority = 0;
        render = true;
        afterVfx = false;
    }
}
