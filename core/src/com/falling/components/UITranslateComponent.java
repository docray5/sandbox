package com.falling.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;
import com.falling.commands.Command;

public class UITranslateComponent implements Component, Pool.Poolable {
    public Command openCmd = null;
    public Command closeCmd = null;

    @Override
    public void reset() {
        openCmd = null;
        closeCmd = null;
    }
}
