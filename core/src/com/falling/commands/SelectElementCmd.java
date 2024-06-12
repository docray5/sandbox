package com.falling.commands;

import com.badlogic.ashley.core.Engine;
import com.falling.components.ElementComponent.ElementType;
import com.falling.systems.WorldSystem;

public class SelectElementCmd extends Command {
    private final WorldSystem worldSystem;
    private ElementType elementType;

	public SelectElementCmd(Engine engine) {
		super(engine);
        worldSystem = engine.getSystem(WorldSystem.class);
	}

	@Override
	public void execute() {
        worldSystem.selectElement(elementType);
	}

    // returns this for chaining
    public SelectElementCmd setElementType(ElementType elementType) {
        this.elementType = elementType;
        return this;
    }
}
