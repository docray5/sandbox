package com.falling.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.EntitySystem;

public class PauseComponent implements Component {
    public Class<?extends EntitySystem> system;
}
