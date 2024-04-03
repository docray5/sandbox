package com.falling.commands;

import com.badlogic.ashley.core.Engine;
import com.falling.systems.WorldSystem;
import com.falling.systems.WorldSystem.BrushType;

public class SelectBrush extends Command {
    private final WorldSystem worldSystem;
    private BrushType brushType;

	public SelectBrush(Engine engine) {
		super(engine);
        worldSystem = engine.getSystem(WorldSystem.class);
	}

	@Override
	public void execute() {
        if (brushType == null) return;
        worldSystem.selectBrush(brushType);
        brushType = null;
	}

    public SelectBrush setBrushType(BrushType brushType) {
        this.brushType = brushType;
        return this;
    }
}
