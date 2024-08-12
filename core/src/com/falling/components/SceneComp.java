package com.falling.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.falling.commands.Command;

/**
 * SceneComp
 */
public class SceneComp implements Component, Poolable {
    public Scene belongingScene = null;
    public Command openCmd = null;
    public Command closeCmd = null;

    @Override
    public void reset() {
        belongingScene = null;
        openCmd = null;
        closeCmd = null;
    }

    public enum Scene {
        LOADING, MAIN
    }
}
