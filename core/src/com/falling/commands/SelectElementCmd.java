package com.falling.commands;

import com.falling.Application;
import com.falling.components.ElementComp.ElementType;
import com.falling.systems.WorldSystem;

public class SelectElementCmd extends Command {
    private final WorldSystem worldSystem;
    private ElementType elementType;

	public SelectElementCmd(ElementType elementType) {
        worldSystem = Application.getEngine().getSystem(WorldSystem.class);
        this.elementType = elementType;
	}

	@Override
	public void execute() {
        worldSystem.selectElement(elementType);
	}
}
