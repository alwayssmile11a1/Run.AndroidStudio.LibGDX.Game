package noshanabi.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import noshanabi.game.GameManager;

/**
 * Created by 2SMILE2 on 25/09/2017.
 */

public class MenuScreen implements Screen{

    //GameManager
    GameManager gameManager;

    //-----------------VIEW RELATED VARIABLES-----------------//
    //how well we want to see our map
    private Viewport menuViewPort;

    //stage manage UI on it
    private Stage stage;

    //----------------TEXTURE RELATED VARIABLES------------//
    Image singlePlayerButton;
    Image multiPlayerButton;
    Texture singlePlayerTexture;
    Texture multiPlayerTexture;


    public MenuScreen(GameManager _gameManager)
    {
        //set up constructor variables
        this.gameManager = _gameManager;

        //-----------------VIEW RELATED VARIABLES-----------------//
        menuViewPort = new StretchViewport(GameManager.WORLDWIDTH, GameManager.WORLDHEIGHT);
        stage = new Stage(menuViewPort,gameManager.batch);


        //Table help us to easily arrange UI, such as labels, texts, etc.
        Table table = new Table();
        table.center();
        table.setFillParent(true);



        //singleplayer Button
        singlePlayerTexture = new Texture("images/playbutton.png");
        singlePlayerButton = new Image(singlePlayerTexture);
        singlePlayerButton.setBounds(0,0,singlePlayerTexture.getWidth(),singlePlayerButton.getHeight());
        singlePlayerButton.setTouchable(Touchable.enabled);
        singlePlayerButton.addListener(new InputListener()
        {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.input.setInputProcessor(gameManager.getMapSelectionScreen().getStage());
                gameManager.setScreen(gameManager.getMapSelectionScreen());
                return true;
            }

        });

        //multiplayer Button
        multiPlayerTexture = new Texture("images/multiplaybutton.png");
        multiPlayerButton = new Image(multiPlayerTexture);
        multiPlayerButton.setBounds(0,0,multiPlayerTexture.getWidth(),multiPlayerTexture.getHeight());
        multiPlayerButton.setTouchable(Touchable.enabled);
        multiPlayerButton.addListener(new InputListener()
        {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                if(gameManager.getPlayerServices() == null) //desktop test
                {
                    Gdx.input.setInputProcessor(gameManager.getLoginScreen().getStage());
                    gameManager.setScreen(gameManager.getLoginScreen());
                    return true;
                }

                if(gameManager.getPlayerServices().isSignedIn()) {
                    gameManager.connectToServer();
                    Gdx.input.setInputProcessor(gameManager.getModeSelectionScreen().getStage());
                    gameManager.setScreen(gameManager.getModeSelectionScreen());
                }
                else
                {
                    Gdx.input.setInputProcessor(gameManager.getLoginScreen().getStage());
                    gameManager.setScreen(gameManager.getLoginScreen());
                }

                return true;
            }

        });

        //add to table
        table.add(singlePlayerButton).expandX();
        table.row();
        table.add(multiPlayerButton).expandX().padTop(10f);

        //add to stage
        stage.addActor(table);

    }


    @Override
    public void render(float delta) {
        //color to clear this screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        menuViewPort.update(width,height);
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

        if (singlePlayerTexture != null)
            singlePlayerTexture.dispose();

        if (multiPlayerTexture != null)
            multiPlayerTexture.dispose();
    }


}
