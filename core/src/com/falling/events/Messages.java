package com.falling.events;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;

public class Messages {
    protected static Array<Message> messages;

    public static void init() {
        messages = new Array<>();
    }

    /**
     * @param entityToAlert This Identifies which entity of that system to alert
     * @param data This Identifies the entity that called the alert method, use it to pass data
     *                          you can pass null here if you want but this serves for accessing needed data
     * @param event (NOT NULL) messages without event specified are not going to be processed
     * all the messages are delete at the end of each frame
     */
    public static void alert(Entity entityToAlert, Event event, Entity data) {
        if (event == null) return;
        messages.add(new Message(entityToAlert, event, data));
        // TODO optimize this i.e make a pool of messages, use the entity system for that too.
    }

    /**
     * convenience method for alerting systems that don't operate on entities.
     * @param familyToAlert (NOT NULL) This Identifies which system to alert
     * @param event (NOT NULL) messages without event specified are not going to be processed
     * all the messages are delete at the end of each frame
     */
    public static void alert(Event event) {
        alert(null, event, null);
    }

    /**called at the end of each frame*/
    public static void clear() {
        messages.clear();
    }
}
