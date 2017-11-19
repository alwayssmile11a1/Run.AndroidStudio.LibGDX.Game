package noshanabi.game.PlayScreenUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.widget.VisImage;

import noshanabi.game.GameManager;

/**
 * Created by 2SMILE2 on 02/10/2017.
 */

public class PlayScreenUI {

    private GameManager gameManager;

    //manage on-screen buttons
    Stage inGameStage;
    //viewport
    Viewport viewPort;

    //Pause Button
    boolean pauseButtonPressed = false;
    Texture pauseButtonTexture;
    VisImage pauseButton;

    //Pause game stage
    Stage pauseGameStage;
    VisImage continueButton;
    Texture continueButtonTexture;
    boolean continueButtonPressed = false;
    private boolean drawPauseGameStage = false;
    VisImage pauseGameBackground;
    Texture pauseGameTexture;

    Texture menuButtonTexture;
    VisImage menuButton;

    private boolean switchToMenuScreen=false;


    public PlayScreenUI(GameManager gameManager) {
        //set up
        viewPort = new StretchViewport(GameManager.WORLDWIDTH, GameManager.WORLDHEIGHT);
        inGameStage = new Stage(viewPort, gameManager.batch);

        this.gameManager = gameManager;

        //--------------PAUSE BUTTON ------------------------
        pauseButtonTexture = new Texture("images/pausebutton.png");
        pauseButton = new VisImage(pauseButtonTexture);
        pauseButton.setSize(32, 32);
        pauseButton.setPosition(gameManager.WORLDWIDTH - pauseButton.getWidth() - 10, gameManager.WORLDHEIGHT - pauseButton.getHeight() - 10);
        pauseButton.setTouchable(Touchable.enabled);
        pauseButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                pauseButtonPressed = true;
                drawPauseGameStage = true;
                Gdx.input.setInputProcessor(pauseGameStage);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                pauseButtonPressed = false;
            }
        });
        inGameStage.addActor(pauseButton);


        //-------------PAUSE GAME STAGE ------------------
        pauseGameStage = new Stage(viewPort, gameManager.batch);
        Group pauseGameGroup = new Group();
//        pauseGameGroup.setFillParent(true);
//        pauseGameGroup.center();

        //pause game background
        pauseGameTexture = new Texture("images/WhiteRectangle.png");
        pauseGameBackground = new VisImage(pauseGameTexture);
        pauseGameBackground.setSize(gameManager.WORLDWIDTH-25,gameManager.WORLDHEIGHT-25);
        pauseGameBackground.setPosition(gameManager.WORLDWIDTH/2-pauseGameBackground.getWidth()/2,gameManager.WORLDHEIGHT/2-pauseGameBackground.getHeight()/2);
        pauseGameBackground.setColor(0, 0, 0, 0.5f);
        pauseGameStage.addActor(pauseGameBackground);

        //continue button
        continueButtonTexture = new Texture("images/continuebutton.png");
        continueButton = new VisImage(continueButtonTexture);
        continueButton.setTouchable(Touchable.enabled);
        continueButton.setPosition(gameManager.WORLDWIDTH/2-continueButton.getWidth()/2,gameManager.WORLDHEIGHT/2-continueButton.getHeight()/2);
        continueButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                drawPauseGameStage = false;
                continueButtonPressed = true;
                Gdx.input.setInputProcessor(inGameStage);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                continueButtonPressed = false;
            }
        });
        //add to table
        pauseGameGroup.addActor(continueButton);


        //menu button
        menuButtonTexture = new Texture("images/menubutton.png");
        menuButton = new VisImage(menuButtonTexture);
        menuButton.setTouchable(Touchable.enabled);
        menuButton.setSize(52,52);
        menuButton.setPosition(continueButton.getX()+80,gameManager.WORLDHEIGHT/2-menuButton.getHeight()/2);
        menuButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                switchToMenuScreen = true;
                return true;
            }
        });
        //add to table
        pauseGameGroup.addActor(menuButton);
        pauseGameStage.addActor(pauseGameGroup);
    }


    public void draw()
    {

        inGameStage.draw();
        inGameStage.act();

        if(drawPauseGameStage)
        {
            pauseGameStage.draw();
            pauseGameStage.act();
        }

        //since the frame is so fast that the continueButtonPressed and pauseButtonPressed is not returned to false in 2-3 frames
        //and furthermore, we want the result to be justTouched-like event, setting these variable to false after one frame is necessary
        continueButtonPressed = false;
        pauseButtonPressed = false;

        if(switchToMenuScreen)
        {
            gameManager.getScreen().dispose();
            Gdx.input.setInputProcessor(gameManager.getMenuScreen().getStage());
            gameManager.setScreen(gameManager.getMenuScreen());
            switchToMenuScreen=false;
        }

    }

    public void resize(int width, int height)
    {
        viewPort.update(width,height);
    }

    public boolean isPauseButtonPressed()
    {
        return pauseButtonPressed;
    }

    public Stage getInGameStage() {
        return inGameStage;
    }

    public boolean isContinueButtonPressed() {
        return continueButtonPressed;
    }

    public Stage getPauseGameStage() {
        return pauseGameStage;
    }

    public void dispose() {
        if (inGameStage != null)
            inGameStage.dispose();

        if(pauseButtonTexture!=null)
            pauseButtonTexture.dispose();

        if(continueButtonTexture!=null)
            continueButtonTexture.dispose();

        if(pauseGameStage!=null)
            pauseGameStage.dispose();

        if(pauseGameTexture!=null)
            pauseGameTexture.dispose();

        if(menuButtonTexture!=null)
            menuButtonTexture.dispose();

    }


}
