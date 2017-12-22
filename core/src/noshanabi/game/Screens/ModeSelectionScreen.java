package noshanabi.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.widget.VisLabel;

import noshanabi.game.ButtonPrefabs.CommonButton;
import noshanabi.game.ButtonPrefabs.CommonButtonListener;
import noshanabi.game.ButtonPrefabs.ReturnScreenButton;
import noshanabi.game.ButtonPrefabs.SignOutButton;
import noshanabi.game.GameManager;
import noshanabi.game.Resourses;

/**
 * Created by 2SMILE2 on 12/11/2017.
 */

public class ModeSelectionScreen implements Screen {

    //GameManager
    GameManager gameManager;

    //-----------------VIEW RELATED VARIABLES-----------------//
    //how well we want to see our map
    private Viewport viewport;

    //gameStage manage UI on it
    private Stage stage;

    //----------------TEXTURE RELATED VARIABLES------------//

    private ReturnScreenButton returnScreenButton;

    private SignOutButton signOutButton;

    private CommonButton findRoomButton;
    private CommonButton createRoomButton;


    public ModeSelectionScreen(GameManager _gameManager) {
        //set up constructor variables
        this.gameManager = _gameManager;

        //-----------------VIEW RELATED VARIABLES-----------------//
        viewport = new StretchViewport(Resourses.WORLDWIDTH, Resourses.WORLDHEIGHT);
        stage = new Stage(viewport, gameManager.batch);
        Gdx.input.setInputProcessor(stage);

        //Table help us to easily arrange UI, such as labels, texts, etc.
        Table table = new Table();
        table.center();
        table.setFillParent(true);


        //---------------FIND ROOM AND CREATE ROOM LABEL --------------
        findRoomButton = new CommonButton(gameManager);
        findRoomButton.setText("FIND ROOM");
        createRoomButton = new CommonButton(gameManager);
        createRoomButton.setText("CREATE ROOM");

        //add listener
        findRoomButton.setListener(new CommonButtonListener() {
            @Override
            public void OnClick() {
                Gdx.input.setInputProcessor(gameManager.getFindRoomScreen().getStage());
                gameManager.setScreen(gameManager.getFindRoomScreen());
            }

        });

        createRoomButton.setListener(new CommonButtonListener() {
            @Override
            public void OnClick() {
                Gdx.input.setInputProcessor(gameManager.getCreateRoomScreen().getStage());
                gameManager.setScreen(gameManager.getCreateRoomScreen());
            }
        });

        table.add(findRoomButton).expandX();
        table.row();
        table.add(createRoomButton).expandX().padTop(20);

        //add to gameStage
        stage.addActor(table);


        //---------------------RETURN BUTTON -----------------
        //Group allow to place an actor wherever we want
        Group group = new Group();

        returnScreenButton = new ReturnScreenButton();
        returnScreenButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.input.setInputProcessor(gameManager.getMenuScreen().getStage());
                gameManager.setScreen(gameManager.getMenuScreen());
                return true;
            }

        });

        group.addActor(returnScreenButton);

        //------------------USER INFORMATION ----------------------
        VisLabel userNameLabel = new VisLabel("USER NAME");
        userNameLabel.setFontScale(0.5f);
        if (gameManager.getPlayerServices() != null) {
            userNameLabel.setText(gameManager.getPlayerServices().getUserName());
        }
        userNameLabel.setPosition(Resourses.WORLDWIDTH - userNameLabel.getWidth(), returnScreenButton.getY() + 15);

        group.addActor(userNameLabel);

        //------------------SIGN OUT BUTTON ------------------------

        signOutButton = new SignOutButton(gameManager);

        //add to group
        group.addActor(signOutButton);


        //add to gameStage
        stage.addActor(group);
    }


    @Override
    public void render(float delta) {
        //color to clear this screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.draw();
        stage.act();

        signOutButton.update(delta);
        findRoomButton.update(delta);
        createRoomButton.update(delta);

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width,height);
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
        if (stage != null) {
            stage.dispose();
        }

        if (returnScreenButton != null)
            returnScreenButton.dispose();

        if (signOutButton != null)
            signOutButton.dispose();

        if (findRoomButton != null)
            findRoomButton.dispose();

        if (createRoomButton != null)
            createRoomButton.dispose();
    }

}
