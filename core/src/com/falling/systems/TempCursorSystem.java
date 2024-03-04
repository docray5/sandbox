package com.falling.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.systems.IteratingSystem;
import static com.falling.utils.Families.cursorFamily;

public class TempCursorSystem extends IteratingSystem {

    public TempCursorSystem(int priority) {
        super(cursorFamily, priority);
        setProcessing(true);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        /*for (int i = 0; i < 10; i++) {
            Director.instance.createFireParticle();
        }*/
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {



        /*transformMapper.get(entity).pos.set(mousePos);
        transformMapper.get(entity).pos.x = MathUtils.round(transformMapper.get(entity).pos.x);
        transformMapper.get(entity).pos.y = MathUtils.round(transformMapper.get(entity).pos.y);*/
        /*animationMapper.get(entity).target.set(
                mousePos.x - animatedTextureMapper.get(entity).animation.getKeyFrame(animatedTextureMapper.get(entity).stateTime).getRegionWidth()/2f,
                mousePos.y - animatedTextureMapper.get(entity).animation.getKeyFrame(animatedTextureMapper.get(entity).stateTime).getRegionHeight()/2f);*/
    }

}
