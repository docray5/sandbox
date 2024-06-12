package com.falling.commands;

import com.badlogic.ashley.core.Engine;
import com.falling.systems.UITranslateSystem;

/**
 * OpenMenu
 */
public class OpenMenuCmd extends Command {
    private final UITranslateSystem uiTranslateSystem;

    public OpenMenuCmd(Engine engine) {
        super(engine);
        uiTranslateSystem = engine.getSystem(UITranslateSystem.class);
    }

	@Override
	public void execute() {
        uiTranslateSystem.openMenu();
	}
}
