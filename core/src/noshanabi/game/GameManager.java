package noshanabi.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import noshanabi.game.Screens.PlayScreen;

//manage audio, sprite, world width, world height, etc.
public class GameManager extends Game {

	//----FINAL VARIABLES-----//
	public static final int WORLDWIDTH = 800;
	public static final int WORLDHEIGHT = 400;
	public static final float PPM = 100f;

	//batch is used for draw everything into a screen
	//we just need one of this because we only have to draw on it over and over again
	public SpriteBatch batch;

	//Audio manager
	public AssetManager audioManager;

	@Override
	public void create() {
		batch = new SpriteBatch();

		//initial audio
		audioManager = new AssetManager();
		loadEssentialAudio();

		//set screen
		//setScreen(new MenuScreen(this));
		setScreen(new PlayScreen(this));
		//setScreen(new GameOverScreen(this));


	}

	//used for load essential audio
	public void loadEssentialAudio()
	{
		//example
		audioManager.load("audios/music.mp3", Music.class);
		audioManager.load("audios/sfx_wing.ogg",Sound.class);

	}

	@Override
	public void render () {
		super.render();
		audioManager.update();
	}

	@Override
	public void dispose () {
		if(batch!=null) {
			batch.dispose();
		}
		if(audioManager!=null) {
			audioManager.dispose();
		}
	}

}
