package com.falling.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.falling.components.ResizeComp;

import static com.falling.Core.*;
import static com.falling.utils.Families.resizeFamily;
import static com.falling.utils.Mappers.*;

public class ResizeableSystem extends IteratingSystem {
    private Entity entity;
    private ImmutableArray<Entity> entitiesTmp;
    private ResizeComp resizeTmp;
    private Vector2 posTmp;

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

                switch (resizeTmp.stick) {
                    case LEFT_TOP:
                        posTmp.x -= xGutOffset;
                        posTmp.y += yGutOffset;
                        break;
                    case RIGHT_TOP:
                        posTmp.x += xGutOffset;
                        posTmp.y += yGutOffset;
                        break;
                    case LEFT_BOTTOM:
                        posTmp.x -= xGutOffset;
                        posTmp.y -= yGutOffset;
                        break;
                    case RIGHT_BOTTOM:
                        posTmp.x += xGutOffset;
                        posTmp.y -= yGutOffset;
                        break;
                    case LEFT:
                        posTmp.x -= xGutOffset;
                        break;
                    case RIGHT:
                        posTmp.x += xGutOffset;
                        break;
                    case TOP:
                        posTmp.y += yGutOffset;
                        break;
                    case BOTTOM:
                        posTmp.y -= yGutOffset;
                        break;
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

    @Override
    public void update(float deltaTime) { }

	@Override
	protected void processEntity(Entity entity, float deltaTime) { }
}
