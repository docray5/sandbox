package com.falling.commands;

import com.falling.Application;
import com.falling.systems.WorldSystem;

public class SpawnElementUpCmd extends Command {
    private final WorldSystem worldSystem;

    public SpawnElementUpCmd() {
        worldSystem = Application.getEngine().getSystem(WorldSystem.class);
    }

	@Override
	public void execute() {
        worldSystem.spawnElementUp();
	}
}
