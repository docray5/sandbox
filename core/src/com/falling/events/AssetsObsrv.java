package com.falling.events;

import com.badlogic.ashley.core.Entity;
import com.falling.Application;

public class AssetsObsrv implements Observer {

    private final Application application;

    public AssetsObsrv(Application application) {
        this.application = application;
    }

	@Override
	public void onNotify(Entity entity, Event event) {
        switch (event) {
            case LOADED_ASSETS:
                application.onLoadedAssets();
                break;

            default:
                break;
        }
	}

    
}
