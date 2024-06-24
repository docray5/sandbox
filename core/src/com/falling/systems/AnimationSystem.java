package com.falling.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.falling.components.AnimationComponent;
import com.falling.components.VecAnimatorComponent;
import com.falling.components.VecAnimatorComponent.AnimatorType;

import static com.falling.Core.updateBlur;
import static com.falling.utils.Families.animationFamily;
import static com.falling.utils.Mappers.*;

public class AnimationSystem extends IteratingSystem {
    private VecAnimatorComponent vecAnimator;
    private AnimationComponent animation;
    private float progress;
    private Vector2 vector2Helper;

    public AnimationSystem(int priority) {
        super(animationFamily, priority);
        vecAnimator = null;
        vector2Helper = new Vector2();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        animation = animationMapper.get(entity);

        for (int i = animation.animators.size-1; i >= 0; i--) {
            vecAnimator = animation.animators.get(i);

            if (vecAnimator.repeatTimes == 0) continue;

            vecAnimator.elapsed += deltaTime;

            progress = Math.min(1f, vecAnimator.elapsed/vecAnimator.duration);

            vecAnimator.animatedVecPointer.x = vecAnimator.interpolation.apply(vecAnimator.start.x, vecAnimator.target.x, progress);
            vecAnimator.animatedVecPointer.y = vecAnimator.interpolation.apply(vecAnimator.start.y, vecAnimator.target.y, progress);

            updateBlur = true;

            // Animation ended
            if (progress == 1f) {
                vecAnimator.repeatTimes--;
                vecAnimator.elapsed = 0;
                if (vecAnimator.repeatTimes > 0 || vecAnimator.repeatTimes == -1) {
                    vector2Helper.set(vecAnimator.start);
                    vecAnimator.start.set(vecAnimator.target);
                    vecAnimator.target.set(vector2Helper);
                }
                if (vecAnimator.commandOnFinish != null && (vecAnimator.cmdOnEveryFinish || vecAnimator.repeatTimes == 0))
                    vecAnimator.commandOnFinish.execute();
            }
        }

    }

    public void animateTo(AnimationComponent animationComponent, AnimatorType animatorType, float tx, float ty, boolean additive, int repeat) {
        for (int i = animationComponent.animators.size-1; i >= 0; i--) {
            vecAnimator = animationComponent.animators.get(i);
            if (vecAnimator.type == animatorType) {
                vecAnimator.start.set(vecAnimator.animatedVecPointer);

                if (additive) 
                    vecAnimator.target.set(vecAnimator.animatedVecPointer.x + tx, vecAnimator.animatedVecPointer.y + ty);
                else 
                    vecAnimator.target.set(tx, ty);

                vecAnimator.repeatTimes = repeat;
                vecAnimator.elapsed = 0;
                updateBlur = true;
            }
        }
    }
}
