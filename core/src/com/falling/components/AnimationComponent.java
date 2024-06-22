package com.falling.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;

public class AnimationComponent implements Component, Poolable {

    public Array<VecAnimatorComponent> animators = new Array<>();

	@Override
	public void reset() {
        animators.clear();
	}
}
