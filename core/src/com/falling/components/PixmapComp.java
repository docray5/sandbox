package com.falling.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

import static com.falling.Core.worldHeight;
import static com.falling.Core.worldWidth;

public class PixmapComp implements Component {
    public Pixmap pixmap = new Pixmap((int) worldWidth, (int) worldHeight, Pixmap.Format.RGBA8888);
    public Texture texture = new Texture((int) worldWidth, (int) worldHeight, Pixmap.Format.RGBA8888);
}
