package com.falling.events;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;

import static com.falling.utils.Families.families;

public class Messages {
    private static HashMap<Family, Array<Message>> messages;

    public static void init() {
        messages = new HashMap<>();
        for (Family fam : families) {
            messages.put(fam, new Array<Message>());
        }
    }

    /**
     * @param familyToAlert (NOT NULL) This Identifies which system to alert
     * @param entityToAlert This Identifies which entity of that system to alert
     * @param data This Identifies the entity that called the alert method, use it to pass data
     *                          you can pass null here if you want but this serves for accessing needed data
     * @param event (NOT NULL) messages without event specified are not going to be processed
     * all the messages are delete at the end of each frame
     */
    public static void alert(Entity entityToAlert,  Event event, Family familyToAlert, Entity data) {
        if (familyToAlert == null || event == null) return;
        messages.get(familyToAlert).add(new Message(entityToAlert, event, data));
    }

    /**
     * convenience method for alerting systems that don't operate on entities.
     * @param familyToAlert (NOT NULL) This Identifies which system to alert
     * @param event (NOT NULL) messages without event specified are not going to be processed
     * all the messages are delete at the end of each frame
     */
    public static void alert(Event event, Family familyToAlert) {
        alert(null, event, familyToAlert, null);
    }

    /** @return Array of messages to be processed by system with that is described by this family param*/
    protected static Array<Message> getMessagesForFamily(Family family) {
        return messages.get(family);
    }

    /**called at the end of each frame*/
    public static void clear() {
        for (Array<Message> arr : messages.values()) {
            arr.clear();
        }
    }
}
