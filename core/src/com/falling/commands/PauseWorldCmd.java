package com.falling.commands;

import com.falling.Application;
import com.falling.systems.WorldSystem;

public class PauseWorldCmd extends Command {
    private final WorldSystem worldSystem;

    public PauseWorldCmd() {
        worldSystem = Application.getEngine().getSystem(WorldSystem.class);
    }

	@Override
	public void execute() {
        worldSystem.pause();
	}
}
