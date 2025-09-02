package com.falling;

import static com.falling.components.SceneComp.Scene.LOADING;
import static com.falling.components.SceneComp.Scene.MAIN;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.falling.assets.Assets;
import com.falling.commands.Commands;
import com.falling.events.Observers;
import com.falling.factories.Director;
import com.falling.systems.*;
import com.falling.utils.*;
import com.falling.input.InputSystem;

public class Application implements ApplicationListener {

    public static PooledEngine getEngine() { return engine; }

    private static PooledEngine engine;
    private RenderSystem renderSystem;
    private InputSystem inputSystem;
    private SceneMgrSystem sceneMgrSystem;
    private Assets assets;
    private boolean started = false;

	@Override
	public void create () {
        // ======================= Initialize Singletons =======================
        assets = new Assets();
        assets.load();

        Mappers.init();
        Families.init();

        // ======================= Initialize Engine =======================
        engine = new PooledEngine();
        Director.setInstance(new Director(engine, assets));
        Commands.setInstance(new Commands());

        sceneMgrSystem = new SceneMgrSystem(20);

        engine.addSystem(new AnimationSystem(1));
        engine.addSystem(sceneMgrSystem);
        engine.addSystem(new ResizeableSystem(7));
        renderSystem = new RenderSystem(8);
        engine.addSystem(renderSystem);

        Observers.setInstance(new Observers(this));
        renderSystem.publisher.addObservers(Observers.instance.resizeObsrv);
        assets.publisher.addObservers(Observers.instance.assetsObsrv);

        Director.instance.createLoadingScene();
        sceneMgrSystem.init(LOADING);
	}

	@Override
	public void render () {
        // =================== For loading assets at loading screen (could be done in a loading system but shhhh) ===================
        if (!assets.isFinished()) assets.update();

        // =================== Input -> Update -> Render, The Engine ===================
        engine.update(Gdx.graphics.getDeltaTime());
	}

    public void onLoadedAssets() {
        if (started) return;
        started = true;
        // Engine related:
        renderSystem.init(assets);

        engine.addSystem(new ClickableSystem(2));
        engine.addSystem(new TempCursorSystem(4));
        // engine.addSystem(new WorldSystem(5)); // Commented sandbox
        engine.addSystem(new FluidSystem(5));


        inputSystem = new InputSystem(renderSystem.getViewport(), engine);
        Gdx.input.setInputProcessor(inputSystem);

        // Set up observers and Commands:
        Commands.instance.initAfterAssets();
        Observers.instance.initaAfterAssets();
        inputSystem.keyBinds.initAfterAssets();
        inputSystem.publisher.addObservers(Observers.instance.clickableObsrv);

        // Scene related:
        Director.instance.createMainScene();

        sceneMgrSystem.swapScene(MAIN);
    }

    @Override
    public void resize(int width, int height) {
        renderSystem.resize(width, height);
    }

	@Override
	public void dispose () {
        assets.dispose();
        renderSystem.dispose();
        //engine.getSystem(WorldSystem.class).dispose();
	}

    @Override
	public void pause() {
        for (EntitySystem system : engine.getSystems()) system.setProcessing(false);
	}

	@Override
	public void resume() {
        for (EntitySystem system : engine.getSystems()) system.setProcessing(true);
	}
}
