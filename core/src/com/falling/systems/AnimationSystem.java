package com.falling.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.systems.IteratingSystem;
import com.falling.components.AnimationComponent;
import com.falling.events.Messages;

import static com.falling.utils.Families.animationFamily;
import static com.falling.utils.Mappers.*;

public class AnimationSystem extends IteratingSystem {
    private AnimationComponent tmpAnimation;
    private boolean onX;
    private boolean onY;

    public AnimationSystem(int priority) {
        super(animationFamily, priority);
        tmpAnimation = null;
        onX = false;
        onY = false;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        tmpAnimation = animationMapper.get(entity);

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
                    case CHAIN:
                        if (tmpAnimation.chain.size > 0 && tmpAnimation.iChain < tmpAnimation.chain.size) {
                            tmpAnimation.target.set(tmpAnimation.chain.get(tmpAnimation.iChain));
                            tmpAnimation.iChain++;
                            if (tmpAnimation.iChain >= tmpAnimation.chain.size || tmpAnimation.iChain < 0) {
                                tmpAnimation.isAnimating = false;
                                tmpAnimation.iChain = 0;
                            }
                        }
                        break;
                    case CHAIN_PING_PONG:
                        if (tmpAnimation.chain.size > 0 && tmpAnimation.iChain < tmpAnimation.chain.size) {
                            tmpAnimation.target.set(tmpAnimation.chain.get(tmpAnimation.iChain));
                            if (tmpAnimation.up) tmpAnimation.iChain--;
                            else tmpAnimation.iChain++;
                            if (tmpAnimation.iChain >= tmpAnimation.chain.size || tmpAnimation.iChain < 0) {
                                tmpAnimation.up = !tmpAnimation.up;
                            }
                        }
                        break;
                }
                // Animation finished
                Messages.alert(tmpAnimation.entityToAlert, tmpAnimation.eventOnFinish, entity);
            }
        }

        transformMapper.get(entity).pos.set(tmpAnimation.pos);
    }
}
