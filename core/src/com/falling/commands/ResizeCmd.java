package com.falling.commands;

import com.falling.systems.ResizeableSystem;
import com.falling.systems.WorldSystem;

public class ResizeCmd extends Command {
    private final ResizeableSystem resizeableSystem;
    private final WorldSystem worldSystem;

    public ResizeCmd() {
        resizeableSystem = engine.getSystem(ResizeableSystem.class);
        worldSystem = engine.getSystem(WorldSystem.class);
    }

	@Override
	public void execute() {
        resizeableSystem.resize();
        worldSystem.resize();
	}
}
