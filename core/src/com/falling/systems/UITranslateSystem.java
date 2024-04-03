package com.falling.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;

import static com.falling.utils.Families.uiTranslateFamily;
import static com.falling.utils.Mappers.animationMapper;
import static com.falling.utils.Mappers.uiTranslateMapper;

public class UITranslateSystem extends IteratingSystem {
    private Entity entity;

    /**
     * More like menu animation system
     */
    public UITranslateSystem(int priority) {
        super(uiTranslateFamily, priority);
    }

    @Override
    public void update(float deltaTime) {
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

    }

    public void openMenu() {
        for (int i = 0; i < super.getEntities().size(); i++) {
            entity = super.getEntities().get(i);
            animationMapper.get(entity).target.set(uiTranslateMapper.get(entity).openPos);
            animationMapper.get(entity).isAnimating = true;
        }
    }

    public void closeMenu() {
        for (int i = 0; i < super.getEntities().size(); i++) {
            entity = super.getEntities().get(i);
            animationMapper.get(entity).target.set(uiTranslateMapper.get(entity).closePos);
            animationMapper.get(entity).isAnimating = true;

        }
    }

    public void prepMenu() {
        for (int i = 0; i < super.getEntities().size(); i++) {
            entity = super.getEntities().get(i);
            animationMapper.get(entity).target.set(new Vector2(uiTranslateMapper.get(entity).openPos).sub(0, 4));
            animationMapper.get(entity).isAnimating = true;
        }
    }
}
