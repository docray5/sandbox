package com.falling.utils;

import com.badlogic.ashley.core.Entity;

import java.util.Comparator;

import static com.falling.utils.Mappers.buttonMapper;
import static com.falling.utils.Mappers.renderableMapper;

public class ZComparator implements Comparator<Entity> {
    @Override
    public int compare(Entity entity1, Entity entity2) {
        return buttonMapper.get(entity1).priority - buttonMapper.get(entity2).priority;
    }
}
