package com.falling.commands;

import com.falling.Application;
import com.falling.systems.WorldSystem;
import com.falling.systems.WorldSystem.BrushType;

public class SelectBrushCmd extends Command {
    private final WorldSystem worldSystem;
    private final BrushType brushType;

	public SelectBrushCmd(BrushType brushType) {
        worldSystem = Application.getEngine().getSystem(WorldSystem.class);
        this.brushType = brushType;
	}

	@Override
	public void execute() {
        worldSystem.selectBrush(brushType);
	}
}
