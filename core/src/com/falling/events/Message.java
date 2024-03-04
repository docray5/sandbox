package com.falling.events;

import com.badlogic.ashley.core.Entity;

public class Message {
    private final Entity entityToAlert;
    private final Event event;
    private final Entity entityThatAlerted;

    public Message(Entity entityToAlert, Event event, Entity entityThatAlerted) {
        this.entityToAlert = entityToAlert;
        this.event = event;
        this.entityThatAlerted = entityThatAlerted;
    }

    public Entity getEntityToAlert() {
        return entityToAlert;
    }

    public Event getEvent() {
        return event;
    }

    public Entity getEntityThatAlerted() {
        return entityThatAlerted;
    }
}
