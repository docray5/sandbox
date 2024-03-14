package com.falling;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.falling.assets.Assets;
import com.falling.events.Event;
import com.falling.events.Messages;
import com.falling.factories.Director;
import com.falling.systems.*;
import com.falling.utils.*;

public class Application implements ApplicationListener  {
    private PooledEngine engine;
    private RenderSystem renderSystem;
    private Assets assets;

	@Override
	public void create () {
        // ======================= Initialize Singletons =======================
        assets = new Assets(this);
        assets.load();

        Mappers.init();
        Families.init();
        Messages.init();

        // ======================= Initialize Engine =======================
        renderSystem = new RenderSystem(8);
        InputSystem inputSystem = new InputSystem(renderSystem.getViewport(), 0);
        Gdx.input.setInputProcessor(inputSystem);

        engine = new PooledEngine();

        Director.setInstance(new Director(engine, assets));

        engine.addSystem(inputSystem);
        engine.addSystem(new AnimationSystem(1));

        engine.addSystem(new ResizeableSystem(7));
        engine.addSystem(renderSystem);
        engine.addSystem(new PauseSystem(10));
	}

	@Override
	public void render () {
        // =================== For loading assets at loading screen ===================
        if (!assets.isFinished()) assets.update();

        // =================== Input -> Update -> Render, The Engine ===================
        engine.update(Gdx.graphics.getDeltaTime());

        // =================== Clear Messages ===================
        Messages.clear();
	}

    public void startGame() {
        renderSystem.init(assets);

        engine.addSystem(new ButtonSystem(2));
        engine.addSystem(new UITranslateSystem(2));
        engine.addSystem(new TempCursorSystem(4));
        engine.addSystem(new WorldSystem(5));

        Director.instance.createSpawnArea();

        // Messages.alert(Event.SHIFT_BLUR, Families.renderableFamily);
    }

    @Override
    public void resize(int width, int height) {
        renderSystem.resize(width, height);
    }

	@Override
	public void dispose () {
        assets.dispose();
        renderSystem.dispose();
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
