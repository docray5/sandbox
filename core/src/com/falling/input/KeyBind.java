package com.falling.input;

import com.falling.commands.Command;

/**
 * KeyBind
 */
public class KeyBind {

    private Integer key;
    private Command cmdOnKeyDown;
    private Command cmdOnKeyUp;

    public KeyBind(Integer key, Command onKeyUp, Command onKeyDown) {
        this.key = key;
        cmdOnKeyUp = onKeyUp;
        cmdOnKeyDown = onKeyDown;
    }

    public void keyDown(int keycode) {
        if (key.intValue() != keycode) return;
        if (cmdOnKeyDown == null) return;
        cmdOnKeyDown.execute();
    }

    public void keyUp(int keycode) {
        if (key.intValue() != keycode) return;
        if (cmdOnKeyUp == null) return;
        cmdOnKeyUp.execute();
    }
}
