package noshanabi.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import noshanabi.game.GameManager;

/**
 * Created by 2SMILE2 on 25/09/2017.
 */

public class GameOverScreen implements Screen{

    //viewport
    private Viewport gameOverViewPort;

    //stage manage UI on it
    private Stage stage;

    //game manager
    private GameManager gameManager;


    public GameOverScreen(GameManager _gameManager)
    {
        //set up constructor variables
        this.gameManager = _gameManager;

        //-----------------VIEW RELATED VARIABLES-----------------//
        gameOverViewPort = new StretchViewport(GameManager.WORLDWIDTH, GameManager.WORLDHEIGHT, new OrthographicCamera());
        stage = new Stage(gameOverViewPort,gameManager.batch);

        //Table help us to easily arrange UI, such as labels, texts, etc.
        Table table = new Table();
        table.center();
        table.setFillParent(true);

        //example game over labels
        Label.LabelStyle labelStyle = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
        Label gameOverLabel = new Label("GAME OVER",labelStyle);
        Label playAgainLabel = new Label("Click to play again",labelStyle);

        //add to table
        table.add(gameOverLabel).expandX();
        table.row();
        table.add(playAgainLabel).expandX().padTop(10f);

        //add to stage
        stage.addActor(table);

    }

    //handle the UI that is interacted
    public void handleInput(float delta)
    {

    }

    @Override
    public void render(float delta) {
        //color to clear this screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
        handleInput(delta);
    }

    @Override
    public void resize(int width, int height) {
        gameOverViewPort.update(width,height);
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
    }
}
