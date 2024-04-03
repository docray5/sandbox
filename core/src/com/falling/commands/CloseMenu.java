package com.falling.commands;

import com.falling.systems.UITranslateSystem;
import com.badlogic.ashley.core.Engine;

/**
 * CloseMenu
 */
public class CloseMenu extends Command {
   private final UITranslateSystem uiTranslateSystem;

    public CloseMenu(Engine engine) {
        super(engine);
        uiTranslateSystem = engine.getSystem(UITranslateSystem.class);
    }

    @Override
    public void execute() {
        uiTranslateSystem.openMenu();
    } 
}
