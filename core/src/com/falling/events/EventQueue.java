package com.falling.events;

import java.util.HashMap;

import com.badlogic.gdx.utils.Array;

/**
 * EventQueue
 */
public class EventQueue {

    public static EventQueue instance;
    public static void setInstance() { if (instance==null) instance = new EventQueue(); }

    protected HashMap<Class<?>, Array<Event>> events;

    public EventQueue() {
        events = new HashMap<>();
    }

    public void sendEvent(Event event) {
        events.putIfAbsent(event.getSystem(), new Array<>());
        events.get(event.getSystem()).add(event);
    }

    public void clearEvents() {
        for (Class<?> system : events.keySet())
            events.get(system).clear();
    }
}
