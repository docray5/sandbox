package com.falling.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.falling.events.Message;
import com.falling.events.MessageProcessor;

import static com.falling.utils.Families.uiTranslateFamily;
import static com.falling.utils.Mappers.animationMapper;
import static com.falling.utils.Mappers.uiTranslateMapper;

public class UITranslateSystem extends IteratingSystem {
    private final MessageProcessor processor;
    private Entity entity;

    /**
     * More like menu animation system
     */
    public UITranslateSystem(int priority) {
        super(uiTranslateFamily, priority);

        processor = new MessageProcessor(uiTranslateFamily) {
            @Override
            public void processMessage(Message message) {
                for (int i = 0; i < UITranslateSystem.super.getEntities().size(); i++) {
                    entity = UITranslateSystem.super.getEntities().get(i);
                    switch (message.getEvent()) {
                        case OPEN_MENU:
                            animationMapper.get(entity).target.set(uiTranslateMapper.get(entity).openPos);
                            animationMapper.get(entity).isAnimating = true;
                            break;
                        case CLOSE_MENU:
                            animationMapper.get(entity).target.set(uiTranslateMapper.get(entity).closePos);
                            animationMapper.get(entity).isAnimating = true;
                            break;
                        case PREP_MENU:
                            animationMapper.get(entity).target.set(new Vector2(uiTranslateMapper.get(entity).openPos).sub(0, 4));
                            animationMapper.get(entity).isAnimating = true;
                    }
                }
            }
        };
    }

    @Override
    public void update(float deltaTime) {
        processor.update();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

    }
}
