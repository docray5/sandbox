package com.falling.commands;

import com.falling.Application;
import com.falling.systems.WorldSystem;

public class SpawnElementDownCmd extends Command {
    private final WorldSystem worldSystem;

    public SpawnElementDownCmd() {
        worldSystem = Application.getEngine().getSystem(WorldSystem.class);
    }

	@Override
	public void execute() {
        worldSystem.spawnElementDown();
	}
}
