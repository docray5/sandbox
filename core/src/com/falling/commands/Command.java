package com.falling.commands;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;

public abstract class Command {
    protected Engine engine;
    protected Entity entity;

    public Command(Engine engine) {
        this.engine = engine;
    }

    public abstract void execute(); // call the reciever.

    /**
     * @param entity optional call for you to set an Entity that called the command for later use
     */
    public Command setEntity(Entity entity) {
        this.entity = entity;
        return this;
    }
}
