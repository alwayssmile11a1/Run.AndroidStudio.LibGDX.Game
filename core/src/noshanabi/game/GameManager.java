package noshanabi.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.kotcrab.vis.ui.VisUI;

import noshanabi.game.Extensions.PlayerServices;
import noshanabi.game.Screens.CreateRoomScreen;
import noshanabi.game.Screens.FindRoomScreen;
import noshanabi.game.Screens.GameOverScreen;
import noshanabi.game.Screens.LoginScreen;
import noshanabi.game.Screens.MapSelectionScreen;
import noshanabi.game.Screens.MenuScreen;
import noshanabi.game.Screens.ModeSelectionScreen;
import noshanabi.game.Screens.RoomJoinedScreen;
import noshanabi.game.Server.ServerCreator;

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
	private AssetManager audioManager;

	//Login to facebook, login to google
	private PlayerServices playerServices;



	//---ALL SCREENS----//
	private MenuScreen menuScreen;
	private MapSelectionScreen mapSelectionScreen;
	private LoginScreen loginScreen;
	private GameOverScreen gameOverScreen;
	private ModeSelectionScreen modeSelectionScreen;
	private CreateRoomScreen createRoomScreen;
	private FindRoomScreen findRoomScreen;
	private RoomJoinedScreen roomJoinedScreen;

	//--SERVER--//
	private ServerCreator server;


	public GameManager(PlayerServices playerServices)
	{
		this.playerServices = playerServices;

	}

	@Override
	public void create() {

		batch = new SpriteBatch();

		VisUI.load(VisUI.SkinScale.X2);

		//init screens
		menuScreen = new MenuScreen(this);
		mapSelectionScreen = new MapSelectionScreen(this);
		loginScreen = new LoginScreen(this);
		gameOverScreen = new GameOverScreen(this);
		modeSelectionScreen = new ModeSelectionScreen(this);
		createRoomScreen = new CreateRoomScreen(this);
		findRoomScreen = new FindRoomScreen(this);
		roomJoinedScreen = new RoomJoinedScreen(this);

		//init server
		server = new ServerCreator(this);

		//add server listener
		server.addServerListener(findRoomScreen);
		server.addServerListener(createRoomScreen);
		server.addServerListener(roomJoinedScreen);

		//init audio
		audioManager = new AssetManager();
		loadEssentialAudio();

		//set screen
		Gdx.input.setInputProcessor(menuScreen.getStage());
		//setScreen(new PlayScreen(this, "maps/map0/map.tmx"));
		setScreen(menuScreen);


	}

	public void connectToServer()
	{
		server.connectSocket();
		server.configSocketEvents();
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
		modeSelectionScreen.dispose();
		findRoomScreen.dispose();
		roomJoinedScreen.dispose();

		if(getScreen()!=null)
		{
			getScreen().dispose();
		}

		if(server!=null) {
			server.dispose();
		}

		VisUI.dispose();

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

	public ModeSelectionScreen getModeSelectionScreen() {
		return modeSelectionScreen;
	}

	public CreateRoomScreen getCreateRoomScreen() {
		return createRoomScreen;
	}

	public ServerCreator getServer() {
		return server;
	}

	public PlayerServices getPlayerServices() {
		return playerServices;
	}

	public AssetManager getAudioManager() {
		return audioManager;
	}

	public FindRoomScreen getFindRoomScreen() {
		return findRoomScreen;
	}

	public RoomJoinedScreen getRoomJoinedScreen() {
		return roomJoinedScreen;
	}
}
