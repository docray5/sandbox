package com.falling.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.falling.commands.Command;

/**
 * This class is not supposed to be attatched to any entity on its own, use AnimationComponent class
 */
public class VecAnimatorComponent implements Component, Pool.Poolable {
    public Vector2 animatedVecPointer = null;
    public Vector2 start = new Vector2();
    public Vector2 target = new Vector2();
    public float elapsed = 0;
    public float duration = 1f;
    public Interpolation interpolation = Interpolation.linear;
    public boolean isAnimating = false;
    public Command commandOnFinish = null;
    public AnimatorType type = null;

    @Override
    public void reset() {
        animatedVecPointer = null;
        start.set(0, 0);
        target.set(0, 0);
        elapsed = 0;
        duration = 1f;
        interpolation = Interpolation.linear;
        isAnimating = false;
        commandOnFinish = null;
        type = null;
    }

    public enum AnimatorType {
        POS, SCALE, ROTATION
    }

    // Old but did not want to delete
    /**when using pingPong initially set it to values from pos;*/
}
