package com.falling.commands;

import com.badlogic.ashley.core.Engine;
import com.falling.systems.ClickableSystem;

/** TouchUp */
public class TouchUp extends Command {
    private final ClickableSystem clickableSystem;

	public TouchUp(Engine engine) {
		super(engine);
        clickableSystem = engine.getSystem(ClickableSystem.class);
	}

	@Override
	public void execute() {
        clickableSystem.touchUp();
	}
}
