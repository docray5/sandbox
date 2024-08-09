package com.falling.events;

import com.falling.Application;

/**
 * Observers
 */
public class Observers {

    public static void setInstance(Observers observers) { instance = observers; }
    public static Observers instance;

    public SceneMgrObsrv sceneMgrObsrv;
    public ResizeObsrv resizeObsrv;

    public ClickableObsrv clickableObsrv;

    public Observers(Application application) {
        sceneMgrObsrv = new SceneMgrObsrv(application);
        resizeObsrv = new ResizeObsrv();
    }

    public void initaAfterAssets() {
        clickableObsrv = new ClickableObsrv();
    }
}
