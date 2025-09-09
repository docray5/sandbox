package com.falling.commands;

import com.falling.Application;
import com.falling.systems.FluidSystem;

public class FluidInteractDown extends Command {
    private final FluidSystem fluidSystem;

    public FluidInteractDown() {
        fluidSystem = Application.getEngine().getSystem(FluidSystem.class);
    }

    @Override
    public void execute() {
        fluidSystem.leftMousePressed = true;
    }
}
