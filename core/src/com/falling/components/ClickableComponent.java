package com.falling.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Pool;

public class ClickableComponent implements Component, Pool.Poolable {
    public Rectangle hitBox = new Rectangle();
    public boolean clicked = false;
    public boolean clickable = true;
    public boolean hovered = false;


    @Override
    public void reset() {
        hitBox.set(0, 0, 0, 0);
        clicked = false;
        clickable = true;
        hovered = false;
    }
}
