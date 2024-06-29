package com.falling.events;

/**
 * Event pojo abstract Class
 */
public abstract class Event {

    /** the system that this event is going to be sent to */
    private Class<?> system;

    /** the system that this event is going to be sent to */
    public Event(Class<?> system) {
        this.system = system;
    }

    /** the system that this event is going to be sent to */
    public Class<?> getSystem() {
        return system;
    }
}
