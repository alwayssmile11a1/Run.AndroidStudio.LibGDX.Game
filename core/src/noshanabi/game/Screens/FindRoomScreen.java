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
import com.badlogic.gdx.utils.Align;
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
import noshanabi.game.Resourses;
import noshanabi.game.Server.ServerListener;

/**
 * Created by 2SMILE2 on 13/11/2017.
 */

public class FindRoomScreen implements Screen, ServerListener{

    //viewport
    private Viewport viewport;

    //gameStage manage UI on it
    private Stage stage;

    //game manager
    private GameManager gameManager;

    private Sprite backGround;

    private ReturnScreenButton returnScreenButton;

    private SignOutButton signOutButton;

    private Table roomTable;

    private HashMap<String, Array<Label>> roomList;

    private Array<Array<String>> roomsToAdd;

    private Array<String> roomsToRemove;

    private boolean unableToJoinRoom;

    private boolean needSwitchScreen;

    private int maxPlayersInRoom;

    public FindRoomScreen(GameManager _gameManager) {
        //set up constructor variables
        this.gameManager = _gameManager;

        backGround = new Sprite(new Texture(Resourses.FindRoomBackground));
        backGround.setSize(Resourses.WORLDWIDTH, Resourses.WORLDHEIGHT);

        roomsToAdd = new Array<Array<String>>();
        roomsToRemove = new Array<String>();

        unableToJoinRoom = true;
        needSwitchScreen = false;

        //-----------------VIEW RELATED VARIABLES-----------------//
        viewport = new StretchViewport(Resourses.WORLDWIDTH, Resourses.WORLDHEIGHT);
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
        userNameLabel.setPosition(Resourses.WORLDWIDTH - userNameLabel.getWidth(), returnScreenButton.getY() + 15);

        group.addActor(userNameLabel);


        //add to gameStage
        stage.addActor(group);


        //---------------ROOM LIST----------------------------------
        roomList = new HashMap<String, Array<Label>>();
        roomTable = new Table();
        roomTable.align(Align.top);
        roomTable.add(new VisLabel("Room name")).expandX();
        roomTable.add(new VisLabel("State")).expandX();
        roomTable.add(new VisLabel("Players")).expandX();
        roomTable.row();

        VisScrollPane scrollPane = new VisScrollPane(roomTable);
        scrollPane.setSize(Resourses.WORLDWIDTH/1.5f,Resourses.WORLDHEIGHT-100);
        scrollPane.setPosition(125,50);

        stage.addActor(scrollPane);


    }

    @Override
    public void OnSocketRoomCreated(Object... args) {
        try {

            JSONObject data = (JSONObject) args[0];
            String roomName = data.getString("roomName");
            addRoom(roomName,"Waiting", "1");

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
    public void OnGetMaxPlayersInRoom(Object ...args)
    {
        try {

            JSONObject data = (JSONObject) args[0];
            maxPlayersInRoom = data.getInt("max");

        } catch (JSONException e) {
            Gdx.app.log("SocketIO", "Error getting max players in a room");
            e.printStackTrace();
        }

    }

    @Override
    public void OnSocketRoomJoined(Object... args) {
        unableToJoinRoom = false;
    }

    @Override
    public void OnGetRooms(Object... args) {
        try {
            JSONObject objects = (JSONObject) args[0];

            //get all the keys
            Iterator<String> iter = objects.keys();

            //loop through all to get all room names
            while (iter.hasNext()) {
                String roomName = iter.next();
                //get room
                JSONObject room = new JSONObject(objects.get(roomName).toString());

                //get the number of players in this room
                int playersCount = ((JSONObject)room.getJSONObject("players")).length();
                String roomState = room.getString("state");

                //room information
                Array<String> roomInfo = new Array<String>();
                roomInfo.add(roomName);
                roomInfo.add(roomState);
                roomInfo.add(""+playersCount);

                roomsToAdd.add(roomInfo);
            }

        } catch (JSONException e) {
            Gdx.app.log("SocketIO", "Error handling get rooms event");
        }
    }

    @Override
    public void OnRoomCreated(Object... args) {
        try {

            JSONObject data = (JSONObject) args[0];
            String roomName = data.getString("roomName");

            //room information
            Array<String> roomInfo = new Array<String>();
            roomInfo.add(roomName);
            roomInfo.add("Waiting");
            roomInfo.add("1");

            roomsToAdd.add(roomInfo);

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
    public void OnUnableToJoinRoom(Object... args) {
        unableToJoinRoom = true;
    }

    @Override
    public void OnGameJoined(Object... args) {

    }

    @Override
    public void OnMapTransitioned(Object... args)
    {

    }

    @Override
    public void OnRoomStateChanged(Object... args) {
        try {
            JSONObject data = (JSONObject) args[0];
            String roomName = data.getString("roomName");
            String roomState = data.getString("state");
            roomList.get(roomName).get(1).setText(roomState); //get(1): room state

        } catch (JSONException e) {
            Gdx.app.log("SocketIO", "Error getting room state");
            e.printStackTrace();
        }
    }


    @Override
    public void OnPlayersCountChanged(Object ...args)
    {
        try {
            JSONObject data = (JSONObject) args[0];
            String roomName = data.getString("roomName");
            int playersCount = data.getInt("playersCount");
            roomList.get(roomName).get(2).setText(playersCount+"/"+maxPlayersInRoom); //get(2) : player count

        } catch (JSONException e) {
            Gdx.app.log("SocketIO", "Error getting players count");
            e.printStackTrace();
        }
    }

    private void addRoom(final String roomName, String roomState, String playersCount)
    {
        VisLabel roomNameLabel = new VisLabel(roomName);
        VisLabel roomStateLabel = new VisLabel(roomState);
        VisLabel playersCountLabel = new VisLabel(playersCount + "/" + maxPlayersInRoom);

        roomNameLabel.addListener(new InputListener() {
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

        roomTable.add(roomNameLabel).expandX();
        roomTable.add(roomStateLabel).expandX();
        roomTable.add(playersCountLabel).expandX();
        roomTable.row();

        Array<Label> roomInfoLabels = new Array<Label>();
        roomInfoLabels.add(roomNameLabel);
        roomInfoLabels.add(roomStateLabel);
        roomInfoLabels.add(playersCountLabel);

        //add ro list
        roomList.put(roomName,roomInfoLabels);
    }

    private void removeRoom(String roomName)
    {
        Array<Label> roomLabel = roomList.get(roomName);
        if(roomLabel!=null) {
            for (Label label : roomLabel) {
                roomTable.removeActor(label);
            }
            roomList.remove(roomName);
        }
    }

    @Override
    public void render(float delta) {
        //color to clear this screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if(roomsToAdd.size > 0)
        {
            for(Array<String> room: roomsToAdd) {
                addRoom(room.get(0),room.get(1),room.get(2));
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

        if(!unableToJoinRoom && needSwitchScreen)
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
