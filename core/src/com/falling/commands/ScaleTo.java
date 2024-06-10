package com.falling.commands;

import static com.falling.utils.Mappers.transformMapper;

import com.badlogic.ashley.core.Engine;

public class ScaleTo extends Command {
    private float scale;

    public ScaleTo(Engine engine, float scaleTo) {
        super(engine);
        scale = scaleTo;
    }

	@Override
	public void execute() {
        transformMapper.get(entity).scale.set(scale, scale);
	}

    public Command SetScaleTo(float scaleTo) {
        scale = scaleTo;
        return this;
    }
}
