package com.falling.events;

public enum Event {
    // ========= Input: =========
    TOUCH_DOWN, TOUCH_UP, MOUSE_EXIT, MOUSE_ENTER, CLICKED,
    // ========= Screen, System Pausing, VFX: =========
    SHAKE_SCREEN, PAUSE_SYSTEM, RESUME_SYSTEM, SHIFT_BLUR,
    // ========= SFX: =========
    PLAY_HIT,
    // ========= UI =========
    OPEN_MENU, CLOSE_MENU, PREP_MENU,
    // ========= GAME =========
    SPAWN_PARTICLE, SEL_SAND, SEL_WATER
}
