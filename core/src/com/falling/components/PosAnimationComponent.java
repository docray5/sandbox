package com.falling.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.falling.commands.Command;

public class PosAnimationComponent implements Component, Pool.Poolable {
    public Vector2 pos = new Vector2();
    public Vector2 target = new Vector2();
    public float accel = 8;
    public Interpolation interpolation = Interpolation.linear;
    public boolean isAnimating = false;
    public float rounding = 100.0f;
    public Command commandOnFinish = null;
    public AnimationMode mode = AnimationMode.NORMAL;
    /**when using pingPong initially set it to values from pos;*/
    public Vector2 pingPongPos = new Vector2();
    public boolean up = false;

    @Override
    public void reset() {
        pos.set(0, 0);
        target.set(0, 0);
        accel = 8;
        interpolation = Interpolation.linear;
        isAnimating = false;
        rounding = 100.0f;
        commandOnFinish = null;
        mode = AnimationMode.NORMAL;
        pingPongPos.set(0, 0);
        up = false;
    }

    public enum AnimationMode {
        PING_PONG, NORMAL, LOOP
    }
}
