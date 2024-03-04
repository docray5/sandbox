package com.falling.utils;

import com.badlogic.ashley.core.Entity;

import java.util.Comparator;

import static com.falling.utils.Mappers.renderableMapper;

public class PriorityComparator implements Comparator<Entity> {
    @Override
    public int compare(Entity entity1, Entity entity2) {
        return renderableMapper.get(entity1).priority - renderableMapper.get(entity2).priority;
    }
}
