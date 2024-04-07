package com.falling.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.falling.components.ButtonComponent;
import com.falling.components.ClickableComponent;

import static com.falling.Core.mousePos;
import static com.falling.utils.Families.*;
import static com.falling.utils.Mappers.*;

public class ButtonSystem extends IteratingSystem {
    private Vector2 posTmp;
    private Entity entityTmp;
    private ButtonComponent buttonComponentTmp;
    private ClickableComponent clickableComponentTmp;

    private boolean mouseEnter;
    private int translate;
    private float scale;

    public ButtonSystem(int priority) {
        super(buttonFamily, priority);
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
            clickableComponentTmp = clickableMapper.get(getEntities().get(i));
            if (clickableComponentTmp.clickable && clickableComponentTmp.hitBox.contains(mousePos)) {
                clickableComponentTmp.clicked = true;

                entityTmp = getEntities().get(i);
                buttonComponentTmp = buttonMapper.get(entityTmp);

                buttonComponentTmp.onTouchDownCommand.execute();

                // =========== Animation ===========
                if (buttonComponentTmp.onTouchAnim == null) continue;
                switch (buttonComponentTmp.onTouchAnim) {
                    case TEXTURE:
                        texRegionMapper.get(entityTmp).textureRegion.setTexture(buttonComponentTmp.clickedTexture);
                        break;
                    case SCALE:
                        transformMapper.get(entityTmp).scale.set(0.5f, 0.5f);
                        break;
                }

                Gdx.input.vibrate(10);
            }
        }
    }

    public void touchUp() {
        for (int i = 0; i < getEntities().size(); i++) {
            clickableComponentTmp = clickableMapper.get(getEntities().get(i));

            if (!clickableComponentTmp.clicked || !clickableComponentTmp.clickable) continue;

            entityTmp = getEntities().get(i);
            buttonComponentTmp = buttonMapper.get(entityTmp);

            clickableComponentTmp.clicked = false;
            
            buttonComponentTmp.onTouchUpCommand.execute();

            if (clickableComponentTmp.hitBox.contains(mousePos) && buttonComponentTmp.onClickCommand != null) {
                buttonComponentTmp.onClickCommand.execute();
            }

            // =========== Animation ===========
            if (buttonComponentTmp.onTouchAnim == null) continue;
            switch (buttonComponentTmp.onTouchAnim) {
                case TEXTURE:
                    texRegionMapper.get(entityTmp).textureRegion.setTexture(buttonComponentTmp.normalTexture);
                    break;
                case SCALE:
                    transformMapper.get(entityTmp).scale.set(1, 1);
                    break;
            }
        }
    }

    public void mouseMoved() {
        for (int i = 0; i < getEntities().size(); i++) {
            entityTmp = getEntities().get(i);
            clickableComponentTmp = clickableMapper.get(entityTmp);

            if (!clickableComponentTmp.clickable) continue;

            if (clickableComponentTmp.hitBox.contains(mousePos)) {
                // mouse enter
                clickableComponentTmp.hovered = true;
                mouseEnter = true;
            } else if (clickableComponentTmp.hovered) {
                // mouse exit
                clickableComponentTmp.hovered = false;
                mouseEnter = false;
            } else continue;

            // Animation code TODO better make it a command
            if (buttonMapper.get(entityTmp).onMouseAnim == null) continue;
            buttonComponentTmp = buttonMapper.get(entityTmp);
            translate = mouseEnter ? 1 : -1;
            scale = mouseEnter ? 1.1f : 1;
            switch (buttonComponentTmp.onMouseAnim) {
                case SCALE:
                    transformMapper.get(entityTmp).scale.set(scale, scale);
                    break;
                case OUTLINE:
                    break;
                case TRANSLATE:
                    if (animationMapper.has(entityTmp))
                        animationMapper.get(entityTmp).pos.y += translate;
                    else
                        transformMapper.get(entityTmp).pos.y += translate;
                    break;
            }
        }
    }
}
