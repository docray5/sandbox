package com.falling.commands;

import com.falling.systems.UITranslateSystem;

/**
 * PrepMenu
 */
public class PrepMenuCmd extends Command {
    private final UITranslateSystem uiTranslateSystem;

    public PrepMenuCmd() {
        uiTranslateSystem = engine.getSystem(UITranslateSystem.class);
    }

	@Override
	public void execute() {
        uiTranslateSystem.openMenu();
	}
}
