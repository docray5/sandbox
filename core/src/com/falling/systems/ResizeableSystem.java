package com.falling.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.falling.components.ResizeComp;

import static com.falling.Core.*;
import static com.falling.utils.Families.resizeFamily;
import static com.falling.utils.Mappers.*;

public class ResizeableSystem extends IteratingSystem {
    private Entity entity;
    private ImmutableArray<Entity> entitiesTmp;
    private ResizeComp resizeTmp;
    private Vector2 posTmp;
    private Vector2 blurPosTmp;

    public ResizeableSystem(int priority) {
        super(resizeFamily, priority);
        setProcessing(false);
    }

    public void resize() {
        entitiesTmp = getEntities();
        for (int i = 0; i < entitiesTmp.size(); i++) {
            entity = entitiesTmp.get(i);
            resizeTmp = resizeMapper.get(entity);

            if (transformMapper.has(entity)) {
                posTmp = transformMapper.get(entity).pos;

                normalizePosition(resizeTmp, posTmp);

                if (blurMapper.has(entity)) {
                    blurPosTmp = blurMapper.get(entity).regionPos;
                    blurPosTmp.set(posTmp);
                    absolutePosition(resizeTmp, blurPosTmp);
                }
            }

            if (clickableMapper.has(entity)) {
                clickableMapper.get(entity).hitBox.setPosition(posTmp);
            }

            if (typeMapper.has(entity)) {
                switch (typeMapper.get(entity).type) {
                    case FULLSCREEN_CLICK:
                        clickableMapper.get(entity).hitBox.setSize(worldWidth, worldHeight);
                        break;
                    case WORLD:
                        // DIRTY QUICK FIX: TODO
                        getEngine().getSystem(WorldSystem.class).resize();
                        break;
                }
            }        
        }
    }

    private void absolutePosition(ResizeComp resize, Vector2 pos) {
        switch (resize.stick) {
            case LEFT_TOP:
                pos.x += lrGutter;
                pos.y -= tbGutter;
                break;
            case RIGHT_TOP:
                pos.x -= lrGutter;
                pos.y -= tbGutter;
                break;
            case LEFT_BOTTOM:
                pos.x += lrGutter;
                pos.y += tbGutter;
                break;
            case RIGHT_BOTTOM:
                pos.x -= lrGutter;
                pos.y += tbGutter;
                break;
            case LEFT:
                pos.x += lrGutter;
                break;
            case RIGHT:
                pos.x -= lrGutter;
                break;
            case TOP:
                pos.y -= tbGutter;
                break;
            case BOTTOM:
                pos.y += tbGutter;
                break;
        }
    }

    private void normalizePosition(ResizeComp resize, Vector2 pos) {
        switch (resize.stick) {
            case LEFT_TOP:
                pos.x -= xGutOffset;
                pos.y += yGutOffset;
                break;
            case RIGHT_TOP:
                pos.x += xGutOffset;
                pos.y += yGutOffset;
                break;
            case LEFT_BOTTOM:
                pos.x -= xGutOffset;
                pos.y -= yGutOffset;
                break;
            case RIGHT_BOTTOM:
                pos.x += xGutOffset;
                pos.y -= yGutOffset;
                break;
            case LEFT:
                pos.x -= xGutOffset;
                break;
            case RIGHT:
                pos.x += xGutOffset;
                break;
            case TOP:
                pos.y += yGutOffset;
                break;
            case BOTTOM:
                pos.y -= yGutOffset;
                break;
        }
    }

    @Override
    public void update(float deltaTime) { }

	@Override
	protected void processEntity(Entity entity, float deltaTime) { }
}
