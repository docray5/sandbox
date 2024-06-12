package com.falling.commands;

import com.badlogic.ashley.core.Engine;
import com.falling.systems.RenderSystem;

/**
 * ShiftBlur
 */
public class ShiftBlurCmd extends Command {
    private final RenderSystem renderSystem;

    public ShiftBlurCmd(Engine engine) {
        super(engine);
        renderSystem = engine.getSystem(RenderSystem.class);
    }

	@Override
	public void execute() {
        renderSystem.shiftBlur();
	} 
}
