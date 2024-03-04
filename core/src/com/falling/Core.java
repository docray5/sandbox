package com.falling;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class Core {
    public static Vector2 mousePos = new Vector2();
    public static final float startWorldWidth = 100;
    public static final float startWorldHeight = 100;
    public static final Vector3 cameraPos = new Vector3();

    public static float worldWidth = 100;
    public static float worldHeight = 100;
    public static int screenWidth = (int) worldWidth * 8;
    public static int screenHeight = (int) worldHeight * 8;
    public static float lrGutter = 0;
    public static float tbGutter = 0;
}
