package com.falling.commands;

import com.badlogic.ashley.core.Engine;

public abstract class Command {
    protected Engine engine;

    public Command(Engine engine) {
        this.engine = engine;
    }

    public abstract void execute(); // call the reciever.
}
