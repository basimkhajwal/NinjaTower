package net.net63.codearcade.NinjaTower.desktop;

import net.net63.codearcade.NinjaTower.GameMain;
import net.net63.codearcade.NinjaTower.utils.Constants;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		config.title = Constants.GAME_TITLE;
		
		config.resizable = false;
		
		config.width = Constants.DESKTOP_GAME_WIDTH;
		config.height = Constants.DESKTOP_GAME_HEIGHT;
		
		config.vSyncEnabled = true;
		
		new LwjglApplication(new GameMain(), config);
	}
}
