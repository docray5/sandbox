package com.falling.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.falling.components.ClickableComponent;
import com.falling.events.Messages;

import static com.falling.Core.*;
import static com.falling.utils.Families.*;
import static com.falling.utils.Mappers.*;
import static com.falling.events.Event.*;

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
                Messages.alert(getEntities().get(i), TOUCH_DOWN, null);
            }
        }

        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        for (int i = 0; i < getEntities().size(); i++) {
            if (clickableMapper.get(getEntities().get(i)).clicked) {
                Messages.alert(getEntities().get(i), TOUCH_UP, null);
                if (tempClickableComponent.hitBox.contains(tempMousePos)) {
                    Messages.alert(getEntities().get(i), CLICKED, null);
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
                Messages.alert(getEntities().get(i), MOUSE_EXIT, null);
                tempClickableComponent.hovered = true;
            } else if (tempClickableComponent.hovered) {
                Messages.alert(getEntities().get(i), MOUSE_EXIT, null);
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
        Messages.alert(KEY_DOWN);

        if (keycode == Input.Keys.E) {
            selErasePressed = true;
        } else if (keycode == Input.Keys.NUM_1) {
            selSandPressed = true;
        } else if (keycode == Input.Keys.NUM_2) {
            selWaterPressed = true;
        } else if (keycode == Input.Keys.NUM_3) {
            selWoodPressed = true;
        } else if (keycode == Input.Keys.SPACE) {
            shiftBlurPressed = true;
        } else if (keycode == Input.Keys.C) {
            circleBrushPressed = true;
        } else if (keycode == Input.Keys.X) {
            squareBrushPressed = true;
        } else if (keycode == Input.Keys.Z) {
            pixelBrushPressed = true;
        }

        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        Messages.alert(KEY_UP);

        if (keycode == Input.Keys.NUM_0) {
            selErasePressed = false;
        } else if (keycode == Input.Keys.NUM_1) {
            selSandPressed = false;
        } else if (keycode == Input.Keys.NUM_2) {
            selWaterPressed = false;
        } else if (keycode == Input.Keys.NUM_3) {
            selWoodPressed = false;
        } else if (keycode == Input.Keys.SPACE) {
            shiftBlurPressed = false;
        } else if (keycode == Input.Keys.C) {
            circleBrushPressed = false;
        } else if (keycode == Input.Keys.X) {
            squareBrushPressed = false;
        } else if (keycode == Input.Keys.Z) {
            pixelBrushPressed = false;
        }

        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

    }
}
