package com.falling.commands;

import com.badlogic.ashley.core.Engine;
import com.falling.Application;

/** Global Commands and a Wrapper class for cleaner Director class */
public class Commands {

    public static Commands instance;
    public static void setInstance(Commands _instance) { instance = _instance; }

    public final StartGame startGame;

    public TouchDown touchDown;
    public TouchUp touchUp;
    public MouseMoved mouseMoved;
    public OpenMenu openMenu;
    public CloseMenu closeMenu;
    public PrepMenu prepMenu;
    public ShiftBlur shiftBlur;
    public SpawnElementDown spawnElementDown;
    public SpawnElementUp spawnElementUp;
    public SelectElement selectElement;
    public SelectBrush selectBrush;
    public Resize resize;
    public PauseWorld pauseWorld;

    /** inits core commands for loading screen */
    public Commands(Engine engine, Application application) {
        startGame = new StartGame(engine, application);
    }

    /** init the other commands that are used with systems that need assets to be loaded */
    public void initAfterAssets(Engine engine) {
        touchDown = new TouchDown(engine);
        touchUp = new TouchUp(engine);
        mouseMoved = new MouseMoved(engine);
        shiftBlur = new ShiftBlur(engine);
        openMenu = new OpenMenu(engine);
        closeMenu = new CloseMenu(engine);
        prepMenu = new PrepMenu(engine);
        spawnElementDown = new SpawnElementDown(engine);
        spawnElementUp = new SpawnElementUp(engine);
        selectElement = new SelectElement(engine);
        selectBrush = new SelectBrush(engine);
        resize = new Resize(engine);
        pauseWorld = new PauseWorld(engine);
    }
}
