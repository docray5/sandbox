package com.falling.commands;

import com.badlogic.ashley.core.Engine;
import com.falling.systems.ButtonSystem;

/** MouseMoved */
public class MouseMoved extends Command {
    private final ButtonSystem buttonSystem;

	public MouseMoved(Engine engine) {
		super(engine);
        buttonSystem = engine.getSystem(ButtonSystem.class);
	}

	@Override
	public void execute() {
        buttonSystem.mouseMoved();
	}
}
