package com.falling.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.Array;
import com.falling.commands.*;

import static com.falling.components.ElementComp.ElementType.*;
import static com.falling.systems.WorldSystem.BrushType.*;

/**
 * KeyBinds
 */
public class KeyBinds {

    private static Integer SHIFT_BLUR = Input.Keys.B;
    private static Integer PAUSE_WORLD = Input.Keys.SPACE;

    private static Integer SEL_BR_CIRCLE = Input.Keys.C;
    private static Integer SEL_BR_SQUARE = Input.Keys.X;
    private static Integer SEL_BR_PIXEL = Input.Keys.Z;

    private static Integer SEL_ERASER = Input.Keys.E;
    private static Integer SEL_EL_SAND = Input.Keys.NUM_1;
    private static Integer SEL_EL_WATER = Input.Keys.NUM_2;
    private static Integer SEL_EL_WOOD = Input.Keys.NUM_3;
    private static Integer LEFT_FLUID = Input.Keys.LEFT;
    private static Integer RIGHT_FLUID = Input.Keys.RIGHT;

    private final Array<KeyBind> keyBinds;

    public KeyBinds() {
        keyBinds = new Array<>();

        // Load key binds from file to static vars
        
        // Set up KeyBinds and their Actions
        keyBinds.addAll(
            new KeyBind(SEL_BR_CIRCLE, new SelectBrushCmd(CIRCLE), null),
            new KeyBind(SEL_BR_SQUARE, new SelectBrushCmd(SQUARE), null),
            new KeyBind(SEL_BR_PIXEL, new SelectBrushCmd(PIXEL), null),

            new KeyBind(SEL_ERASER, new SelectElementCmd(null), null),
            new KeyBind(SEL_EL_SAND, new SelectElementCmd(SAND), null),
            new KeyBind(SEL_EL_WATER, new SelectElementCmd(WATER), null),
            new KeyBind(SEL_EL_WOOD, new SelectElementCmd(WOOD), null)
        );
    }

    public void initAfterAssets() {
        keyBinds.addAll(
            new KeyBind(SHIFT_BLUR, Commands.instance.shiftBlur, null),
            new KeyBind(PAUSE_WORLD, Commands.instance.pauseWorld, null),
                new KeyBind(LEFT_FLUID, new LeftFluidCmd(), null),
                new KeyBind(RIGHT_FLUID, new RightFluidCmd(), null)
        );
    }

    public void keyDown(int keycode) {
        for (int i = keyBinds.size-1; i >= 0; i--) {
            keyBinds.get(i).keyDown(keycode);;
        }
    }

    public void keyUp(int keycode) {
        for (int i = keyBinds.size-1; i >= 0; i--) {
            keyBinds.get(i).keyUp(keycode);;
        }
    }

    public void updateKeyBinds() {
        // update file
        
        // update keybind instances:
        // The keys are references so if you update static vars
        // the keys in keyBind classes are going to have the newest value
    }
}
