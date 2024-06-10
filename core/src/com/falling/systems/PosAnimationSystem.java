package com.falling.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.systems.IteratingSystem;
import com.falling.components.PosAnimationComponent;

import static com.falling.utils.Families.posAnimationFamily;
import static com.falling.utils.Mappers.*;

public class PosAnimationSystem extends IteratingSystem {
    private PosAnimationComponent tmpAnimation;
    private boolean onX;
    private boolean onY;

    public PosAnimationSystem(int priority) {
        super(posAnimationFamily, priority);
        tmpAnimation = null;
        onX = false;
        onY = false;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        tmpAnimation = posAnimationMapper.get(entity);

        if (tmpAnimation.isAnimating) {
            tmpAnimation.pos.interpolate(tmpAnimation.target, deltaTime * tmpAnimation.accel, tmpAnimation.interpolation);
            onX = false;
            onY = false;

            if (Math.round(tmpAnimation.pos.x * tmpAnimation.rounding) / tmpAnimation.rounding == tmpAnimation.target.x) {
                tmpAnimation.pos.set(tmpAnimation.target.x, tmpAnimation.pos.y);
                onX = true;
            }

            if (Math.round(tmpAnimation.pos.y * tmpAnimation.rounding) / tmpAnimation.rounding == tmpAnimation.target.y) {
                tmpAnimation.pos.set(tmpAnimation.pos.x, tmpAnimation.target.y);
                onY = true;
            }

            if (onX && onY) {
                switch (tmpAnimation.mode) {
                    case LOOP:
                        break;
                    case NORMAL:
                        tmpAnimation.isAnimating = false;
                        break;
                    case PING_PONG:
                        tmpAnimation.target.set(tmpAnimation.pingPongPos);
                        tmpAnimation.pingPongPos.set(tmpAnimation.pos);
                        tmpAnimation.isAnimating = true;
                        break;
                }
                // Animation finished
                if (tmpAnimation.commandOnFinish != null)
                    tmpAnimation.commandOnFinish.execute();
            }
        }

        transformMapper.get(entity).pos.set(tmpAnimation.pos);
    }

    public void animateByXY(PosAnimationComponent animationComponent, float x, float y) {
        animationComponent.target.set(animationComponent.pos.x + x, animationComponent.pos.y + y);
        animationComponent.isAnimating = true;
    }

    public void animateTo(PosAnimationComponent animationComponent, float tx, float ty) {
        animationComponent.target.set(tx, ty);
        animationComponent.isAnimating = true;
    }
}
