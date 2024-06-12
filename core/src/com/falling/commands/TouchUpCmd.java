package com.falling.commands;

import com.badlogic.ashley.core.Engine;
import com.falling.systems.ClickableSystem;

/** TouchUp */
public class TouchUpCmd extends Command {
    private final ClickableSystem clickableSystem;

	public TouchUpCmd(Engine engine) {
		super(engine);
        clickableSystem = engine.getSystem(ClickableSystem.class);
	}

	@Override
	public void execute() {
        clickableSystem.touchUp();
	}
}
