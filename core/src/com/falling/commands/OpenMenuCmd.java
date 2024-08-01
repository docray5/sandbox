package com.falling.commands;

import com.falling.systems.UITranslateSystem;

/**
 * OpenMenu
 */
public class OpenMenuCmd extends Command {
    private final UITranslateSystem uiTranslateSystem;

    public OpenMenuCmd() {
        uiTranslateSystem = engine.getSystem(UITranslateSystem.class);
    }

	@Override
	public void execute() {
        uiTranslateSystem.openMenu();
	}
}
