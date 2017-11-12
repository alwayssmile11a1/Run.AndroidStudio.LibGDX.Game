package noshanabi.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import noshanabi.game.Extensions.PlayerServices;
import noshanabi.game.Screens.GameOverScreen;
import noshanabi.game.Screens.LoginScreen;
import noshanabi.game.Screens.MapSelectionScreen;
import noshanabi.game.Screens.MenuScreen;

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

	//Login to facebook, login to google
	public PlayerServices playerServices;



	//---ALL SCREENS----//
	public MenuScreen menuScreen;
	public MapSelectionScreen mapSelectionScreen;
	public LoginScreen loginScreen;
	public GameOverScreen gameOverScreen;


	public GameManager(PlayerServices playerServices)
	{
		this.playerServices = playerServices;

	}

	@Override
	public void create() {

		batch = new SpriteBatch();

		//init screens
		menuScreen = new MenuScreen(this);
		mapSelectionScreen = new MapSelectionScreen(this);
		loginScreen = new LoginScreen(this);
		gameOverScreen = new GameOverScreen(this);


		//initial audio
		audioManager = new AssetManager();
		loadEssentialAudio();

		//set screen
		Gdx.input.setInputProcessor(menuScreen.getStage());
		setScreen(menuScreen);


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

		menuScreen.dispose();
		mapSelectionScreen.dispose();
		loginScreen.dispose();
		gameOverScreen.dispose();

	}

	public MenuScreen getMenuScreen() {
		return menuScreen;
	}

	public MapSelectionScreen getMapSelectionScreen() {
		return mapSelectionScreen;
	}


	public LoginScreen getLoginScreen() {
		return loginScreen;
	}

	public GameOverScreen getGameOverScreen() {
		return gameOverScreen;
	}

}
