package com.falling.commands;

import static com.falling.utils.Mappers.renderableMapper;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;

public class SetColorCmd extends Command {
    private Color color;
    private boolean additive;

    public SetColorCmd(Entity entity, float r, float g, float b, float a, boolean additive) {
        super(entity);
        color = new Color(r, g, b, a);
        this.additive = additive;
    }

    public SetColorCmd(Color color) {
        this.color = new Color(color);
    }

	@Override
	public void execute() {
        if (additive)
            renderableMapper.get(entity).color.add(color);
        else
            renderableMapper.get(entity).color.set(color);
	}

    public SetColorCmd setColor(Color color, boolean additive) {
        this.color.set(color);
        this.additive = additive;
        return this;
    }


    public SetColorCmd setColor(float r, float g, float b, float a, boolean additive) {
        color.set(r, g, b, a);
        this.additive = additive;
        return this;
    }
}
