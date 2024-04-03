package com.falling.commands;

import com.falling.systems.WorldSystem;
import com.badlogic.ashley.core.Engine;

public class SpawnElementUp extends Command {
    private final WorldSystem worldSystem;

    public SpawnElementUp(Engine engine) {
        super(engine);
        worldSystem = engine.getSystem(WorldSystem.class);
    }

	@Override
	public void execute() {
        worldSystem.spawnElementUp();
	}
}
