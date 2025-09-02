package com.falling.commands;

import com.falling.Application;
import com.falling.systems.FluidSystem;

public class LeftFluidCmd extends Command {
    private final FluidSystem fluidSystem;

    public LeftFluidCmd() {
        fluidSystem = Application.getEngine().getSystem(FluidSystem.class);
    }

    @Override
    public void execute() {
        fluidSystem.leftFluid();
    }
}
