package com.falling.factories.builders;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.graphics.Color;
import com.falling.components.RenderableComp;
import com.falling.factories.Factory;

public class RenderableBuilder {
    private RenderableComp renderableComponent;
    private final Engine engine;
    private boolean creating;
    private final Factory factory;

    public RenderableBuilder(Engine engine, Factory factory) {
        this.engine = engine;
        this.factory = factory;
    }

    public RenderableBuilder createComponent() {
        if (creating) return null;
        renderableComponent = engine.createComponent(RenderableComp.class);
        creating = true;
        return this;
    }

    public Factory endComponent() {
        if (!creating) return null;
        factory.getEntity().add(renderableComponent);
        creating = false;
        return factory;
    }

    public RenderableBuilder setRender(boolean render) {
        if (!creating) return null;
        renderableComponent.render = render;
        return this;
    }

    public RenderableBuilder setAfterVfx(boolean afterVfx) {
        if (!creating) return null;
        renderableComponent.afterVfx = afterVfx;
        return this;
    }

    public RenderableBuilder setCenter(boolean center) {
        if (!creating) return null;
        renderableComponent.center = center;
        return this;
    }

    public RenderableBuilder setColor(Color color) {
        if (!creating) return null;
        renderableComponent.color.set(color);
        return this;
    }

    public RenderableBuilder setPriority(int priority) {
        if (!creating) return null;
        renderableComponent.priority = priority;
        return this;
    }
}
