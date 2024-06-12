package com.falling.commands;

import com.badlogic.ashley.core.Engine;
import com.falling.systems.ClickableSystem;

/** TouchDown */
public class TouchDownCmd extends Command {
    private final ClickableSystem clickableSystem;

	public TouchDownCmd(Engine engine) {
		super(engine);
        clickableSystem = engine.getSystem(ClickableSystem.class);
	}

	@Override
	public void execute() {
        clickableSystem.touchDown();
	}
}
