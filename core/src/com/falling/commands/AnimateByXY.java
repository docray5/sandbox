package com.falling.commands;

import static com.falling.utils.Mappers.posAnimationMapper;

import com.badlogic.ashley.core.Engine;
import com.falling.systems.PosAnimationSystem;

public class AnimateByXY extends Command {

    private final PosAnimationSystem posAnimationSystem;
    private float x;
    private float y;

	public AnimateByXY(Engine engine, float x, float y) {
		super(engine);
        posAnimationSystem = engine.getSystem(PosAnimationSystem.class);
        this.x = x;
        this.y = y;
	}

	@Override
	public void execute() {
        posAnimationSystem.animateByXY(posAnimationMapper.get(entity), x, y);
	}

    public Command setXY(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }
}
