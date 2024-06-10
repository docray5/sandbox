package com.falling.commands;

import static com.falling.utils.Mappers.posAnimationMapper;
import static com.falling.utils.Mappers.transformMapper;

import com.badlogic.ashley.core.Engine;
import com.falling.components.PosAnimationComponent;
import com.falling.components.TransformComponent;

public class TranslateByXY extends Command {
    private float x;
    private float y;
    private TransformComponent transCompTmp;
    private PosAnimationComponent posAnimCompTmp;

	public TranslateByXY(Engine engine, float transByX, float transByY) {
		super(engine);
        x = transByX;
        y = transByY;
	}

	@Override
	public void execute() {
        if (posAnimationMapper.has(entity)) {
            posAnimCompTmp = posAnimationMapper.get(entity);
            posAnimCompTmp.pos.set(posAnimCompTmp.pos.x + x, posAnimCompTmp.pos.y + y);
        } else if (transformMapper.has(entity)) {
            transCompTmp = transformMapper.get(entity);
            transCompTmp.pos.set(transCompTmp.pos.x + x, transCompTmp.pos.y + y);
        }
	}

    public TranslateByXY setXY(float transByX, float transByY) {
        x = transByX;
        y = transByY;
        return this;
    }

}
