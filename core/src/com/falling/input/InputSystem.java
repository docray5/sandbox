package com.falling.input;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.falling.events.Event;
import com.falling.events.Publisher;

import static com.falling.Core.*;

public class InputSystem implements InputProcessor {
    private final Vector2 tempMousePos;
    private final Viewport viewport;
    public final KeyBinds keyBinds;

    public final Publisher publisher = new Publisher();

    public InputSystem(Viewport viewport, Engine engine) {
        tempMousePos = new Vector2();
        this.viewport = viewport;
        this.keyBinds = new KeyBinds();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        tempMousePos.set(screenX, screenY);
        viewport.unproject(tempMousePos);
        mousePos.set(tempMousePos);

        publisher.notify(null, Event.TOUCH_DOWN);

        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        tempMousePos.set(screenX, screenY);
        viewport.unproject(tempMousePos);
        mousePos.set(tempMousePos);

        publisher.notify(null, Event.TOUCH_UP);

        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        tempMousePos.set(screenX, screenY);
        viewport.unproject(tempMousePos);
        mousePos.set(tempMousePos);

        publisher.notify(null, Event.MOUSE_MOVED);

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
        this.keyBinds.keyDown(keycode);

        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        this.keyBinds.keyUp(keycode);

        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }
}
