package com.falling.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.falling.events.Event;
import com.falling.events.Message;
import com.falling.events.MessageProcessor;

import static com.falling.utils.Families.pauseFamily;
import static com.falling.utils.Mappers.pauseMapper;

public class PauseSystem extends EntitySystem {
    private final MessageProcessor processor;

    public PauseSystem(int priority) {
        super(priority);
        processor = new MessageProcessor(pauseFamily) {
            @Override
            public void processMessage(Message message) {
                if (message.getEvent() == Event.PAUSE_SYSTEM) {
                    getEngine().getSystem(pauseMapper.get(message.getEntityToAlert()).system).setProcessing(false);
                } else if (message.getEvent() == Event.RESUME_SYSTEM) {
                    getEngine().getSystem(pauseMapper.get(message.getEntityToAlert()).system).setProcessing(true);
                }
            }
        };
    }

    @Override
    public void update(float deltaTime) {
        processor.update();
    }
}
