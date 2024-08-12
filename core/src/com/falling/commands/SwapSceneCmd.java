package com.falling.commands;

import com.falling.Application;
import com.falling.components.SceneComp.Scene;
import com.falling.systems.SceneMgrSystem;

public class SwapSceneCmd extends Command {
    private SceneMgrSystem sceneMgrSystem;
    private Scene newScene;

    public SwapSceneCmd(Scene newScene) {
        sceneMgrSystem = Application.getEngine().getSystem(SceneMgrSystem.class);
        this.newScene = newScene;
    }

	@Override
	public void execute() {
        sceneMgrSystem.swapScene(newScene);
	}

    public void setNewScene(Scene newScene) {
        this.newScene = newScene;
    }
}
