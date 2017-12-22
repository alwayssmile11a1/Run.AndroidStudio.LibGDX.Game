package noshanabi.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.ParticleEffectLoader;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.kotcrab.vis.ui.VisUI;

import java.io.IOException;

import noshanabi.game.Extensions.PlayerServices;
import noshanabi.game.Screens.CreateRoomScreen;
import noshanabi.game.Screens.FindRoomScreen;
import noshanabi.game.Screens.LoginScreen;
import noshanabi.game.Screens.MapSelectionScreen;
import noshanabi.game.Screens.MenuScreen;
import noshanabi.game.Screens.ModeSelectionScreen;
import noshanabi.game.Screens.PlayScreen;
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

	//PLAYER TEXTURE
	Texture charactersTexture;
	Array<TextureRegion> characterRegions;
	TextureRegion currentCharacter;

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

	private boolean needSwitchScreen = true;

	private Preferences prefs;

	public GameManager(PlayerServices playerServices)
	{
		this.playerServices = playerServices;

	}

	@Override
	public void create() {

		batch = new SpriteBatch();
		disposeScreens = new Array<Screen>();



		//Get Preferences to save our score
		prefs = Gdx.app.getPreferences("My Preferences");

		VisUI.load(VisUI.SkinScale.X2);

		//load characters texture
		setupCharacters();

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



	}

	public Preferences getPreferences()
	{
		return prefs;
	}

	private void setupCharacters()
	{
		charactersTexture = new Texture(Gdx.files.internal(Resourses.PlayerSheet));
		characterRegions = new Array<TextureRegion>();
		XmlReader reader = new XmlReader();
		try {
			XmlReader.Element root = reader.parse(Gdx.files.internal(Resourses.PlayerSheetXML));

			Array<XmlReader.Element> items = root.getChildrenByName("SubTexture");

			for(XmlReader.Element item:items)
			{
				int x =Integer.parseInt( item.getAttribute("x"));
				int y =Integer.parseInt( item.getAttribute("y"));
				int width =Integer.parseInt( item.getAttribute("width"));
				int height =Integer.parseInt( item.getAttribute("height"));

				TextureRegion region = new TextureRegion(charactersTexture,x,y,width,height);

				characterRegions.add(region);

			}

		}
		catch (IOException e)
		{
			Gdx.app.log("Something went wrong","");
		}

		currentCharacter = characterRegions.get(8);

	}

	public Array<TextureRegion> getCharacterRegions()
	{
		return characterRegions;
	}

	public TextureRegion getCurrentCharacter()
	{
		return currentCharacter;
	}

	public void setCurrentCharacter(TextureRegion textureRegion)
	{
		currentCharacter = textureRegion;
	}



	public void connectToServer()
	{
		server.connectSocket();
		server.configSocketEvents();
	}

	//used for load essential audio
	private void loadEssentialAssets()
	{
		//load sound
		assetManager.load(Resourses.ExplosionSound, Sound.class);
		assetManager.load(Resourses.CheckpointSound, Sound.class);
		assetManager.load(Resourses.ClickSound, Sound.class);

		//load effect
		ParticleEffectLoader.ParticleEffectParameter pep = new ParticleEffectLoader.ParticleEffectParameter();
		pep.imagesDir = Gdx.files.internal(Resourses.ParticleImageDir);
		assetManager.load(Resourses.ExplosionEffect1, ParticleEffect.class,pep);
		assetManager.load(Resourses.ExplosionEffect2, ParticleEffect.class,pep);
		assetManager.load(Resourses.ExplosionEffect3, ParticleEffect.class,pep);

//		//load json
//		assetManager.load(Resourses.ButtonSkinTextureAtlas, TextureAtlas.class);
//		SkinLoader.SkinParameter params = new SkinLoader.SkinParameter(Resourses.ButtonSkinTextureAtlas);
//		assetManager.load(Resourses.ButtonSkinJSON, Skin.class, params);


	}

	public void addToDisposeScreens(Screen screen)
	{
		disposeScreens.add(screen);
	}

	@Override
	public void render () {
		super.render();

		if(assetManager.update() && needSwitchScreen) //true if all loading is finish
		{

			needSwitchScreen = false;
			//scale effect
			assetManager.get(Resourses.ExplosionEffect1,ParticleEffect.class).scaleEffect(1/Resourses.PPM);
			assetManager.get(Resourses.ExplosionEffect2,ParticleEffect.class).scaleEffect(1/Resourses.PPM);
			assetManager.get(Resourses.ExplosionEffect3,ParticleEffect.class).scaleEffect(1/Resourses.PPM);

			//set screen
			Gdx.input.setInputProcessor(menuScreen.getStage());
			//setScreen(new PlayScreen(this, "maps/map0/map.tmx"));
			setScreen(menuScreen);
		}


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
		prefs.flush();


		if(batch!=null) {
			batch.dispose();
		}
		if(assetManager !=null) {
			assetManager.dispose();
		}

		if(menuScreen!=null) {
			menuScreen.dispose();
		}
		if(mapSelectionScreen!=null) {
			mapSelectionScreen.dispose();
		}
		if(loginScreen!=null) {
			loginScreen.dispose();
		}
		if(modeSelectionScreen!=null) {
			modeSelectionScreen.dispose();
		}
		if(findRoomScreen!=null) {
			findRoomScreen.dispose();
		}
		if(roomJoinedScreen!=null) {
			roomJoinedScreen.dispose();
		}

		//dispose current screen
		Screen currentScreen = getScreen();
		if(currentScreen!=null && currentScreen instanceof PlayScreen)
		{
			currentScreen.dispose();
			currentScreen = null;
		}

		if(server!=null) {
			server.dispose();
		}

		VisUI.dispose();

		if(charactersTexture!=null)
			charactersTexture.dispose();

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
