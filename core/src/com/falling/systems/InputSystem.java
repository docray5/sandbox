package com.falling.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.falling.commands.*;

import static com.falling.Core.*;
import static com.falling.components.ElementComponent.ElementType.*;
import static com.falling.systems.WorldSystem.BrushType.*;

public class InputSystem implements InputProcessor {
    private final Vector2 tempMousePos;
    private final Viewport viewport;
    private final Commands commands;

    public InputSystem(Viewport viewport, Engine engine) {
        tempMousePos = new Vector2();
        this.viewport = viewport;

        commands = Commands.instance;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        tempMousePos.set(screenX, screenY);
        viewport.unproject(tempMousePos);
        mousePos.set(tempMousePos);

        commands.touchDown.execute();

        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        tempMousePos.set(screenX, screenY);
        viewport.unproject(tempMousePos);
        mousePos.set(tempMousePos);

        commands.touchUp.execute();

        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        tempMousePos.set(screenX, screenY);
        viewport.unproject(tempMousePos);
        mousePos.set(tempMousePos);

        commands.mouseMoved.execute();

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
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Input.Keys.E:
                commands.selectElement.setElementType(null).execute();
                break;
            case Input.Keys.NUM_1:
                commands.selectElement.setElementType(SAND).execute();
                break;
            case Input.Keys.NUM_2:
                commands.selectElement.setElementType(WATER).execute();
                break;
            case Input.Keys.NUM_3:
                commands.selectElement.setElementType(WOOD).execute();
                break;
            case Input.Keys.C:
                commands.selectBrush.setBrushType(CIRCLE).execute();
                break;
            case Input.Keys.X:
                commands.selectBrush.setBrushType(SQUARE).execute();
                break;
            case Input.Keys.Z:
                commands.selectBrush.setBrushType(PIXEL).execute();
                break;
            case Input.Keys.SPACE:
                commands.shiftBlur.execute();
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }
}
