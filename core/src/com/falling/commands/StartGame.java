package com.falling.commands;

import com.badlogic.ashley.core.Engine;
import com.falling.Application;

public class StartGame extends Command {
    private final Application application;

	public StartGame(Engine engine, Application application) {
		super(engine);
        this.application = application;
	}

	@Override
	public void execute() {
        application.startGame();
	}
}
