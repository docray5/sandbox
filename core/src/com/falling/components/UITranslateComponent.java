package com.falling.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

public class UITranslateComponent implements Component, Pool.Poolable {
    public Vector2 openPos = new Vector2();
    public Vector2 closePos = new Vector2();

    @Override
    public void reset() {
        openPos.set(0, 0);
        closePos.set(0, 0);
    }
}
