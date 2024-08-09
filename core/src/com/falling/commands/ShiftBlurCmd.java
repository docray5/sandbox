package com.falling.commands;

import com.falling.Application;
import com.falling.systems.RenderSystem;

/**
 * ShiftBlur
 */
public class ShiftBlurCmd extends Command {
    private final RenderSystem renderSystem;

    public ShiftBlurCmd() {
        renderSystem = Application.getEngine().getSystem(RenderSystem.class);
    }

	@Override
	public void execute() {
        renderSystem.shiftBlur();
	} 
}
