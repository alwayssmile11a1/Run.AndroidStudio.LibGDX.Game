package noshanabi.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import noshanabi.game.GameManager;

/**
 * Created by 2SMILE2 on 12/11/2017.
 */

public class ModeSelectionScreen implements Screen {

    //GameManager
    GameManager gameManager;

    //-----------------VIEW RELATED VARIABLES-----------------//
    //how well we want to see our map
    private Viewport viewport;

    //stage manage UI on it
    private Stage stage;

    //----------------TEXTURE RELATED VARIABLES------------//

    private Image returnImage;
    private Texture returnTexture;

    private Image signOutImage;
    private Texture signOutTexture;

    public ModeSelectionScreen(GameManager _gameManager) {
        //set up constructor variables
        this.gameManager = _gameManager;

        //color to clear this screen
        Gdx.gl.glClearColor(0, 0, 0, 1);

        //-----------------VIEW RELATED VARIABLES-----------------//
        viewport = new StretchViewport(GameManager.WORLDWIDTH, GameManager.WORLDHEIGHT);
        stage = new Stage(viewport, gameManager.batch);
        Gdx.input.setInputProcessor(stage);

        //Table help us to easily arrange UI, such as labels, texts, etc.
        Table table = new Table();
        table.center();
        table.setFillParent(true);


        //---------------FIND ROOM AND CREATE ROOM LABEL --------------
        Label.LabelStyle labelStyle = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
        Label findRoomLabel = new Label("FIND ROOM", labelStyle);
        Label createRoomLabel = new Label("CREATE ROOM", labelStyle);

        findRoomLabel.setFontScale(2);
        createRoomLabel.setFontScale(2);

        //add listener
        findRoomLabel.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.input.setInputProcessor(gameManager.getCreateRoomScreen().getStage());
                gameManager.setScreen(gameManager.getCreateRoomScreen());
                return true;
            }

        });

        createRoomLabel.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                Gdx.input.getTextInput(gameManager.getCreateRoomScreen(), "Enter your room name:", "", "Room name ...");
                Gdx.input.setInputProcessor(gameManager.getCreateRoomScreen().getStage());
                gameManager.setScreen(gameManager.getCreateRoomScreen());

                return true;
            }

        });

        table.add(findRoomLabel).expandX();
        table.row();
        table.add(createRoomLabel).expandX().padTop(20);

        //add to stage
        stage.addActor(table);


        //---------------------RETURN BUTTON -----------------
        //Group allow to place an actor wherever we want
        Group group = new Group();

        returnTexture = new Texture("images/rightarrow.png");
        returnImage = new Image(returnTexture);
        returnImage.setBounds(0, 0, returnTexture.getWidth(), returnTexture.getHeight());
        returnImage.setTouchable(Touchable.enabled);
        returnImage.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.input.setInputProcessor(gameManager.getMenuScreen().getStage());
                gameManager.setScreen(gameManager.getMenuScreen());
                return true;
            }

        });

        returnImage.setSize(50, 50);
        returnImage.setOrigin(returnImage.getWidth() / 2, returnImage.getHeight() / 2);
        returnImage.setScaleX(-1);
        returnImage.setPosition(10, gameManager.WORLDHEIGHT - 60);

        group.addActor(returnImage);

        //------------------SIGN OUT BUTTON ------------------------
        signOutTexture = new Texture("images/signout.png");
        signOutImage = new Image(signOutTexture);
        signOutImage.setBounds(0, 0, signOutTexture.getWidth(), signOutTexture.getHeight());
        signOutImage.setTouchable(Touchable.enabled);
        signOutImage.addListener(new InputListener() {
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
        //set position and size
        signOutImage.setPosition(gameManager.WORLDWIDTH - 60, gameManager.WORLDHEIGHT - 60);
        signOutImage.setSize(50, 50);


        //add to group
        group.addActor(signOutImage);


        //------------------USER INFORMATION ----------------------
        Label userNameLabel = new Label("USER NAME", labelStyle);
        if (gameManager.getPlayerServices() != null && gameManager.getPlayerServices().isSignedIn()) {
            userNameLabel.setText(gameManager.getPlayerServices().getUserName());
        }
        userNameLabel.setPosition(gameManager.WORLDWIDTH - userNameLabel.getWidth() - 100, returnImage.getY()+15);

        group.addActor(userNameLabel);


        //add to actor
        stage.addActor(group);
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
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

        if(returnTexture!=null)
            returnTexture.dispose();

        if(signOutTexture!=null)
            signOutTexture.dispose();

    }

}
