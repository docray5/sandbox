package com.falling.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Pool;
import com.falling.commands.Command;

public class ButtonComponent implements Component, Pool.Poolable {
    public Command onTouchUpCommand = null;
    public Command onTouchDownCommand = null;
    public Command onClickCommand = null;
    public AnimationType onMouseAnim = null;
    public AnimationType onTouchAnim = null;
    public Texture normalTexture = null;
    public Texture clickedTexture = null;

    @Override
    public void reset() {
        onTouchUpCommand = null;
        onTouchDownCommand = null;
        onClickCommand = null;
        onMouseAnim = null;
        normalTexture = null;
        clickedTexture = null;
    }

    public enum AnimationType {
        TRANSLATE, SCALE, OUTLINE, TEXTURE
    }
}
