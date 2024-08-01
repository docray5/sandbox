package com.falling.commands;

import com.falling.systems.WorldSystem;

public class PauseWorldCmd extends Command {
    private final WorldSystem worldSystem;

    public PauseWorldCmd() {
        worldSystem = engine.getSystem(WorldSystem.class);
    }

	@Override
	public void execute() {
        worldSystem.pause();
	}
}
