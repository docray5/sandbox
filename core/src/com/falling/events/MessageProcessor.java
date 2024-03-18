package com.falling.events;

/**
 * Only processes messages of a specified family
 * these messages are processed in a system that operates on specified family
 */
public class MessageProcessor {

    /** call this before super.update() in your system's overridden update method */
    public void update() {
        for (int i = Messages.messages.size-1; i >= 0; i--) {
            processMessage(Messages.messages.get(i));
        }
    }

    /**
     * Override this method to assign function to your Event (yes it is empty in super)
     * return true when processed the messange, it is going to be removed for performance
     */
    public void processMessage(Message message) { }
    // TODO make it return boolean so when message is processed it is going to get removed.
}
