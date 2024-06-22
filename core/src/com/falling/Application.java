package com.falling;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.falling.assets.Assets;
import com.falling.commands.Commands;
import com.falling.factories.Director;
import com.falling.systems.*;
import com.falling.utils.*;

public class Application implements ApplicationListener  {
    private PooledEngine engine;
    private RenderSystem renderSystem;
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
        Commands.setInstance(new Commands(engine, this));

        engine.addSystem(new AnimationSystem(1));
        engine.addSystem(new ResizeableSystem(7));
        renderSystem = new RenderSystem(8);
        engine.addSystem(renderSystem);

        Director.instance.createTitle();
	}

	@Override
	public void render () {
        // =================== For loading assets at loading screen (could be done in a loading system but shhhh) ===================
        if (!assets.isFinished()) assets.update();

        // =================== Input -> Update -> Render, The Engine ===================
        engine.update(Gdx.graphics.getDeltaTime());
	}

    public void startGame() {
        if (started) return;
        started = true;
        // Engine related:
        renderSystem.init(assets);

        engine.addSystem(new ClickableSystem(2));
        engine.addSystem(new UITranslateSystem(2));
        engine.addSystem(new TempCursorSystem(4));
        engine.addSystem(new WorldSystem(5));

        Commands.instance.initAfterAssets(engine);
        Gdx.input.setInputProcessor(new InputSystem(renderSystem.getViewport(), engine));

        // Scene related:
        Director.instance.createSpawnArea();
        Director.instance.createStartText();
        Director.instance.createBlurBtn();
        Director.instance.createPauseBtn();
    }

    @Override
    public void resize(int width, int height) {
        renderSystem.resize(width, height);
    }

	@Override
	public void dispose () {
        assets.dispose();
        renderSystem.dispose();
        engine.getSystem(WorldSystem.class).dispose();
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
