package com.falling.events;

import com.badlogic.gdx.utils.Array;

/**
 * An EventHandler thats ready to be put in your system!
 */
public abstract class EventHandler {
    private Class<?> system;
    private Array<Event> events;

    /** this system */
    public EventHandler(Class<?> system) {
        this.system = system;
    }

    /** put this at the beggining of your update func */
    public void handleEvents() {
        events = EventQueue.instance.events.getOrDefault(system, null);
        if (events == null || events.isEmpty()) return;
        for (int i = events.size-1; i >= 0; i--) {
            processEvent(events.get(i));
        }
    }

    /** Every event directed to this system gets processed here, so implement it to your liking */
    public abstract void processEvent(Event event);
}
