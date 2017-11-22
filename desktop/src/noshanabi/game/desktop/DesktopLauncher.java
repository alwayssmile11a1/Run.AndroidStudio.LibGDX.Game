package noshanabi.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import noshanabi.game.GameManager;
import noshanabi.game.Resourses;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = Resourses.WORLDWIDTH;
		config.height = Resourses.WORLDHEIGHT;
		new LwjglApplication(new GameManager(null), config);
	}
}
