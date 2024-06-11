package com.falling.commands;

import static com.falling.utils.Mappers.posAnimationMapper;
import static com.falling.utils.Mappers.transformMapper;

import com.badlogic.ashley.core.Engine;
import com.falling.components.PosAnimationComponent;
import com.falling.components.TransformComponent;

public class TranslateToCmd extends Command {
    private float x;
    private float y;
    private TransformComponent transCompTmp;
    private PosAnimationComponent posAnimCompTmp;
    private boolean additive;

    public TranslateToCmd(Engine engine, float transToX, float transToY, boolean additive) {
        super(engine);
        x = transToX;
        y = transToY;
        this.additive = additive;
    }

    @Override
    public void execute() {
        if (posAnimationMapper.has(entity)) {
            posAnimCompTmp = posAnimationMapper.get(entity);
            if (additive)
                posAnimCompTmp.pos.set(posAnimCompTmp.pos.x + x, posAnimCompTmp.pos.y + y);
            else
                posAnimCompTmp.pos.set(x, y);
        } else if (transformMapper.has(entity)) {
            transCompTmp = transformMapper.get(entity);
            if (additive)
                transCompTmp.pos.set(transCompTmp.pos.x + x, transCompTmp.pos.y + y);
            else
            transCompTmp.pos.set(x, y);
        }
    }

    public TranslateToCmd setTransToXY(float transToX, float transToY, boolean additive) {
        x = transToX;
        y = transToY;
        this.additive = additive;
        return this;
    }
}
