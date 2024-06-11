package com.falling.commands;

import static com.falling.utils.Mappers.posAnimationMapper;

import com.badlogic.ashley.core.Engine;
import com.falling.systems.PosAnimationSystem;

/**
 * Animates entity to new Position
 */
public class PosAnimToCmd extends Command {
    private final PosAnimationSystem posAnimationSystem;
    private float animToX;
    private float animToY;
    private boolean additive;

    /**
     * @param additive determines whether to add the x and y to current positon or move to x and y
     */
    public PosAnimToCmd(Engine engine, float animToX, float animToY, boolean additive) {
        super(engine);
        posAnimationSystem = engine.getSystem(PosAnimationSystem.class);
        this.animToX = animToX;
        this.animToY = animToY;
        this.additive = additive;
    }

	@Override
	public void execute() {
        if (additive)
            posAnimationSystem.animateBy(posAnimationMapper.get(entity), animToX, animToY);
        else
            posAnimationSystem.animateTo(posAnimationMapper.get(entity), animToX, animToY);
	}

    public void setAnimTo(float animToX, float animToY, boolean additive) {
        this.animToX = animToX;
        this.animToY = animToY;
        this.additive = additive;
    }
}
