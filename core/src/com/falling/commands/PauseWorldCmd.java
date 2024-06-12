package com.falling.commands;

import com.badlogic.ashley.core.Engine;
import com.falling.systems.WorldSystem;

public class PauseWorldCmd extends Command {
    private final WorldSystem worldSystem;

    public PauseWorldCmd(Engine engine) {
        super(engine);
        worldSystem = engine.getSystem(WorldSystem.class);
    }

	@Override
	public void execute() {
        worldSystem.pause();
	}
}
