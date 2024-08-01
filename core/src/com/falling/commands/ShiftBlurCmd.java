package com.falling.commands;

import com.falling.systems.RenderSystem;

/**
 * ShiftBlur
 */
public class ShiftBlurCmd extends Command {
    private final RenderSystem renderSystem;

    public ShiftBlurCmd() {
        renderSystem = engine.getSystem(RenderSystem.class);
    }

	@Override
	public void execute() {
        renderSystem.shiftBlur();
	} 
}
