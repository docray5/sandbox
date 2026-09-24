package com.falling.commands;

import static com.falling.utils.Mappers.blurMapper;

import com.badlogic.ashley.core.Entity;
import com.falling.systems.RenderSystem;
import com.falling.Application;

/**
 * ResizeBlurCompCmd
 */
public class ResizeBlurCompCmd extends Command {
    private final RenderSystem renderSystem;
    private int newWidth;
    private int newHeight;

    public ResizeBlurCompCmd(Entity entity, int newWidth, int newHeight) {
        renderSystem = Application.getEngine().getSystem(RenderSystem.class);
        this.entity = entity;
        this.newWidth = newWidth;
        this.newHeight = newHeight;
    }

	@Override
	public void execute() {
        renderSystem.resizeBlurComp(blurMapper.get(entity), newWidth, newHeight);
	}
}
