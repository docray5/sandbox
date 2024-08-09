package com.falling.commands;

import com.badlogic.ashley.core.Entity;

public abstract class Command {
    protected Entity entity;

    public Command() {
    }

    /**
     * @param entity optional call for you to set an Entity that called the command for later use
     */
    public Command(Entity entity) {
        this.entity = entity;
    }

    public abstract void execute(); // call the reciever.
}
