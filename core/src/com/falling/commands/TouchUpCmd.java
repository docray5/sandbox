package com.falling.commands;

import com.falling.systems.ClickableSystem;

/** TouchUp */
public class TouchUpCmd extends Command {
    private final ClickableSystem clickableSystem;

	public TouchUpCmd() {
        clickableSystem = engine.getSystem(ClickableSystem.class);
	}

	@Override
	public void execute() {
        clickableSystem.touchUp();
	}
}
