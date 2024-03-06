package com.falling.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.falling.events.Event;
import com.falling.events.Messages;

import static com.falling.Core.mousePos;
import static com.falling.Core.touchHeld;
import static com.falling.utils.Families.*;

public class InputSystem extends IteratingSystem implements InputProcessor {
    private final Vector2 tempMousePos;
    private final Viewport viewport;

    public InputSystem(Viewport viewport, int priority) {
        super(inputFamily, priority);
        tempMousePos = new Vector2();
        this.viewport = viewport;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        tempMousePos.set(screenX, screenY);
        viewport.unproject(tempMousePos);

        touchHeld = true;

        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        touchHeld = false;
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        tempMousePos.set(screenX, screenY);
        viewport.unproject(tempMousePos);
        mousePos.set(tempMousePos);
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
