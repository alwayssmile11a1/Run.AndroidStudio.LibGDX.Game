package noshanabi.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Stage;
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
    //world width and height
    private int worldWidth;
    private int worldHeight;
    //a camera to view our world
    private OrthographicCamera mainCamera;
    //stage manage UI on it
    private Stage stage;

    //----------------TEXTURE RELATED VARIABLES------------//
    //the background image
    Sprite backgroundSprite;


    public MenuScreen(GameManager gameManager)
    {
        //set up constructor variables
        this.gameManager = gameManager;

        //-----------------VIEW RELATED VARIABLES-----------------//
        menuViewPort = new StretchViewport(GameManager.WORLDWIDTH, GameManager.WORLDHEIGHT, new OrthographicCamera());
        stage = new Stage(menuViewPort,gameManager.batch);

        Gdx.gl.glClearColor(0,0,0,1);
    }

    //handle the UI that is interacted
    public void handleInput(float delta)
    {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
        handleInput(delta);
    }

    @Override
    public void resize(int width, int height) {
        menuViewPort.update(width,height);
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
        if(gameManager!=null)
        {
            gameManager.dispose();
        }
        if(backgroundSprite.getTexture()!=null)
        {
            backgroundSprite.getTexture().dispose();
        }
    }
}
