package com.falling.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.systems.IteratingSystem;
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

    public AnimationSystem(int priority) {
        super(animationFamily, priority);
        vecAnimator = null;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        animation = animationMapper.get(entity);

        for (int i = animation.animators.size-1; i >= 0; i--) {
            vecAnimator = animation.animators.get(i);

            if (!vecAnimator.isAnimating) continue;

            vecAnimator.elapsed += deltaTime;

            progress = Math.min(1f, vecAnimator.elapsed/vecAnimator.duration);

            vecAnimator.animatedVecPointer.x = vecAnimator.interpolation.apply(vecAnimator.start.x, vecAnimator.target.x, progress);
            vecAnimator.animatedVecPointer.y = vecAnimator.interpolation.apply(vecAnimator.start.y, vecAnimator.target.y, progress);

            updateBlur = true;

            // Animation ended
            if (progress == 1f) {
                vecAnimator.isAnimating = false;
                vecAnimator.elapsed = 0;
                if (vecAnimator.commandOnFinish != null)
                    vecAnimator.commandOnFinish.execute();
            }
        }

    }

    public void animateTo(AnimationComponent animationComponent, AnimatorType animatorType, float tx, float ty, boolean additive) {
        for (int i = animationComponent.animators.size-1; i >= 0; i--) {
            vecAnimator = animationComponent.animators.get(i);
            if (vecAnimator.type == animatorType) {
                vecAnimator.start.set(vecAnimator.animatedVecPointer);

                if (additive) 
                    vecAnimator.target.set(vecAnimator.animatedVecPointer.x + tx, vecAnimator.animatedVecPointer.y + ty);
                else 
                    vecAnimator.target.set(tx, ty);

                vecAnimator.isAnimating = true;
                vecAnimator.elapsed = 0;
                updateBlur = true;
            }
        }
    }
}
