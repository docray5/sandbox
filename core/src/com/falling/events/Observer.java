package com.falling.events;

import com.badlogic.ashley.core.Entity;

/**
 * Observer(Listener) Interface Reacts to events/notifications sent by Subject
 * You implement this one in your systems
 */
public interface Observer {

    void onNotify(Entity entity, Event event);
}
