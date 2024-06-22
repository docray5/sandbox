package com.falling.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.systems.IteratingSystem;

import static com.falling.utils.Families.uiTranslateFamily;
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

            uiTranslateMapper.get(entity).openCmd.execute();
        }
    }

    public void closeMenu() {
        for (int i = 0; i < super.getEntities().size(); i++) {
            entity = super.getEntities().get(i);

            uiTranslateMapper.get(entity).closeCmd.execute();
        }
    }

    public void prepMenu() {
        for (int i = 0; i < super.getEntities().size(); i++) {
            entity = super.getEntities().get(i);
        }
    }
}
