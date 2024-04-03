package com.falling.factories.builders;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.falling.commands.Command;
import com.falling.components.AnimationComponent;
import com.falling.factories.Factory;

public class AnimationBuilder {
    private AnimationComponent animationComponent;
    private final Engine engine;
    private boolean creating;
    private final Factory factory;

    public AnimationBuilder(Engine engine, Factory factory) {
        this.engine = engine;
        this.factory = factory;
    }

    public AnimationBuilder createAnimationComponent() {
        if (creating) return null;
        animationComponent = engine.createComponent(AnimationComponent.class);
        creating = true;
        return this;
    }

    public Factory endComponent() {
        if (!creating) return null;
        factory.getEntity().add(animationComponent);
        creating = false;
        return factory;
    }

    public AnimationBuilder setPos(float x, float y) {
        if (!creating) return null;
        animationComponent.pos.set(x, y);
        return this;
    }

    public AnimationBuilder setPos(Vector2 pos) {
        if (!creating) return null;
        animationComponent.pos.set(pos);
        return this;
    }

    public AnimationBuilder setTarget(float x, float y) {
        if (!creating) return null;
        animationComponent.target.set(x, y);
        return this;
    }

    public AnimationBuilder setTarget(Vector2 target) {
        if (!creating) return null;
        animationComponent.target.set(target);
        return this;
    }

    public AnimationBuilder setAnimating(boolean animating) {
        if (!creating) return null;
        animationComponent.isAnimating = animating;
        return this;
    }

    public AnimationBuilder setAccel(float accel) {
        if (!creating) return null;
        animationComponent.accel = accel;
        return this;
    }

    public AnimationBuilder setLoopMode() {
        if (!creating) return null;
        animationComponent.mode = AnimationComponent.AnimationMode.LOOP;
        return this;
    }

    public AnimationBuilder setPingPongMode(float x, float y) {
        if (!creating) return null;
        animationComponent.mode = AnimationComponent.AnimationMode.PING_PONG;
        animationComponent.pingPongPos.set(x, y);
        return this;
    }

    public AnimationBuilder setPingPongMode(Vector2 pingPongPos) {
        if (!creating) return null;
        animationComponent.mode = AnimationComponent.AnimationMode.PING_PONG;
        animationComponent.pingPongPos.set(pingPongPos);
        return this;
    }

    public AnimationBuilder setChainPingPongMode(Array<Vector2> chain) {
        if (!creating) return null;
        animationComponent.mode = AnimationComponent.AnimationMode.CHAIN_PING_PONG;
        animationComponent.chain = chain;
        return this;
    }

    public AnimationBuilder setChainMode(Array<Vector2> chain) {
        if (!creating) return null;
        animationComponent.mode = AnimationComponent.AnimationMode.CHAIN;
        animationComponent.chain = chain;
        return this;
    }

    public AnimationBuilder setRounding(float rounding) {
        if (!creating) return null;
        animationComponent.rounding = rounding;
        return this;
    }

    public AnimationBuilder setInterpolation(Interpolation interpolation) {
        if (!creating) return null;
        animationComponent.interpolation = interpolation;
        return this;
    }

    public AnimationBuilder setCommandOnFinish(Command command) {
        if (!creating) return null;
        animationComponent.commandOnFinish = command;
        return this;
    }
}
