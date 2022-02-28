package org.delusion.elgame.desktop;


import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import org.delusion.elgame.ElGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.setFromDisplayMode(LwjglApplicationConfiguration.getDesktopDisplayMode());
//		config.width = 1920;
//		config.height = 1080;
		config.fullscreen = true;
		config.vSyncEnabled = false;
		config.foregroundFPS = 0;

		new LwjglApplication(new ElGame(), config);
	}
}
