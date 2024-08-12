package com.falling.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.systems.IteratingSystem;
import com.falling.components.SceneComp;
import com.falling.components.SceneComp.Scene;

import static com.falling.utils.Families.sceneFamily;
import static com.falling.utils.Mappers.sceneMapper;

public class SceneMgrSystem extends IteratingSystem {
    private Scene currentScene;
    private Entity entityTmp;
    private SceneComp sceneCompTmp;

    public SceneMgrSystem(int priority) {
        super(sceneFamily, priority);
    }

    /**
     * For first scene (99% of the time LoadingScene)
     * call this method in create() method of Application
     * after creating all the entities for LoadingScene
     */
    public void init(Scene scene) {
        currentScene = scene;
        openCurrentScene();
    }

    public void swapScene(Scene newScene) {
        closeCurrentScene();
        currentScene = newScene;
        openCurrentScene();
    }

    private void openCurrentScene() {
        for (int i = getEntities().size()-1; i >= 0; i--) {
            entityTmp = getEntities().get(i);
            if (!sceneMapper.has(entityTmp)) continue; 
            if (sceneMapper.get(entityTmp).belongingScene != currentScene) continue;

            sceneCompTmp = sceneMapper.get(entityTmp);
            if (sceneCompTmp.openCmd == null) continue;
            sceneCompTmp.openCmd.execute();
        }
    }

    private void closeCurrentScene() {
        for (int i = getEntities().size()-1; i >= 0; i--) {
            entityTmp = getEntities().get(i);
            if (!sceneMapper.has(entityTmp)) continue; 
            if (sceneMapper.get(entityTmp).belongingScene != currentScene) continue;

            sceneCompTmp = sceneMapper.get(entityTmp);
            if (sceneCompTmp.closeCmd == null) continue;
            sceneCompTmp.closeCmd.execute();
        }
    }

    @Override
    public void update(float arg0) {
    }

	@Override
	protected void processEntity(Entity arg0, float arg1) {
	}
}
