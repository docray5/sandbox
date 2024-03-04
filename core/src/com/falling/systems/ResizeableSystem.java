package com.falling.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.falling.components.ResizeComponent;

import static com.falling.Core.*;
import static com.falling.Core.tbGutter;
import static com.falling.utils.Families.resizeFamily;
import static com.falling.utils.Mappers.*;

public class ResizeableSystem extends EntitySystem {
    private Entity entity;
    private ImmutableArray<Entity> entitiesTmp;
    private ResizeComponent resizeTmp;
    private Vector2 posTmp;

    public ResizeableSystem(int priority) {
        super(priority);
    }

    public void resize() {
        entitiesTmp = getEngine().getEntitiesFor(resizeFamily);
        for (int i = 0; i < entitiesTmp.size(); i++) {
            entity = entitiesTmp.get(i);
            resizeTmp = resizeMapper.get(entity);

            if (transformMapper.has(entity)) {
                posTmp = transformMapper.get(entity).pos;
                posTmp.x += resizeTmp.lastLRGutter;
                posTmp.y += resizeTmp.lastTBGutter;

                resizeTmp.lastLRGutter = 0;
                resizeTmp.lastTBGutter = 0;

                switch (resizeTmp.stick) {
                    case LEFT_TOP:
                        posTmp.x -= lrGutter;
                        posTmp.y += tbGutter;
                        resizeTmp.lastLRGutter += lrGutter;
                        resizeTmp.lastTBGutter -= tbGutter;
                        break;
                    case RIGHT_TOP:
                        posTmp.x += lrGutter;
                        posTmp.y += tbGutter;
                        resizeTmp.lastLRGutter -= lrGutter;
                        resizeTmp.lastTBGutter -= tbGutter;
                        break;
                    case LEFT_BOTTOM:
                        posTmp.x -= lrGutter;
                        posTmp.y -= tbGutter;
                        resizeTmp.lastLRGutter += lrGutter;
                        resizeTmp.lastTBGutter += tbGutter;
                        break;
                    case RIGHT_BOTTOM:
                        posTmp.x += lrGutter;
                        posTmp.y -= tbGutter;
                        resizeTmp.lastLRGutter -= lrGutter;
                        resizeTmp.lastTBGutter += tbGutter;
                        break;
                    case LEFT:
                        posTmp.x -= lrGutter;
                        resizeTmp.lastLRGutter += lrGutter;
                        break;
                    case RIGHT:
                        posTmp.x += lrGutter;
                        resizeTmp.lastLRGutter -= lrGutter;
                        break;
                    case TOP:
                        posTmp.y += tbGutter;
                        resizeTmp.lastTBGutter -= tbGutter;
                        break;
                    case BOTTOM:
                        posTmp.y -= tbGutter;
                        resizeTmp.lastTBGutter += tbGutter;
                        break;
                }
            }

            if (typeMapper.has(entity)) {
                switch (typeMapper.get(entity).type) {
                    case FULLSCREEN_CLICK:
                        break;
                }
            }
        }
    }

    @Override
    public void update(float deltaTime) {

    }
}
