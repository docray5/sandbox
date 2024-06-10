package com.falling.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.falling.components.ClickableComponent;
import com.falling.utils.ZComparator;

import static com.falling.Core.mousePos;
import static com.falling.Core.updateBlur;
import static com.falling.utils.Families.*;
import static com.falling.utils.Mappers.*;

public class ClickableSystem extends SortedIteratingSystem {
    private Vector2 posTmp;
    private Entity entityTmp;
    private ClickableComponent clickCompTmp;

    public ClickableSystem(int priority) {
        super(clickableFamily, new ZComparator(), priority);
        posTmp = new Vector2();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (renderableMapper.has(entity) && texRegionMapper.has(entity) && renderableMapper.get(entity).center) {
            posTmp.set(transformMapper.get(entity).pos);
            posTmp.x -= texRegionMapper.get(entity).textureRegion.getRegionWidth()/2f;
            posTmp.y -= texRegionMapper.get(entity).textureRegion.getRegionHeight()/2f;
            clickableMapper.get(entity).hitBox.setPosition(posTmp);
        }
        else
            clickableMapper.get(entity).hitBox.setPosition(transformMapper.get(entity).pos);
    }

    public void touchDown() {
        for (int i = 0; i < getEntities().size(); i++) {
            entityTmp = getEntities().get(i);
            clickCompTmp = clickableMapper.get(entityTmp);
            if (clickCompTmp.clickable && clickCompTmp.hitBox.contains(mousePos)) {
                clickCompTmp.clicked = true;

                updateBlur = true;

                if (clickCompTmp.onTouchDownCommand != null)
                    clickCompTmp.onTouchDownCommand.setEntity(entityTmp).execute();

                Gdx.input.vibrate(clickCompTmp.vibrationMs);
                return;
            }
        }
    }

    public void touchUp() {
        for (int i = 0; i < getEntities().size(); i++) {
            entityTmp = getEntities().get(i);
            clickCompTmp = clickableMapper.get(entityTmp);

            if (!clickCompTmp.clicked || !clickCompTmp.clickable) continue;

            clickCompTmp.clicked = false;

            updateBlur = true;
            
            if (clickCompTmp.onTouchUpCommand != null)
                clickCompTmp.onTouchUpCommand.setEntity(entityTmp).execute();

            if (clickCompTmp.hitBox.contains(mousePos) && clickCompTmp.onClickCommand != null) {
                clickCompTmp.onClickCommand.execute();
            }
        }
    }

    public void mouseMoved() {
        for (int i = 0; i < getEntities().size(); i++) {
            entityTmp = getEntities().get(i);
            clickCompTmp = clickableMapper.get(entityTmp);

            if (!clickCompTmp.clickable) continue;

            updateBlur = true;

            if (clickCompTmp.hitBox.contains(mousePos)) {
                // mouse enter
                clickCompTmp.hovered = true;
                if (clickCompTmp.onMouseOverCommand != null)
                    clickCompTmp.onMouseOverCommand.setEntity(entityTmp).execute();
            } else if (clickCompTmp.hovered) {
                // mouse exit
                clickCompTmp.hovered = false;
                if (clickCompTmp.onMouseOffCommand != null)
                    clickCompTmp.onMouseOffCommand.setEntity(entityTmp).execute();
            }
        }
    }
}
