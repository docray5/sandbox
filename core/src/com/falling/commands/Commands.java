package com.falling.commands;

/** Global Commands and a Wrapper class for cleaner Director class */
public class Commands {

    public static Commands instance;
    public static void setInstance(Commands _instance) { if (instance==null) instance = _instance; }

    public ShiftBlurCmd shiftBlur;
    public SpawnElementDownCmd spawnElementDown;
    public SpawnElementUpCmd spawnElementUp;
    public PauseWorldCmd pauseWorld;
    public FluidInteractDown fluidInteractDown;
    public FluidInteractUp fluidInteractUp;
    public GravityCmd gravityCmd;

    /** inits core commands for loading screen */
    public Commands() {
    }

    /** init the other commands that are used with systems that need assets to be loaded */
    public void initAfterAssets() {
        shiftBlur = new ShiftBlurCmd();
        spawnElementDown = new SpawnElementDownCmd();
        spawnElementUp = new SpawnElementUpCmd();
        pauseWorld = new PauseWorldCmd();
        fluidInteractDown = new FluidInteractDown();
        fluidInteractUp = new FluidInteractUp();
        gravityCmd = new GravityCmd();
    }
}
