package com.falling.events;

import com.falling.Application;

/**
 * Observers
 */
public class Observers {

    public static void setInstance(Observers observers) { instance = observers; }
    public static Observers instance;

    public AssetsObsrv assetsObsrv;
    public ResizeObsrv resizeObsrv;

    public ClickableObsrv clickableObsrv;

    public Observers(Application application) {
        assetsObsrv = new AssetsObsrv(application);
        resizeObsrv = new ResizeObsrv();
    }

    public void initaAfterAssets() {
        clickableObsrv = new ClickableObsrv();
    }
}
