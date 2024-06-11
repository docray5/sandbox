package com.falling.commands;

import static com.falling.utils.Mappers.transformMapper;

import com.badlogic.ashley.core.Engine;

public class RotateToCmd extends Command {
    private float rotateTo;
    private boolean additive;

    public RotateToCmd(Engine engine, float rotateTo, boolean additive) {
        super(engine);
        this.rotateTo = rotateTo;
        this.additive = additive;
    }

	@Override
	public void execute() {
        if (additive)
            transformMapper.get(entity).rotation += rotateTo;
        else
            transformMapper.get(entity).rotation = rotateTo;
	}

    public RotateToCmd setRotateTo(float rotateTo, boolean additive) {
        this.rotateTo = rotateTo;
        this.additive = additive;
        return this;
    }
}
