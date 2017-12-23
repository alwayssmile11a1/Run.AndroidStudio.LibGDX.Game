package noshanabi.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisTable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

import noshanabi.game.ButtonPrefabs.ReturnScreenButton;
import noshanabi.game.ButtonPrefabs.SignOutButton;
import noshanabi.game.GameManager;
import noshanabi.game.Resourses;
import noshanabi.game.Server.ServerListener;

/**
 * Created by 2SMILE2 on 16/11/2017.
 */

public class RoomJoinedScreen implements Screen, ServerListener {

    //viewport
    private Viewport viewport;

    //gameStage manage UI on it
    private Stage stage;

    //game manager
    private GameManager gameManager;

    //map count
    private int mapCount = 0;


    private Sprite backGround;

    //map
    Array<Image> mapImages;
    Array<Texture> mapTextures;

    private ReturnScreenButton returnScreenButton;

    private SignOutButton signOutButton;

    private Texture nextMapTexture;
    private Image nextMapButton;
    private Image previousMapButton;

    private int transitionUp;
    private int transitionDistance;
    private int transitionPosition;
    private int transitionSpeed;

    private VisTable playersTable;

    private Array<String> playersToAdd;

    private Array<String> playersToRemove;

    private HashMap<String, Image> players;

    private Group mapGroup;

    private boolean needSwitchScreen;

    private boolean ownRoom = false;

    //choose player table
    private Stage chooseCharacterStage;
    private VisTable chooseCharacterTable;
    private Texture chooseCharacterBackgroundTexture;
    private Image chooseCharacterBackground;


    public RoomJoinedScreen(GameManager _gameManager) {
        //set up constructor variables
        this.gameManager = _gameManager;

        playersToAdd = new Array<String>();
        playersToRemove = new Array<String>();

        transitionUp = -1;
        transitionDistance = 0;
        transitionPosition = 0;
        transitionSpeed = 20;

        backGround = new Sprite(new Texture(Resourses.RoomJoinedBackground));
        backGround.setSize(Resourses.WORLDWIDTH, Resourses.WORLDHEIGHT);
        //-------------------------------------------------------
        players = new HashMap<String, Image>();

        needSwitchScreen = false;

        //-----------------VIEW RELATED VARIABLES-----------------//
        viewport = new StretchViewport(Resourses.WORLDWIDTH, Resourses.WORLDHEIGHT);
        stage = new Stage(viewport, gameManager.batch);

        //-----------------MAP GROUP ---------------------
        setupMapGroup();

        //-----------------all players group -------------
        playersTable = new VisTable();

        playersTable.setPosition(50,50);
        playersTable.setSize(100,Resourses.WORLDHEIGHT-playersTable.getY()*2);
        stage.addActor(playersTable);



        //------------------RETURN BUTTON ----------------------
        Group group = new Group();

        //the return button
        returnScreenButton = new ReturnScreenButton();
        returnScreenButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                gameManager.getServer().getSocket().emit("leaveRoom");

                Gdx.input.setInputProcessor(gameManager.getModeSelectionScreen().getStage());
                gameManager.setScreen(gameManager.getModeSelectionScreen());
                return true;
            }

        });


        //add to group
        group.addActor(returnScreenButton);


        //------------------USER INFORMATION ----------------------
        Label.LabelStyle labelStyle = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
        Label userNameLabel = new Label("USER NAME", labelStyle);
        if (gameManager.getPlayerServices() != null && gameManager.getPlayerServices().isSignedIn()) {
            userNameLabel.setText(gameManager.getPlayerServices().getUserName());
        }
        userNameLabel.setPosition(Resourses.WORLDWIDTH - userNameLabel.getWidth() - 100, returnScreenButton.getY() + 15);

        group.addActor(userNameLabel);

        //------------------SIGN OUT BUTTON ------------------------
        signOutButton = new SignOutButton(gameManager);
        group.addActor(signOutButton);

        //add to actor
        stage.addActor(group);

        //-------------------------------
        setupChooseCharacterTable();
    }

    private void setupMapGroup()
    {
        //-----LOAD MAP TEXTURES AND MAP IMAGES
        //Create group
        mapGroup = new Group();

        mapTextures = new Array<Texture>();
        mapImages = new Array<Image>();

        //load textures
        while (true) {
            String textureFileName = "maps/map" + mapCount + "/maptexture.png";
            try {
                Texture mapTexture = new Texture(textureFileName);
                mapTextures.add(mapTexture);
                mapCount++;

            } catch (GdxRuntimeException e) {
                break;
            }
        }
        //load map images
        for (int i = 0; i < mapCount; i++) {
            Image mapImage = new Image(mapTextures.get(i));
            final String mapName = "maps/map" + i + "/map.tmx";
            final String backgroundMusicName = "maps/map" + i + "/backgroundmusic.mp3";
            mapImages.add(mapImage);
            mapImage.setBounds(0, 0, mapTextures.get(i).getWidth(), mapTextures.get(i).getHeight());
            mapImage.setTouchable(Touchable.enabled);
            mapImage.setPosition(250, 75 - Resourses.WORLDHEIGHT * i);
            mapImage.setSize(Resourses.WORLDWIDTH - 300, Resourses.WORLDHEIGHT - 150);
            mapImage.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    Gdx.app.log("SwitchToMap","");
                    MapSelectionScreen.MapInfo mapInfo = new MapSelectionScreen.MapInfo();
                    mapInfo.mapName = mapName;
                    PlayScreen playScreen = new PlayScreen(gameManager, mapInfo, backgroundMusicName);
                    playScreen.setServer(gameManager.getServer());
                    //Gdx.input.setInputProcessor(playScreen.getGameStage());
                    if(ownRoom) {
                        gameManager.getServer().getSocket().emit("joinGame");
                    }
                    gameManager.setScreen(playScreen);

                    return true;
                }

            });

            mapGroup.addActor(mapImages.get(i));
        }


        //--------------- NEXT MAP BUTTON --------------
        nextMapTexture = new Texture(Resourses.NextMapButton);
        nextMapButton = new Image(nextMapTexture);
        nextMapButton.setBounds(0, 0, nextMapTexture.getWidth(), nextMapTexture.getHeight());
        nextMapButton.setTouchable(Touchable.enabled);
        nextMapButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                if (transitionDistance == 0 && transitionPosition > 0) {
                    gameManager.getAssetManager().get(Resourses.ClickSound, Sound.class).play();
                    transitionUp = 1;
                    transitionPosition--;
                    gameManager.getServer().getSocket().emit("transitionMap",1);
                }
                return true;
            }

        });

        nextMapButton.setSize(50, 50);
        nextMapButton.setOrigin(nextMapButton.getWidth() / 2, nextMapButton.getHeight() / 2);
        nextMapButton.setRotation(90);
        nextMapButton.setPosition(mapImages.get(0).getX() + mapImages.get(0).getWidth() / 2 - nextMapButton.getWidth() / 2, Resourses.WORLDHEIGHT - 70);

        //add to group
        mapGroup.addActor(nextMapButton);


        //--------------------PREVIOUS MAP BUTTON --------------------------
        previousMapButton = new Image(nextMapTexture);
        previousMapButton.setBounds(0, 0, nextMapTexture.getWidth(), nextMapTexture.getHeight());
        previousMapButton.setTouchable(Touchable.enabled);
        previousMapButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (transitionDistance == 0 && transitionPosition < mapCount - 1) {
                    gameManager.getAssetManager().get(Resourses.ClickSound, Sound.class).play();
                    transitionUp = 0;
                    transitionPosition++;
                    gameManager.getServer().getSocket().emit("transitionMap",0);
                }
                return true;
            }

        });

        previousMapButton.setSize(50, 50);
        previousMapButton.setOrigin(previousMapButton.getWidth() / 2, previousMapButton.getHeight() / 2);
        previousMapButton.setRotation(-90);
        previousMapButton.setPosition(nextMapButton.getX(), 20);

        //add to group
        mapGroup.addActor(previousMapButton);


        //add to gameStage
        stage.addActor(mapGroup);


    }

    public void setupChooseCharacterTable()
    {
        chooseCharacterStage = new Stage();

        //------------------CHOOSE CHARACTER TABLE ---------------------//
        //get all player texture
        chooseCharacterTable = new VisTable();
        chooseCharacterTable.setFillParent(true);
        chooseCharacterTable.center();

        //background
        chooseCharacterBackgroundTexture = new Texture(Resourses.GameFinishedBackground);
        chooseCharacterBackground = new VisImage(chooseCharacterBackgroundTexture);
        chooseCharacterBackground.setSize(Resourses.WORLDWIDTH-25,Resourses.WORLDHEIGHT-25);
        chooseCharacterBackground.setPosition(Resourses.WORLDWIDTH/2- chooseCharacterBackground.getWidth()/2,Resourses.WORLDHEIGHT/2- chooseCharacterBackground.getHeight()/2);
        chooseCharacterBackground.setColor(0, 0, 0, 0.5f);
        chooseCharacterTable.addActor(chooseCharacterBackground);


        Array<TextureRegion> characterRegions = gameManager.getCharacterRegions();
        int index = 0;
        for(final TextureRegion region:characterRegions) {

            VisImage characterImage = new VisImage(region);
            characterImage.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    gameManager.setCurrentCharacter(region);
                    chooseCharacterTable.setVisible(false);
                    Gdx.input.setInputProcessor(stage);
                    players.get("main").setDrawable(new TextureRegionDrawable(region));
                    return true;
                }

            });
            chooseCharacterTable.add(characterImage).expandX();
            index++;
            if (index >= 4) {
                index = 0;
                chooseCharacterTable.row().padTop(50f);
            }
        }


        chooseCharacterStage.addActor(chooseCharacterTable);
        chooseCharacterTable.setVisible(false);
    }

    @Override
    public void OnSocketRoomCreated(Object... args) {

    }

    @Override
    public void OnRoomExisted(Object... args) {

    }

    @Override
    public void OnGetRooms(Object... args) {

    }

    @Override
    public void OnRoomCreated(Object... args) {

    }

    @Override
    public void OnRoomRemoved(Object... args) {

    }

    @Override
    public void OnGetMaxPlayersInRoom(Object ...args)
    {

    }

    @Override
    public void OnRoomStateChanged(Object... args) {

    }


    @Override
    public void OnGetOtherPlayers(Object... args) {

        JSONObject objects = (JSONObject) args[0];

        //get all the keys
        Iterator<String> iter = objects.keys();

        //loop through all
        while (iter.hasNext()) {
            String otherPlayerID = iter.next();

            playersToAdd.add(otherPlayerID);

        }
    }

    private void addPlayer(String playerID)
    {
        VisImage playerImage = new VisImage(gameManager.getCurrentCharacter());
        players.put(playerID, playerImage);
        playersTable.add(playerImage).padBottom(20f).size(50,50);
        playersTable.row();

        if(playerID.equals("main"))
        {
            playerImage.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    Gdx.input.setInputProcessor(chooseCharacterStage);
                    chooseCharacterTable.setVisible(true);
                    return true;
                }

            });
        }

    }

    private void removePlayer(String playerID)
    {
        //clear table to add again
        playersTable.clear();
        players.remove(playerID);

        for(HashMap.Entry<String,Image> entry : players.entrySet())
        {
            playersTable.add(entry.getValue()).padBottom(20f).size(50,50);
            playersTable.row();

        }
    }

    @Override
    public void OnRoomJoined(Object... args) {

        JSONObject data = (JSONObject) args[0];

        try {
            String id = data.getString("id");
            playersToAdd.add(id);

        } catch (JSONException e) {
            Gdx.app.log("SocketIO", "Error getting ID");
            e.printStackTrace();
        }

    }

    @Override
    public void OnSocketRoomJoined(Object... args) {
        playersToAdd.add("main");
    }

    @Override
    public void OnSocketRoomLeaved(Object... args) {
        playersTable.clear();
        playersToRemove.clear();
        playersToRemove.clear();
        players.clear();
    }

    @Override
    public void OnRoomLeaved(Object... args) {

        try {
            JSONObject data = (JSONObject) args[0];
            String id = data.getString("id");
            playersToRemove.add(id);

        } catch (JSONException e) {
            Gdx.app.log("SocketIO", "Error getting ID");
            e.printStackTrace();
        }


    }

    @Override
    public void OnUnableToJoinRoom(Object... args) {

    }

    @Override
    public void OnPlayersCountChanged(Object ...args)
    {

    }

    @Override
    public void OnGameJoined(Object... args) {
        needSwitchScreen = true;
    }

    @Override
    public void OnMapTransitioned(Object... args)
    {
        try {
            JSONObject data = (JSONObject) args[0];
            transitionUp = data.getInt("transitionUp");

            if(transitionUp==0) {
                transitionPosition++;
            }
            else
            {
                transitionPosition--;
            }

        } catch (JSONException e) {
            Gdx.app.log("SocketIO", "Error transitioning map");
            e.printStackTrace();
        }

    }


    public void ownRoomMode(boolean ownRoom)
    {
        this.ownRoom = ownRoom;
        if(ownRoom)
        {
            mapGroup.setTouchable(Touchable.enabled);
            nextMapButton.setVisible(true);
            previousMapButton.setVisible(true);
        }
        else
        {
            mapGroup.setTouchable(Touchable.disabled);
            nextMapButton.setVisible(false);
            previousMapButton.setVisible(false);
        }
    }

    @Override
    public void render(float delta) {
        //color to clear this screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if(playersToAdd.size>0)
        {
            for(String player: playersToAdd) {
                addPlayer(player);

            }
            playersToAdd.clear();
        }

        if(playersToRemove.size>0)
        {
            for(String player: playersToRemove) {
                removePlayer(player);

            }
            playersToRemove.clear();
        }

        if(needSwitchScreen==true) {
            needSwitchScreen = false;
            //perform touch down event
            InputEvent event = new InputEvent();
            event.setType(InputEvent.Type.touchDown);
            mapImages.get(transitionPosition).fire(event);
        }


        //transition map
        if (transitionUp !=-1) {
            if (transitionUp == 0) {
                for (Image map : mapImages) {
                    map.setPosition(map.getX(), map.getY() + transitionSpeed);
                }

            } else {

                for (Image map : mapImages) {
                    map.setPosition(map.getX(), map.getY() - transitionSpeed);

                }
            }
            transitionDistance += transitionSpeed;

            if (transitionDistance >= Resourses.WORLDHEIGHT) {
                transitionDistance = 0;
                transitionUp = -1;
            }

        }

        //draw sprite
        gameManager.batch.begin();

        backGround.draw(gameManager.batch);

        gameManager.batch.end();

        signOutButton.update(delta);

        stage.draw();
        stage.act();
        chooseCharacterStage.draw();
        chooseCharacterStage.act();
    }


    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    public Stage getStage() {
        return stage;
    }

    @Override
    public void show() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }


    @Override
    public void dispose() {
        if(stage!=null)
        {
            stage.dispose();
        }

        for(int i=0;i<mapCount;i++) {
            mapTextures.get(i).dispose();
        }

        if(backGround.getTexture()!=null)
        {
            backGround.getTexture().dispose();
        }

        if(returnScreenButton!=null)
            returnScreenButton.dispose();

        if(nextMapTexture!=null)
            nextMapTexture.dispose();

        if(signOutButton!=null)
            signOutButton.dispose();

        if(chooseCharacterBackgroundTexture !=null)
            chooseCharacterBackgroundTexture.dispose();

        if(chooseCharacterStage!=null) {
            chooseCharacterStage.dispose();
            chooseCharacterStage = null;
        }

        //Gdx.app.log("dispose","Room joined screen");

    }

}
