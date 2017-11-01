package noshanabi.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import noshanabi.game.GameManager;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = GameManager.WORLDWIDTH;
		config.height = GameManager.WORLDHEIGHT;
		new LwjglApplication(new GameManager(), config);
	}
}
