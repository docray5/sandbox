package com.falling.events;

import com.badlogic.ashley.core.Entity;
import com.falling.Application;
import com.falling.systems.ResizeableSystem;

/**
 * ResizeObsrv
 */
public class ResizeObsrv implements Observer {

    private final ResizeableSystem resizeableSystem;

    public ResizeObsrv() {
        resizeableSystem = Application.getEngine().getSystem(ResizeableSystem.class);
    }

	@Override
	public void onNotify(Entity entity, Event event) {
        switch (event) {
            case RESIZE:
                resizeableSystem.resize();
                break;

            default:
                break;
        }
	}

}
