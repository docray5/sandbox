package com.falling.events;

import com.badlogic.ashley.core.Entity;
import com.falling.Application;

/**
 * SceneObserver
 */
public class SceneMgrObsrv implements Observer {

    private final Application application;

    public SceneMgrObsrv(Application application) {
        this.application = application;
    }

	@Override
	public void onNotify(Entity entity, Event event) {
        switch (event) {
            case START_GAME:
                application.startGame();
                break;
            case OPEN_MENU:
            break;
            case CLOSE_MENU:
            break;
            case PREP_MENU:
            break;

            default:
                break;
        }
	}

    
}
