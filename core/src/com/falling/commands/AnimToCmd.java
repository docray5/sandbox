package com.falling.commands;

import static com.falling.utils.Mappers.animationMapper;

import com.badlogic.ashley.core.Entity;
import com.falling.components.VecAnimatorComp.AnimatorType;
import com.falling.systems.AnimationSystem;
import com.falling.Application;

/**
 * Animates entity to new Position
 */
public class AnimToCmd extends Command {
    private final AnimationSystem posAnimationSystem;
    private AnimatorType animatorType;
    private float animToX;
    private float animToY;
    private boolean additive;
    private int repeatTimes;

    /**
     * @param additive determines whether to add the x and y to current positon or move to x and y
     */
    public AnimToCmd(Entity entity, AnimatorType animatorType, float animToX, float animToY, boolean additive) {
        this(entity, animatorType, animToX, animToY, additive, 1);
    }

    /**
     * @param additive determines whether to add the x and y to current positon or move to x and y
     * @param repeatTimes n=-1 -> loop | n=1 -> normal | n>1 -> repeat n times | n=0 no animation
     */
    public AnimToCmd(Entity entity, AnimatorType animatorType, float animToX, float animToY, boolean additive, int repeatTimes) {
        super(entity);
        posAnimationSystem = Application.getEngine().getSystem(AnimationSystem.class);
        this.animatorType = animatorType;
        this.animToX = animToX;
        this.animToY = animToY;
        this.additive = additive;
        this.repeatTimes = repeatTimes;
    }

	@Override
	public void execute() {
        posAnimationSystem.animateTo(animationMapper.get(entity), animatorType, animToX, animToY, additive, repeatTimes);
	}

    public void setAnimTo(AnimatorType animatorType, float animToX, float animToY, boolean additive) {
        setAnimTo(animatorType, animToX, animToY, additive, 1);
    }

    /** n=-1 -> loop | n=1 -> normal | n>1 -> repeat n times | n=0 no animation */
    public void setAnimTo(AnimatorType animatorType, float animToX, float animToY, boolean additive, int repeatTimes) {
        this.animatorType = animatorType;
        this.animToX = animToX;
        this.animToY = animToY;
        this.additive = additive;
        this.repeatTimes = repeatTimes;
    }
}
