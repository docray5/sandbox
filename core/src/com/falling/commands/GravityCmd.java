package com.falling.commands;

import com.falling.Application;
import com.falling.systems.FluidSystem;

public class GravityCmd extends Command {

    private final FluidSystem fluidSystem;

    public GravityCmd() {
        fluidSystem = Application.getEngine().getSystem(FluidSystem.class);
    }

    @Override
    public void execute() {
        fluidSystem.gravity();
    }
}
