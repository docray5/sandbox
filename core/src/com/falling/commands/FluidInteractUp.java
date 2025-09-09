package com.falling.commands;

import com.falling.Application;
import com.falling.systems.FluidSystem;

public class FluidInteractUp extends Command {
    private final FluidSystem fluidSystem;

    public FluidInteractUp() {
        fluidSystem = Application.getEngine().getSystem(FluidSystem.class);
    }

    @Override
    public void execute() {
        fluidSystem.leftMousePressed = false;
    }
}
