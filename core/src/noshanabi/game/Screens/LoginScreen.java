package noshanabi.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
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
    Image facebookLoginButton;
    Texture facebookLoginTexture;

    //the background image
    Image googleLoginButton;
    Texture googleLoginTexture;

    private Image returnImage;
    private Texture returnTexture;

    private boolean isNeedSwitchScreen;



    public LoginScreen(GameManager _gameManager)
    {
        //set up constructor variables
        this.gameManager = _gameManager;
        //color to clear this screen
        Gdx.gl.glClearColor(0,0,0,1);

        isNeedSwitchScreen = false;

        //-----------------VIEW RELATED VARIABLES-----------------//
        viewPort = new StretchViewport(GameManager.WORLDWIDTH, GameManager.WORLDHEIGHT);
        stage = new Stage(viewPort,gameManager.batch);

        //Table help us to easily arrange UI, such as labels, texts, etc.
        Table table = new Table();
        table.center();
        table.setFillParent(true);

        //---------------- FACEBOOK LOGIN BUTTON ----------------
        facebookLoginTexture = new Texture("images/facebookloginbutton.png");
        facebookLoginButton = new Image(facebookLoginTexture);
        facebookLoginButton.setBounds(0,0, facebookLoginTexture.getWidth(), facebookLoginButton.getHeight());
        facebookLoginButton.setTouchable(Touchable.enabled);
        facebookLoginButton.addListener(new InputListener()
        {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                if(gameManager.playerServices!=null) {

                    gameManager.playerServices.signInToFacebook();
                    isNeedSwitchScreen = true;
                }

                return true;
            }

        });

        //------------------------- GOOGLE LOGIN BUTTON ------------------
        googleLoginTexture = new Texture("images/googleloginbutton.png");
        googleLoginButton = new Image(googleLoginTexture);
        googleLoginButton.setBounds(0,0, googleLoginTexture.getWidth(), googleLoginTexture.getHeight());
        googleLoginButton.setTouchable(Touchable.enabled);
        googleLoginButton.addListener(new InputListener()
        {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                if(gameManager.playerServices!=null) {

                    gameManager.playerServices.signInToGoogle();
                    isNeedSwitchScreen = true;
                }

                return true;
            }

        });



        //add to table
        table.add(facebookLoginButton).size(300,100);
        table.row();
        table.add(googleLoginButton).size(300,100);

        //add to stage
        stage.addActor(table);



        //Group allow to place an actor wherever we want
        Group group = new Group();

        //--------------------RETURN BUTTON--------------------
        returnTexture = new Texture("images/rightarrow.png");
        returnImage = new Image(returnTexture);
        returnImage.setBounds(0,0,returnTexture.getWidth(),returnTexture.getHeight());
        returnImage.setTouchable(Touchable.enabled);
        returnImage.addListener(new InputListener()
        {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.input.setInputProcessor(gameManager.getMenuScreen().getStage());
                gameManager.setScreen(gameManager.getMenuScreen());
                return true;
            }

        });

        returnImage.setSize(50,50);
        returnImage.setOrigin(returnImage.getWidth()/2, returnImage.getHeight()/2);
        returnImage.setScaleX(-1);
        returnImage.setPosition(10,gameManager.WORLDHEIGHT-60);
        //add to group
        group.addActor(returnImage);


        //add to actor
        stage.addActor(group);

    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();

        if(gameManager.playerServices!=null && gameManager.playerServices.isSignedIn() && isNeedSwitchScreen)
        {
            Gdx.input.setInputProcessor(gameManager.getModeSelectionScreen().getStage());
            gameManager.setScreen(gameManager.getModeSelectionScreen());
            isNeedSwitchScreen = false;
        }

    }

    @Override
    public void resize(int width, int height) {
        viewPort.update(width,height);
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

        if (facebookLoginTexture != null)
            facebookLoginTexture.dispose();

        if (googleLoginTexture != null)
            googleLoginTexture.dispose();

        if(returnTexture!=null)
            returnTexture.dispose();

    }

}
