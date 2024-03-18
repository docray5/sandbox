package com.falling.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.falling.components.ButtonComponent;
import com.falling.components.ClickableComponent;
import com.falling.components.TypeComponent;
import com.falling.events.Event;
import com.falling.events.Message;
import com.falling.events.MessageProcessor;
import com.falling.events.Messages;

import static com.falling.utils.Families.*;
import static com.falling.utils.Mappers.*;

public class ButtonSystem extends IteratingSystem {
    private final MessageProcessor processor;
    private Vector2 posTmp;
    private ButtonComponent buttonComponentTmp;

    public ButtonSystem(int priority) {
        super(buttonFamily, priority);
        processor = new MessageProcessor() {
            @Override
            public void processMessage(Message message) {
                if (message.getEntityToAlert() == null || !buttonMapper.has(message.getEntityToAlert())) return;
                buttonComponentTmp = buttonMapper.get(message.getEntityToAlert());
                switch (message.getEvent()) {
                    case CLICKED:
                        if (buttonComponentTmp.holdable) break;
                        Messages.alert(buttonComponentTmp.onClickEvent);
                        break;
                    case TOUCH_DOWN:
                        clickableMapper.get(message.getEntityToAlert()).clicked = true;

                        // =========== Animation ===========
                        if (typeMapper.has(message.getEntityToAlert()) &&
                                typeMapper.get(message.getEntityToAlert()).type == TypeComponent.Type.FULLSCREEN_CLICK)
                            Messages.alert(Event.PREP_MENU);
                        if (buttonComponentTmp.onTouch == null) break;
                        switch (buttonComponentTmp.onTouch) {
                            case TEXTURE:
                                texRegionMapper.get(message.getEntityToAlert()).textureRegion.setTexture(buttonComponentTmp.clickedTexture);
                                break;
                            case SCALE:
                                transformMapper.get(message.getEntityToAlert()).scale.set(0.5f, 0.5f);
                                break;
                        }

                        Gdx.input.vibrate(10);
                        break;
                    case TOUCH_UP:
                        clickableMapper.get(message.getEntityToAlert()).clicked = false;

                        // =========== Animation ===========
                        if (buttonComponentTmp.onTouch == null) break;
                        switch (buttonComponentTmp.onTouch) {
                            case TEXTURE:
                                texRegionMapper.get(message.getEntityToAlert()).textureRegion.setTexture(buttonComponentTmp.normalTexture);
                                break;
                            case SCALE:
                                transformMapper.get(message.getEntityToAlert()).scale.set(1, 1);
                                break;
                        }

                        break;
                    case MOUSE_ENTER:
                        if (buttonComponentTmp.onMouse == null) break;
                        switch (buttonComponentTmp.onMouse) {
                            case SCALE:
                                transformMapper.get(message.getEntityToAlert()).scale.set(1.1f, 1.1f);
                                break;
                            case OUTLINE:
                                break;
                            case TRANSLATE:
                                if (animationMapper.has(message.getEntityToAlert()))
                                    animationMapper.get(message.getEntityToAlert()).pos.y += 1;
                                else
                                    transformMapper.get(message.getEntityToAlert()).pos.y += 1f;
                                break;
                        }
                        break;
                    case MOUSE_EXIT:
                        if (buttonComponentTmp.onMouse == null) break;
                        switch (buttonComponentTmp.onMouse) {
                            case SCALE:
                                transformMapper.get(message.getEntityToAlert()).scale.set(1, 1);
                                break;
                            case OUTLINE:
                                break;
                            case TRANSLATE:
                                if (animationMapper.has(message.getEntityToAlert()))
                                    animationMapper.get(message.getEntityToAlert()).pos.y -= 1;
                                 else
                                     transformMapper.get(message.getEntityToAlert()).pos.y-= 1f;
                                break;
                        }
                        break;
					default:
						break;
                }
            }
        };
    }

    @Override
    public void update(float deltaTime) {
        processor.update();
        super.update(deltaTime);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        buttonComponentTmp = buttonMapper.get(entity);

        if (clickableMapper.get(entity).clicked && buttonComponentTmp.holdable)
            Messages.alert(buttonComponentTmp.onClickEvent);

        if (renderableMapper.has(entity) && texRegionMapper.has(entity) && renderableMapper.get(entity).center) {
            posTmp.set(transformMapper.get(entity).pos);
            posTmp.x -= texRegionMapper.get(entity).textureRegion.getRegionWidth()/2f;
            posTmp.y -= texRegionMapper.get(entity).textureRegion.getRegionHeight()/2f;
            clickableMapper.get(entity).hitBox.setPosition(posTmp);
        }
        else
            clickableMapper.get(entity).hitBox.setPosition(transformMapper.get(entity).pos);
    }
}
