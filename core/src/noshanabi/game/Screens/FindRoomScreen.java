package noshanabi.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisScrollPane;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

import noshanabi.game.ButtonPrefabs.ReturnScreenButton;
import noshanabi.game.ButtonPrefabs.SignOutButton;
import noshanabi.game.GameManager;
import noshanabi.game.Server.ServerListener;

/**
 * Created by 2SMILE2 on 13/11/2017.
 */

public class FindRoomScreen implements Screen, ServerListener{

    //viewport
    private Viewport viewport;

    //stage manage UI on it
    private Stage stage;

    //game manager
    private GameManager gameManager;

    private Sprite backGround;

    private ReturnScreenButton returnScreenButton;

    private SignOutButton signOutButton;

    private Table roomTable;

    private HashMap<String, Label> roomList;

    private Array<String> roomsToAdd;

    private Array<String> roomsToRemove;

    private boolean isRoomFull;

    private boolean needSwitchScreen;

    public FindRoomScreen(GameManager _gameManager) {
        //set up constructor variables
        this.gameManager = _gameManager;

        //color to clear this screen
        Gdx.gl.glClearColor(0, 0, 0, 1);

        backGround = new Sprite(new Texture("images/BlueBackground.png"));
        backGround.setSize(GameManager.WORLDWIDTH, GameManager.WORLDHEIGHT);

        roomsToAdd = new Array<String>();
        roomsToRemove = new Array<String>();

        isRoomFull = true;
        needSwitchScreen = false;

        //-----------------VIEW RELATED VARIABLES-----------------//
        viewport = new StretchViewport(GameManager.WORLDWIDTH, GameManager.WORLDHEIGHT);
        stage = new Stage(viewport, gameManager.batch);


        //--------------------RETURN BUTTON -----------------------
        //Group allow to place an actor wherever we want
        Group group = new Group();

        returnScreenButton = new ReturnScreenButton();
        returnScreenButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.input.setInputProcessor(gameManager.getModeSelectionScreen().getStage());
                gameManager.setScreen(gameManager.getModeSelectionScreen());
                return true;
            }

        });

        //add to group
        group.addActor(returnScreenButton);

        //------------------SIGN OUT BUTTON ------------------------
        signOutButton = new SignOutButton();
        signOutButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                if (gameManager.getPlayerServices() != null) {
                    gameManager.getPlayerServices().signOut();
                }
                Gdx.input.setInputProcessor(gameManager.getLoginScreen().getStage());
                gameManager.setScreen(gameManager.getLoginScreen());
                return true;
            }

        });
        //add to group
        group.addActor(signOutButton);

        //------------------USER INFORMATION ----------------------
        VisLabel userNameLabel = new VisLabel("USER NAME");
        userNameLabel.setFontScale(0.5f);
        if (gameManager.getPlayerServices() != null) {
            userNameLabel.setText(gameManager.getPlayerServices().getUserName());
        }
        userNameLabel.setPosition(gameManager.WORLDWIDTH - userNameLabel.getWidth(), returnScreenButton.getY() + 15);

        group.addActor(userNameLabel);


        //add to stage
        stage.addActor(group);


        //---------------ROOM LIST----------------------------------
        roomList = new HashMap<String, Label>();
        roomTable = new Table();

        VisScrollPane scrollPane = new VisScrollPane(roomTable);
        scrollPane.setSize(gameManager.WORLDWIDTH/1.5f,gameManager.WORLDHEIGHT-100);
        scrollPane.setPosition(gameManager.WORLDWIDTH/2-scrollPane.getWidth()/2,50);

        stage.addActor(scrollPane);


    }

    @Override
    public void OnSocketRoomCreated(Object... args) {
        try {

            JSONObject data = (JSONObject) args[0];
            String roomName = data.getString("roomName");
            addRoom(roomName);

        } catch (JSONException e) {
            Gdx.app.log("SocketIO", "Error adding room");
            e.printStackTrace();
        }
    }

    @Override
    public void OnRoomExisted(Object... args) {

    }

    @Override
    public void OnRoomJoined(Object... args) {

    }

    @Override
    public void OnGetOtherPlayers(Object... args) {

    }

    @Override
    public void OnSocketRoomJoined(Object... args) {
        isRoomFull = false;
    }

    @Override
    public void OnGetRooms(Object... args) {
        JSONObject objects = (JSONObject) args[0];

        //get all the keys
        Iterator<String> iter = objects.keys();

        //loop through all to get all room names
        while (iter.hasNext()) {
            String roomName = iter.next();
            roomsToAdd.add(roomName);
        }
    }

    @Override
    public void OnRoomCreated(Object... args) {
        try {

            JSONObject data = (JSONObject) args[0];
            String roomName = data.getString("roomName");
            roomsToAdd.add(roomName);

        } catch (JSONException e) {
            Gdx.app.log("SocketIO", "Error adding room");
            e.printStackTrace();
        }
    }

    @Override
    public void OnRoomRemoved(Object... args) {
        try {
            JSONObject data = (JSONObject) args[0];
            String roomName = data.getString("roomName");
            roomsToRemove.add(roomName);

        } catch (JSONException e) {
            Gdx.app.log("SocketIO", "Error removing room");
            e.printStackTrace();
        }
    }

    @Override
    public void OnSocketRoomLeaved(Object... args) {

    }

    @Override
    public void OnRoomLeaved(Object... args) {

    }

    @Override
    public void OnRoomFull(Object... args) {
        isRoomFull = true;
    }

    @Override
    public void OnGameJoined(Object... args) {

    }

    private void addRoom(final String roomName)
    {
        VisLabel roomLabel = new VisLabel(roomName);
        roomLabel.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                try {
                    JSONObject data = new JSONObject();
                    data.put("roomName", roomName);
                    gameManager.getServer().getSocket().emit("joinRoom", data);
                    needSwitchScreen = true;

                }
                catch (JSONException e)
                {
                    Gdx.app.log("SOCKET.IO", "Error joining room");
                }

                return true;
            }
        });

        roomTable.add(roomLabel).expandX();
        roomTable.row();

        //add ro list
        roomList.put(roomName,roomLabel);
    }

    private void removeRoom(String roomName)
    {
        roomTable.removeActor(roomList.get(roomName));
        roomList.remove(roomName);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if(roomsToAdd.size > 0)
        {
            for(String roomName: roomsToAdd) {
                addRoom(roomName);
            }
            roomsToAdd.clear();
        }

        if(roomsToRemove.size>0)
        {
            for(String roomName: roomsToRemove) {
                removeRoom(roomName);
            }
            roomsToRemove.clear();
        }

        if(!isRoomFull && needSwitchScreen)
        {
            RoomJoinedScreen roomJoinedScreen = gameManager.getRoomJoinedScreen();
            Gdx.input.setInputProcessor(roomJoinedScreen.getStage());
            roomJoinedScreen.ownRoomMode(false);
            gameManager.setScreen(roomJoinedScreen);
            needSwitchScreen = false;
        }

        //draw sprite
        gameManager.batch.begin();

        backGround.draw(gameManager.batch);

        gameManager.batch.end();

        stage.draw();

        stage.act();
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

        if(backGround.getTexture()!=null)
        {
            backGround.getTexture().dispose();
        }

        if(returnScreenButton!=null)
            returnScreenButton.dispose();

        if(signOutButton!=null)
            signOutButton.dispose();

    }

}
