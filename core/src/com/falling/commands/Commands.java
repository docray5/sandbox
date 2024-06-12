package com.falling.commands;

import com.badlogic.ashley.core.Engine;
import com.falling.Application;

/** Global Commands and a Wrapper class for cleaner Director class */
public class Commands {

    public static Commands instance;
    public static void setInstance(Commands _instance) { instance = _instance; }

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
    public Commands(Engine engine, Application application) {
        startGame = new StartGameCmd(engine, application);
    }

    /** init the other commands that are used with systems that need assets to be loaded */
    public void initAfterAssets(Engine engine) {
        touchDown = new TouchDownCmd(engine);
        touchUp = new TouchUpCmd(engine);
        mouseMoved = new MouseMovedCmd(engine);
        shiftBlur = new ShiftBlurCmd(engine);
        openMenu = new OpenMenuCmd(engine);
        closeMenu = new CloseMenuCmd(engine);
        prepMenu = new PrepMenuCmd(engine);
        spawnElementDown = new SpawnElementDownCmd(engine);
        spawnElementUp = new SpawnElementUpCmd(engine);
        selectElement = new SelectElementCmd(engine);
        selectBrush = new SelectBrushCmd(engine);
        resize = new ResizeCmd(engine);
        pauseWorld = new PauseWorldCmd(engine);
    }
}
