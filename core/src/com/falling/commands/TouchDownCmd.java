package com.falling.commands;

import com.falling.systems.ClickableSystem;

/** TouchDown */
public class TouchDownCmd extends Command {
    private final ClickableSystem clickableSystem;

	public TouchDownCmd() {
        clickableSystem = engine.getSystem(ClickableSystem.class);
	}

	@Override
	public void execute() {
        clickableSystem.touchDown();
	}
}
