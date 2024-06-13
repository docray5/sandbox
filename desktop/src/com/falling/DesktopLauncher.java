package com.falling;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import static com.falling.Core.*;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Leap!");
		config.setForegroundFPS(fps);
		config.setIdleFPS(10);
		// config.setWindowIcon("icon.png");
		// config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
		config.setWindowedMode(screenWidth, screenHeight);
        // config.setTransparentFramebuffer(true);
        // config.setDecorated(false);

		new Lwjgl3Application(new Application(), config);
	}
}
