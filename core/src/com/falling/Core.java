package com.falling;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class Core {
    public static Vector2 mousePos = new Vector2();
    public static final float startWorldWidth = 400; // 400
    public static final float startWorldHeight = 220; // 220
    public static final Vector3 cameraPos = new Vector3();
    public static Matrix4 cameraMatrixTemp = new Matrix4(); // TEMP FOR TESTING PURPOSES

    /** Used to control blur, set to true if updating something thats under blur */
    public static boolean updateBlur = true;

    public static float worldWidth = startWorldWidth;
    public static float worldHeight = startWorldHeight;
    public static int screenWidth = (int) worldWidth * 4;
    public static int screenHeight = (int) worldHeight * 4;
    public static float lrGutter = 0;
    public static float tbGutter = 0;
    public static float xGutOffset = 0;
    public static float yGutOffset = 0;
    public static float windowScaleX = screenWidth/worldWidth;
    public static float windowScaleY = screenHeight/worldHeight;

    /** makes screen look more like pixel art */
    public static boolean pixelate = false;
    public static int fps = 60;

    public static final float[] sandColor = { -33, 0.285f, 0.965f };
}
