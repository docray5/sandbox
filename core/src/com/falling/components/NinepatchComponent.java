package com.falling.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;

public class NinepatchComponent implements Component, Poolable {
    public NinePatch ninePatch = null;
    public Vector2 size = new Vector2();

	@Override
	public void reset() {
        ninePatch = null;
        size.set(0, 0);
	}
}
