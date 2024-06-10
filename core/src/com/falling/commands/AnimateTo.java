package com.falling.commands;

import static com.falling.utils.Mappers.posAnimationMapper;

import com.badlogic.ashley.core.Engine;
import com.falling.systems.PosAnimationSystem;

public class AnimateTo extends Command {
    private final PosAnimationSystem posAnimationSystem;
    private float animToX;
    private float animToY;

    public AnimateTo(Engine engine, float x, float y) {
        super(engine);
        posAnimationSystem = engine.getSystem(PosAnimationSystem.class);
        animToX = x;
        animToY = y;
    }

	@Override
	public void execute() {
        posAnimationSystem.animateTo(posAnimationMapper.get(entity), animToX, animToY);
	}

    public void setXY(float x, float y) {
        animToX = x;
        animToY = y;
    }
}
