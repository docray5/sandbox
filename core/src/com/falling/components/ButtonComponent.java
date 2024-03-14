package com.falling.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Pool;
import com.falling.events.Event;

public class ButtonComponent implements Component, Pool.Poolable {
    public Event onClickEvent = null;
    public Family familyToAlert = null;
    public AnimationType onMouse = null;
    public AnimationType onTouch = null;
    public Texture normalTexture = null;
    public Texture clickedTexture = null;
    public boolean holdable = false;

    @Override
    public void reset() {
        onClickEvent = null;
        familyToAlert = null;
        onMouse = null;
        normalTexture = null;
        clickedTexture = null;
        holdable = false;
    }

    public enum AnimationType {
        TRANSLATE, SCALE, OUTLINE, TEXTURE
    }
}
