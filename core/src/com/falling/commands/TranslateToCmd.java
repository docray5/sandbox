package com.falling.commands;

import static com.falling.utils.Mappers.animationMapper;
import static com.falling.utils.Mappers.transformMapper;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
import com.falling.components.VecAnimatorComp;
import com.falling.components.VecAnimatorComp.AnimatorType;
import com.falling.components.TransformComp;

public class TranslateToCmd extends Command {
    private float x;
    private float y;
    private TransformComp transCompTmp;
    private Array<VecAnimatorComp> animatorsTmp;
    private boolean additive;

    public TranslateToCmd(Entity entity, float transToX, float transToY, boolean additive) {
        super(entity);
        x = transToX;
        y = transToY;
        this.additive = additive;
    }

    @Override
    public void execute() {
        if (animationMapper.has(entity)) {
            animatorsTmp = animationMapper.get(entity).animators;

            for (int i = animatorsTmp.size-1; i >= 0; i--) {
                if (animatorsTmp.get(i).type == AnimatorType.POS) {
                    if (additive)
                        animatorsTmp.get(i).animatedVecPointer.set(animatorsTmp.get(i).animatedVecPointer.x + x, animatorsTmp.get(i).animatedVecPointer.y + y);
                    else
                        animatorsTmp.get(i).animatedVecPointer.set(x, y);
                }
            }
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
