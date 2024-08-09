package com.falling.events;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;

/** Publisher/Subject holds observers and sends notifications to them
 * This is for example your Physics system sends a notification ENTITY_FELL
 * and Subject's observers will be notified about the event and they will react */
public class Publisher {

    private Array<Observer> observers;

    public Publisher() {
        observers = new Array<>();
    }

    public void addObservers(Observer... observers) {
        this.observers.addAll(observers);
    }

    public void removeObserver(Observer observer) {
        observers.removeValue(observer, true);
    }

    public void notify(Entity entity, Event event) {
        for (int i = observers.size-1; i >= 0; i--) {
            observers.get(i).onNotify(entity, event);
        }
    }
}
