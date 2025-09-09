package com.falling.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.falling.components.ClickableComp;
import com.falling.utils.ZComparator;

import static com.falling.Core.mousePos;
import static com.falling.Core.updateBlur;
import static com.falling.utils.Families.*;
import static com.falling.utils.Mappers.*;

public class ClickableSystem extends SortedIteratingSystem {
    private Vector2 posTmp;
    private Entity entityTmp;
    private ClickableComp clickCompTmp;

    public ClickableSystem(int priority) {
        super(clickableFamily, new ZComparator(), priority);
        posTmp = new Vector2();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (renderableMapper.has(entity) && renderableMapper.get(entity).center) {
            posTmp.set(transformMapper.get(entity).pos);
            if (texRegionMapper.has(entity)) {
                posTmp.x -= texRegionMapper.get(entity).textureRegion.getRegionWidth()/2f;
                posTmp.y -= texRegionMapper.get(entity).textureRegion.getRegionHeight()/2f;
            } else if (ninepatchMapper.has(entity)) {
                posTmp.x -= ninepatchMapper.get(entity).size.x/2f;
                posTmp.y -= ninepatchMapper.get(entity).size.y/2f;
            }
            clickableMapper.get(entity).hitBox.setPosition(posTmp);
        }
        else
            clickableMapper.get(entity).hitBox.setPosition(transformMapper.get(entity).pos);

        if (ninepatchMapper.has(entity))
            clickableMapper.get(entity).hitBox.setSize(ninepatchMapper.get(entity).size.x, ninepatchMapper.get(entity).size.y);
    }

    public void touchDown() {
        for (int i = 0; i < getEntities().size(); i++) {
            entityTmp = getEntities().get(i);
            clickCompTmp = clickableMapper.get(entityTmp);
            if (clickCompTmp.clickable && clickCompTmp.hitBox.contains(mousePos)) {
                clickCompTmp.clicked = true;

                updateBlur = true;

                if (clickCompTmp.onTouchDownCommand != null)
                    clickCompTmp.onTouchDownCommand.execute();

                if (clickCompTmp.vibrationMs > 0 && clickCompTmp.vibrationMs < 100)
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
            
            if (clickCompTmp.onTouchUpCommand != null) {
                clickCompTmp.onTouchUpCommand.execute();
                // We are not 100% sure if cursor is on top so if it is then: (a neat Qof)
                if (clickCompTmp.hitBox.contains(mousePos)) {
                    clickCompTmp.hovered = true;
                    if (clickCompTmp.onMouseOverCommand != null)
                        clickCompTmp.onMouseOverCommand.execute();
                }
            }

            if (clickCompTmp.hitBox.contains(mousePos) && clickCompTmp.onClickCommand != null)
                clickCompTmp.onClickCommand.execute();
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
                    clickCompTmp.onMouseOverCommand.execute();
            } else if (clickCompTmp.hovered) {
                // mouse exit
                clickCompTmp.hovered = false;
                if (clickCompTmp.onMouseOffCommand != null)
                    clickCompTmp.onMouseOffCommand.execute();
            }
        }
    }
}
