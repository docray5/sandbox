package com.falling.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.falling.commands.Command;

/**
 * This class is not supposed to be attatched to any entity on its own, use AnimationComponent class
 */
public class VecAnimatorComp implements Component, Pool.Poolable {
    public Vector2 animatedVecPointer = null;
    public Vector2 start = new Vector2();
    public Vector2 target = new Vector2();
    public float elapsed = 0;
    public float duration = 1f;
    public Interpolation interpolation = Interpolation.linear;
    public Command commandOnFinish = null;
    public AnimatorType type = null;
    /** n=-1 -> loop | n=1 -> normal | n>1 -> repeat n times | n=0 no animation */
    public int repeatTimes = 0;
    public boolean cmdOnEveryFinish = false;

    @Override
    public void reset() {
        animatedVecPointer = null;
        start.set(0, 0);
        target.set(0, 0);
        elapsed = 0;
        duration = 1f;
        interpolation = Interpolation.linear;
        commandOnFinish = null;
        type = null;
        repeatTimes = 0;
        cmdOnEveryFinish = false;
    }

    public enum AnimatorType {
        POS, SCALE, ROTATION, SIZE
    }

    // Old but did not want to delete
    /**when using pingPong initially set it to values from pos;*/
}
