package com.falling.utils;

import com.badlogic.ashley.core.Entity;

import java.util.Comparator;

import static com.falling.utils.Mappers.clickableMapper;

public class ZComparator implements Comparator<Entity> {
    @Override
    public int compare(Entity entity1, Entity entity2) {
        return clickableMapper.get(entity1).priority - clickableMapper.get(entity2).priority;
    }
}
