package com.falling.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.Pool;

public class TextComponent implements Component, Pool.Poolable {
    public BitmapFont font;
    public GlyphLayout glyphLayout = new GlyphLayout();
    public String text = "";

    @Override
    public void reset() {
        font = null;
        text = "";
        glyphLayout.reset();
    }
}
