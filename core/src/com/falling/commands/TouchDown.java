package com.falling.commands;

import com.badlogic.ashley.core.Engine;
import com.falling.systems.ButtonSystem;

/** TouchDown */
public class TouchDown extends Command {
    private final ButtonSystem buttonSystem;

	public TouchDown(Engine engine) {
		super(engine);
        buttonSystem = engine.getSystem(ButtonSystem.class);
	}

	@Override
	public void execute() {
        buttonSystem.touchDown();
	}
}
