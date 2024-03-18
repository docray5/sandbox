package com.falling.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Pool;
import com.falling.events.Event;

public class ButtonComponent implements Component, Pool.Poolable {
    public Event onTouchUpEvent = null;
    public Event onTouchDownEvent = null;
    public Event onClickEvent = null;
    public AnimationType onMouse = null;
    public AnimationType onTouch = null;
    public Texture normalTexture = null;
    public Texture clickedTexture = null;

    @Override
    public void reset() {
        onTouchUpEvent = null;
        onTouchDownEvent = null;
        onClickEvent = null;
        onMouse = null;
        normalTexture = null;
        clickedTexture = null;
    }

    public enum AnimationType {
        TRANSLATE, SCALE, OUTLINE, TEXTURE
    }
}
