package com.falling.commands;

import com.badlogic.ashley.core.Engine;
import com.falling.systems.ClickableSystem;

/** MouseMoved */
public class MouseMoved extends Command {
    private final ClickableSystem clickableSystem;

	public MouseMoved(Engine engine) {
		super(engine);
        clickableSystem = engine.getSystem(ClickableSystem.class);
	}

	@Override
	public void execute() {
        clickableSystem.mouseMoved();
	}
}
