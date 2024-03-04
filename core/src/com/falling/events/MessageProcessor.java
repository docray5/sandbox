package com.falling.events;


import com.badlogic.ashley.core.Family;

/**
 * Only processes messages of a specified family
 * these messages are processed in a system that operates on specified family
 */
public class MessageProcessor {
    private final Family family;

    public MessageProcessor(Family family) {
        this.family = family;
    }

    /**
     * call this before super.update() in your system's overridden update method
     * messages without Event specified are not going to be executed
     */
    public void update() {
        if (!Messages.getMessagesForFamily(family).isEmpty()) {
            for (int i = 0; i < Messages.getMessagesForFamily(family).size; i++) {
                processMessage(Messages.getMessagesForFamily(family).get(i));
            }
        }
    }

    /**
     * Override this method to assign function to your Event (yes it is empty in super)
     */
    public void processMessage(Message message) {

    }
}
