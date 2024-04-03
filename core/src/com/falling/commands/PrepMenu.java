package com.falling.commands;

import com.badlogic.ashley.core.Engine;
import com.falling.systems.UITranslateSystem;

/**
 * PrepMenu
 */
public class PrepMenu extends Command {
    private final UITranslateSystem uiTranslateSystem;

    public PrepMenu(Engine engine) {
        super(engine);
        uiTranslateSystem = engine.getSystem(UITranslateSystem.class);
    }

	@Override
	public void execute() {
        uiTranslateSystem.openMenu();
	}
}
