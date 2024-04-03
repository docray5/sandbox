package com.falling;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class Core {
    public static Vector2 mousePos = new Vector2();
    public static final float startWorldWidth = 200;
    public static final float startWorldHeight = 200;
    public static final Vector3 cameraPos = new Vector3();
    public static boolean touchHeld = false;

    /** Used to control blur, set to true if updating something thats under blur */
    public static boolean updateBlur = false;

    public static float worldWidth = startWorldWidth;
    public static float worldHeight = startWorldHeight;
    public static int screenWidth = (int) worldWidth * 4;
    public static int screenHeight = (int) worldHeight * 4;
    public static float lrGutter = 0;
    public static float tbGutter = 0;
    public static float xGutOffset = 0;
    public static float yGutOffset = 0;

    public static final float[] sandColor = { -33, 0.285f, 0.965f };
}
