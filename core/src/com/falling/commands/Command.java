package com.falling.commands;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.falling.Application;

public abstract class Command {
    protected Engine engine; // Useless ram usage... TODO
    protected Entity entity;

    public Command() {
        this.engine = Application.getEngine();
    }

    /**
     * @param entity optional call for you to set an Entity that called the command for later use
     */
    public Command(Entity entity) {
        this.engine = Application.getEngine();
        this.entity = entity;
    }


    public abstract void execute(); // call the reciever.
}
