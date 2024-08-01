package com.falling.commands;

import com.falling.systems.ClickableSystem;

/** MouseMoved */
public class MouseMovedCmd extends Command {
    private final ClickableSystem clickableSystem;

	public MouseMovedCmd() {
        clickableSystem = engine.getSystem(ClickableSystem.class);
	}

	@Override
	public void execute() {
        clickableSystem.mouseMoved();
	}
}
