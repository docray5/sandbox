package com.falling.events;

import com.badlogic.ashley.core.Entity;
import com.falling.Application;
import com.falling.systems.ClickableSystem;

public class ClickableObsrv implements Observer {

    private final ClickableSystem clickableSystem;

    public ClickableObsrv() {
        clickableSystem = Application.getEngine().getSystem(ClickableSystem.class);
    }

	@Override
	public void onNotify(Entity entity, Event event) {
        switch (event) {
            case TOUCH_DOWN:
                clickableSystem.touchDown();
                break;
            case TOUCH_UP:
                clickableSystem.touchUp();
                break;
            case MOUSE_MOVED:
                clickableSystem.mouseMoved();
                break;

            default:
                break;
        }
	}
}
