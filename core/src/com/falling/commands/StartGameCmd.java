package com.falling.commands;

import com.falling.Application;

public class StartGameCmd extends Command {
    private final Application application;

	public StartGameCmd(Application application) {
        this.application = application;
	}

	@Override
	public void execute() {
        application.startGame();
	}
}
