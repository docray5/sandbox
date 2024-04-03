package com.falling.commands;

import com.badlogic.ashley.core.Engine;
import com.falling.systems.ButtonSystem;

/** TouchUp */
public class TouchUp extends Command {
    private final ButtonSystem buttonSystem;

	public TouchUp(Engine engine) {
		super(engine);
        buttonSystem = engine.getSystem(ButtonSystem.class);
	}

	@Override
	public void execute() {
        buttonSystem.touchUp();
	}
}
