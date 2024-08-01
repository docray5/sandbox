package com.falling.commands;

import com.falling.Application;

/** Global Commands and a Wrapper class for cleaner Director class */
public class Commands {

    public static Commands instance;
    public static void setInstance(Commands _instance) { if (instance==null) instance = _instance; }

    public final StartGameCmd startGame;

    public TouchDownCmd touchDown;
    public TouchUpCmd touchUp;
    public MouseMovedCmd mouseMoved;
    public OpenMenuCmd openMenu;
    public CloseMenuCmd closeMenu;
    public PrepMenuCmd prepMenu;
    public ShiftBlurCmd shiftBlur;
    public SpawnElementDownCmd spawnElementDown;
    public SpawnElementUpCmd spawnElementUp;
    public SelectElementCmd selectElement;
    public SelectBrushCmd selectBrush;
    public ResizeCmd resize;
    public PauseWorldCmd pauseWorld;

    /** inits core commands for loading screen */
    public Commands(Application application) {
        startGame = new StartGameCmd(application);
    }

    /** init the other commands that are used with systems that need assets to be loaded */
    public void initAfterAssets() {
        touchDown = new TouchDownCmd();
        touchUp = new TouchUpCmd();
        mouseMoved = new MouseMovedCmd();
        shiftBlur = new ShiftBlurCmd();
        openMenu = new OpenMenuCmd();
        closeMenu = new CloseMenuCmd();
        prepMenu = new PrepMenuCmd();
        spawnElementDown = new SpawnElementDownCmd();
        spawnElementUp = new SpawnElementUpCmd();
        selectElement = new SelectElementCmd();
        selectBrush = new SelectBrushCmd();
        resize = new ResizeCmd();
        pauseWorld = new PauseWorldCmd();
    }
}
