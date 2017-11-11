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
 * Created by 2SMILE2 on 11/11/2017.
 */

public class LoginScreen implements Screen {

    //GameManager
    GameManager gameManager;

    //-----------------VIEW RELATED VARIABLES-----------------//
    //how well we want to see our map
    private Viewport viewPort;

    //stage manage UI on it
    private Stage stage;

    //----------------TEXTURE RELATED VARIABLES------------//
    //the background image
    Image facebookLoginButton;
    Texture facebookLoginTexture;



    public LoginScreen(GameManager _gameManager)
    {
        //set up constructor variables
        this.gameManager = _gameManager;

        //-----------------VIEW RELATED VARIABLES-----------------//
        viewPort = new StretchViewport(GameManager.WORLDWIDTH, GameManager.WORLDHEIGHT);
        stage = new Stage(viewPort,gameManager.batch);
        Gdx.input.setInputProcessor(stage);

        //Table help us to easily arrange UI, such as labels, texts, etc.
        Table table = new Table();
        table.center();
        table.setFillParent(true);

        //singleplayer Button
        facebookLoginTexture = new Texture("images/facebookloginbutton.png");
        facebookLoginButton = new Image(facebookLoginTexture);
        facebookLoginButton.setBounds(0,0, facebookLoginTexture.getWidth(), facebookLoginButton.getHeight());
        facebookLoginButton.setTouchable(Touchable.enabled);
        facebookLoginButton.addListener(new InputListener()
        {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                gameManager.facebookPlayerServices.signIn();
                //gameManager.setScreen(new MapSelectionScreen(gameManager));
                dispose();
                return true;
            }

        });



        //add to table
        table.add(facebookLoginButton).size(300,100);

        //add to stage
        stage.addActor(table);

        //color to clear this screen
        Gdx.gl.glClearColor(0,0,0,1);
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewPort.update(width,height);
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

        if (facebookLoginTexture != null)
            facebookLoginTexture.dispose();


    }

}
