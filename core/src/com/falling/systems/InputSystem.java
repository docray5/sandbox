package com.falling.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.falling.components.ClickableComponent;
import com.falling.events.Event;
import com.falling.events.Messages;

import static com.falling.Core.*;
import static com.falling.utils.Families.*;
import static com.falling.utils.Mappers.*;

public class InputSystem extends IteratingSystem implements InputProcessor {
    private final Vector2 tempMousePos;
    private final Viewport viewport;

    private ClickableComponent tempClickableComponent;

    public InputSystem(Viewport viewport, int priority) {
        super(inputFamily, priority);
        tempMousePos = new Vector2();
        this.viewport = viewport;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        tempMousePos.set(screenX, screenY);
        viewport.unproject(tempMousePos);

        for (int i = 0; i < getEntities().size(); i++) {
            tempClickableComponent = clickableMapper.get(getEntities().get(i));
            if (tempClickableComponent.clickable && tempClickableComponent.hitBox.contains(tempMousePos)) {
                Messages.alert(getEntities().get(i),Event.TOUCH_DOWN, buttonFamily, null);
            }
        }

        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        for (int i = 0; i < getEntities().size(); i++) {
            if (clickableMapper.get(getEntities().get(i)).clicked) {
                Messages.alert(getEntities().get(i), Event.TOUCH_UP, buttonFamily, null);
                if (tempClickableComponent.hitBox.contains(tempMousePos)) {
                    Messages.alert(getEntities().get(i), Event.CLICKED, buttonFamily, null);
                }
            }
        }

        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        tempMousePos.set(screenX, screenY);
        viewport.unproject(tempMousePos);
        mousePos.set(tempMousePos);

        for (int i = 0; i < getEntities().size(); i++) {
            tempClickableComponent = clickableMapper.get(getEntities().get(i));
            if (tempClickableComponent.clickable && tempClickableComponent.hitBox.contains(tempMousePos)) {
                Messages.alert(getEntities().get(i), Event.MOUSE_EXIT, buttonFamily, null);
                tempClickableComponent.hovered = true;
            } else if (tempClickableComponent.hovered) {
                Messages.alert(getEntities().get(i), Event.MOUSE_EXIT, buttonFamily, null);
                tempClickableComponent.hovered = false;
            }
        }

        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        tempMousePos.set(screenX, screenY);
        viewport.unproject(tempMousePos);
        mousePos.set(tempMousePos);
        return false;
    }

    // -----------------------------------------------------------------------

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

    }
}
