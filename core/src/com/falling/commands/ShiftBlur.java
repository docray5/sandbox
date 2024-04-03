package com.falling.commands;

import com.badlogic.ashley.core.Engine;
import com.falling.systems.RenderSystem;

/**
 * ShiftBlur
 */
public class ShiftBlur extends Command {
    private final RenderSystem renderSystem;

    public ShiftBlur(Engine engine) {
        super(engine);
        renderSystem = engine.getSystem(RenderSystem.class);
    }

	@Override
	public void execute() {
        renderSystem.shiftBlur();
	} 
}
