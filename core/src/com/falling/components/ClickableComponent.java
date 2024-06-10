package com.falling.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Pool;
import com.falling.commands.Command;

public class ClickableComponent implements Component, Pool.Poolable {
    public Rectangle hitBox = new Rectangle();
    public boolean clicked = false;
    public boolean clickable = true;
    public boolean hovered = false;

    public Command onTouchUpCommand = null;
    public Command onTouchDownCommand = null;
    public Command onClickCommand = null;
    public Command onMouseOverCommand = null;
    public Command onMouseOffCommand = null;
    // * Lower gets clicked first *//
    public int priority = 0;
    public int vibrationMs = 0;


    @Override
    public void reset() {
        hitBox.set(0, 0, 0, 0);
        clicked = false;
        clickable = true;
        hovered = false;
        onTouchUpCommand = null;
        onTouchDownCommand = null;
        onClickCommand = null;
        onMouseOverCommand = null;
        onMouseOffCommand = null;
        priority = 0;
        vibrationMs = 0;
    }
}
