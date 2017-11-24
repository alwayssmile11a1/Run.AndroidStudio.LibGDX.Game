package noshanabi.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.VisUI;

import noshanabi.game.Extensions.PlayerServices;
import noshanabi.game.Screens.CreateRoomScreen;
import noshanabi.game.Screens.FindRoomScreen;
import noshanabi.game.Screens.LoginScreen;
import noshanabi.game.Screens.MapSelectionScreen;
import noshanabi.game.Screens.MenuScreen;
import noshanabi.game.Screens.ModeSelectionScreen;
import noshanabi.game.Screens.RoomJoinedScreen;
import noshanabi.game.Server.ServerCreator;

//manage audio, sprite, world width, world height, etc.
public class GameManager extends Game {

	//batch is used for draw everything into a screen
	//we just need one of this because we only have to draw on it over and over again
	public SpriteBatch batch;

	//Audio manager
	private AssetManager assetManager;

	//Login to facebook, login to google
	private PlayerServices playerServices;



	//---ALL SCREENS----//
	private MenuScreen menuScreen;
	private MapSelectionScreen mapSelectionScreen;
	private LoginScreen loginScreen;
	private ModeSelectionScreen modeSelectionScreen;
	private CreateRoomScreen createRoomScreen;
	private FindRoomScreen findRoomScreen;
	private RoomJoinedScreen roomJoinedScreen;

	//--SERVER--//
	private ServerCreator server;


	//==OTHERS--//
	private Array<Screen> disposeScreens;


	public GameManager(PlayerServices playerServices)
	{
		this.playerServices = playerServices;

	}

	@Override
	public void create() {

		batch = new SpriteBatch();
		disposeScreens = new Array<Screen>();

		VisUI.load(VisUI.SkinScale.X2);

		//init screens
		menuScreen = new MenuScreen(this);
		mapSelectionScreen = new MapSelectionScreen(this);
		loginScreen = new LoginScreen(this);
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
		assetManager = new AssetManager();
		loadEssentialAssets();

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
	public void loadEssentialAssets()
	{
		//load sound
		assetManager.load(Resourses.ExplosionSound, Sound.class);
		assetManager.load(Resourses.CheckpointSound, Sound.class);
	}

	public void addToDisposeScreens(Screen screen)
	{
		disposeScreens.add(screen);
	}

	@Override
	public void render () {
		super.render();
		assetManager.update();

		if(disposeScreens.size>0)
		{
			for (Screen screen:disposeScreens)
			{
				screen.dispose();
			}
			disposeScreens.clear();
		}

	}


	@Override
	public void dispose () {
		if(batch!=null) {
			batch.dispose();
		}
		if(assetManager !=null) {
			assetManager.dispose();
		}

		menuScreen.dispose();
		mapSelectionScreen.dispose();
		loginScreen.dispose();
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

	public AssetManager getAssetManager() {
		return assetManager;
	}

	public FindRoomScreen getFindRoomScreen() {
		return findRoomScreen;
	}

	public RoomJoinedScreen getRoomJoinedScreen() {
		return roomJoinedScreen;
	}
}
