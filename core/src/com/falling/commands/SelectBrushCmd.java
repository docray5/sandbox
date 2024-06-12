package com.falling.commands;

import com.badlogic.ashley.core.Engine;
import com.falling.systems.WorldSystem;
import com.falling.systems.WorldSystem.BrushType;

public class SelectBrushCmd extends Command {
    private final WorldSystem worldSystem;
    private BrushType brushType;

	public SelectBrushCmd(Engine engine) {
		super(engine);
        worldSystem = engine.getSystem(WorldSystem.class);
	}

	@Override
	public void execute() {
        if (brushType == null) return;
        worldSystem.selectBrush(brushType);
        brushType = null;
	}

    public SelectBrushCmd setBrushType(BrushType brushType) {
        this.brushType = brushType;
        return this;
    }
}
