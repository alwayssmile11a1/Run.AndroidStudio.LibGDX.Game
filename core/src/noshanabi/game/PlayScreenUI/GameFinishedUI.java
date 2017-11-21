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
 * Created by 2SMILE2 on 21/11/2017.
 */

public class GameFinishedUI {

    //viewport
    Viewport viewPort;


    //finish game stage
    Stage stage;


    //Pause Button
    boolean pauseButtonPressed = false;
    Texture pauseButtonTexture;
    VisImage pauseButton;

    //group
    Group gameFinishedGroup;
    //replay button
    VisImage replayButton;
    Texture replayButtonTexture;
    boolean replayButtonPressed = false;

    //background
    VisImage background;
    Texture backgroundTexture;


    //menu button
    Texture menuButtonTexture;
    VisImage menuButton;
    private boolean menuButtonPressed =false;


    private boolean needSetInputProcessor = true;
//    private boolean drawGameFinishedStageGroup = false;

    public GameFinishedUI(GameManager gameManager) {
        needSetInputProcessor = true;
        //set up
        viewPort = new StretchViewport(GameManager.WORLDWIDTH, GameManager.WORLDHEIGHT);
        //inGameStage = new Stage(viewPort, gameManager.batch);
        stage = new Stage(viewPort, gameManager.batch);

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
                gameFinishedGroup.setVisible(true);
                //drawGameFinishedStageGroup = true;
                //Gdx.input.setInputProcessor(stage);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                pauseButtonPressed = false;
            }
        });
        stage.addActor(pauseButton);


        //-------------GAME FINISHED STAGE ------------------
        gameFinishedGroup = new Group();

        //background
        backgroundTexture = new Texture("images/WhiteRectangle.png");
        background = new VisImage(backgroundTexture);
        background.setSize(gameManager.WORLDWIDTH-25,gameManager.WORLDHEIGHT-25);
        background.setPosition(gameManager.WORLDWIDTH/2- background.getWidth()/2,gameManager.WORLDHEIGHT/2- background.getHeight()/2);
        background.setColor(0, 0, 0, 0.5f);
        gameFinishedGroup.addActor(background);

        //replay button
        replayButtonTexture = new Texture("images/youtube.png");
        replayButton = new VisImage(replayButtonTexture);
        replayButton.setTouchable(Touchable.enabled);
        replayButton.setPosition(gameManager.WORLDWIDTH/2- replayButton.getWidth()/2,gameManager.WORLDHEIGHT/2- replayButton.getHeight()/2);
        replayButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                replayButtonPressed = true;
                gameFinishedGroup.setVisible(false);
                //drawGameFinishedStageGroup = false;
                //Gdx.input.setInputProcessor(inGameStage);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                replayButtonPressed = false;
            }
        });
        //add to table
        gameFinishedGroup.addActor(replayButton);


        //menu button
        menuButtonTexture = new Texture("images/menubutton.png");
        menuButton = new VisImage(menuButtonTexture);
        menuButton.setTouchable(Touchable.enabled);
        menuButton.setSize(52,52);
        menuButton.setPosition(replayButton.getX()+80,gameManager.WORLDHEIGHT/2-menuButton.getHeight()/2);
        menuButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                menuButtonPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                menuButtonPressed = false;
            }

        });
        //add to table
        gameFinishedGroup.addActor(menuButton);
        stage.addActor(gameFinishedGroup);
    }


    public void draw()
    {
        if(needSetInputProcessor) {
            Gdx.app.log("hello","");
            Gdx.input.setInputProcessor(stage);
            needSetInputProcessor = false;
        }

        stage.draw();
        stage.act();

        //since the frame is so fast that the replayButtonPressed and pauseButtonPressed is not returned to false in 2-3 frames
        //and furthermore, we want the result to be justTouched-like event, setting these variable to false after one frame is necessary
        replayButtonPressed = false;
        menuButtonPressed = false;
        pauseButtonPressed = false;


    }

    public void resize(int width, int height)
    {
        viewPort.update(width,height);
    }


    public Stage getStage() {
        return stage;
    }

    public boolean isReplayButtonPressed() {
        return replayButtonPressed;
    }

    public void dispose() {
        if (stage != null)
            stage.dispose();

        if(replayButtonTexture !=null)
            replayButtonTexture.dispose();

        if(stage !=null)
            stage.dispose();

        if(backgroundTexture !=null)
            backgroundTexture.dispose();

        if(menuButtonTexture!=null)
            menuButtonTexture.dispose();

        if(pauseButtonTexture!=null)
            pauseButtonTexture.dispose();

//        if(inGameStage!=null)
//            inGameStage.dispose();

    }


    public boolean isMenuButtonPressed() {
        return menuButtonPressed;
    }

    public boolean isPauseButtonPressed() {
        return pauseButtonPressed;
    }
}
