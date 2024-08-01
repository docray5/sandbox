package com.falling.commands;

import static com.falling.utils.Mappers.transformMapper;

import com.badlogic.ashley.core.Entity;

public class ScaleToCmd extends Command {
    private float scale;

    public ScaleToCmd(Entity entity, float scaleTo) {
        super(entity);
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
