package com.falling.commands;

import com.falling.systems.UITranslateSystem;

/**
 * CloseMenu
 */
public class CloseMenuCmd extends Command {
   private final UITranslateSystem uiTranslateSystem;

    public CloseMenuCmd() {
        uiTranslateSystem = engine.getSystem(UITranslateSystem.class);
    }

    @Override
    public void execute() {
        uiTranslateSystem.openMenu();
    } 
}
