package noshanabi.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;

import org.json.JSONException;
import org.json.JSONObject;

import noshanabi.game.ButtonPrefabs.ReturnScreenButton;
import noshanabi.game.ButtonPrefabs.SignOutButton;
import noshanabi.game.GameManager;
import noshanabi.game.Server.ServerListener;

/**
 * Created by 2SMILE2 on 13/11/2017.
 */

public class CreateRoomScreen implements Screen, ServerListener{

    //viewport
    private Viewport viewport;

    //gameStage manage UI on it
    private Stage stage;

    //game manager
    private GameManager gameManager;


    private ReturnScreenButton returnScreenButton;

    private SignOutButton signOutButton;

    private boolean isRoomExisted;

    Sprite backGround;

    //Room name variables
    Table roomNameTable;
    VisTextField roomNameTextArea;
    VisTextButton acceptButton;
    VisLabel errorLabel;

    public CreateRoomScreen(GameManager _gameManager) {
        //set up constructor variables
        this.gameManager = _gameManager;


        backGround = new Sprite(new Texture("images/BlueBackground.png"));
        backGround.setSize(GameManager.WORLDWIDTH, GameManager.WORLDHEIGHT);

        isRoomExisted = true;

        //-----------------VIEW RELATED VARIABLES-----------------//
        viewport = new StretchViewport(GameManager.WORLDWIDTH, GameManager.WORLDHEIGHT);
        stage = new Stage(viewport, gameManager.batch);


        //----------------ROOM NAME ---------------------------
        roomNameTable = new Table();
        roomNameTable.setFillParent(true);
        roomNameTable.center();

        //Label
        VisLabel roomNameLabel = new VisLabel("Enter your room name ..");
        roomNameLabel.setFontScale(0.7f,0.6f);
        roomNameTable.add(roomNameLabel).left();
        roomNameTable.row();

        //text field
        roomNameTextArea = new VisTextField();
        roomNameTable.add(roomNameTextArea).size(300, roomNameTextArea.getHeight());
        roomNameTable.padBottom(50);
        roomNameTable.row();

        //accept button
        acceptButton = new VisTextButton("CREATE");
        acceptButton.getLabel().setFontScale(0.5f);
        acceptButton.addListener(new InputListener()
        {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                if(!gameManager.getServer().getSocket().connected())
                {
                    errorLabel.setText("Can not connect to server, try again");
                    gameManager.connectToServer();
                    return true;
                }

                if(!roomNameTextArea.getText().isEmpty()) {
                    JSONObject data = new JSONObject();
                    try {
                        data.put("roomName", roomNameTextArea.getText());
                        gameManager.getServer().getSocket().emit("createRoom", data);
                    } catch (JSONException e) {
                        Gdx.app.log("SOCKET.IO", "Error sending room name data");
                    }

                }
                else
                {
                    errorLabel.setText("Your room is INVALID, Enter another name");
                }

                return true;
            }
        });
        roomNameTable.add(acceptButton).padTop(10).padBottom(10).size(80,30);
        roomNameTable.row();

        //Error label
        errorLabel = new VisLabel();
        errorLabel.setFontScale(0.5f);
        roomNameTable.add(errorLabel);
        errorLabel.setColor(Color.RED);


        //add to gameStage
        stage.addActor(roomNameTable);

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

        //------------------SIGN OUT BUTTON ------------------------
        signOutButton = new SignOutButton();
        signOutButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                if (gameManager.getPlayerServices() != null) {
                    gameManager.getPlayerServices().signOut();
                }
                gameManager.getServer().getSocket().disconnect();
                Gdx.input.setInputProcessor(gameManager.getLoginScreen().getStage());
                gameManager.setScreen(gameManager.getLoginScreen());
                return true;
            }

        });

        group.addActor(signOutButton);

        //------------------USER INFORMATION ----------------------
        VisLabel userNameLabel = new VisLabel("USER NAME");
        userNameLabel.setFontScale(0.5f);
        if (gameManager.getPlayerServices() != null) {
            userNameLabel.setText(gameManager.getPlayerServices().getUserName());
        }
        userNameLabel.setPosition(gameManager.WORLDWIDTH - userNameLabel.getWidth(), returnScreenButton.getY() + 15);

        group.addActor(userNameLabel);


        //add to gameStage
        stage.addActor(group);

    }

    @Override
    public void OnSocketRoomCreated(Object... args) {
        isRoomExisted = false;
    }

    @Override
    public void OnRoomExisted(Object... args) {
        isRoomExisted = true;
        errorLabel.setText("Your room is EXISTED, Enter another name");
    }

    @Override
    public void OnRoomJoined(Object... args) {

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
    public void OnGetOtherPlayers(Object... args) {

    }

    @Override
    public void OnSocketRoomJoined(Object... args) {

    }

    @Override
    public void OnSocketRoomLeaved(Object... args) {

    }

    @Override
    public void OnRoomLeaved(Object... args) {

    }

    @Override
    public void OnUnableToJoinRoom(Object... args) {

    }

    @Override
    public void OnPlayersCountChanged(Object ...args)
    {

    }

    @Override
    public void OnRoomStateChanged(Object... args) {

    }

    @Override
    public void OnGameJoined(Object... args) {

    }

    @Override
    public void OnMapTransitioned(Object... args)
    {

    }

    @Override
    public void OnGetMaxPlayersInRoom(Object ...args)
    {

    }

    @Override
    public void render(float delta) {
        //color to clear this screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        if (!isRoomExisted)
        {
            errorLabel.setText("");
            isRoomExisted = true;

            RoomJoinedScreen roomJoinedScreen = gameManager.getRoomJoinedScreen();
            Gdx.input.setInputProcessor(roomJoinedScreen.getStage());
            roomJoinedScreen.ownRoomMode(true);
            gameManager.setScreen(roomJoinedScreen);
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
