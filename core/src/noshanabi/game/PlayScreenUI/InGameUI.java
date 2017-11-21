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
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;

import noshanabi.game.GameManager;

/**
 * Created by 2SMILE2 on 02/10/2017.
 */

public class InGameUI {

    //manage on-screen buttons
    Stage inGameStage;
    //viewport
    Viewport viewPort;

    //Pause Button
    boolean pauseButtonPressed = false;
    Texture pauseButtonTexture;
    VisImage pauseButton;

    //Pause game stage
    Stage gamePausedStage;
    VisImage continueButton;
    Texture continueButtonTexture;
    boolean continueButtonPressed = false;
    private boolean drawPauseGameStage = false;
    VisImage gamePausedBackground;
    Texture gamePausedBackgroundTexture;

    Texture menuButtonTexture;
    VisImage menuButton;

    private boolean menuButtonPressed =false;


    //just for holding count down label
    VisLabel countDownLabel;
    float countDownTime = 3;

    public InGameUI(GameManager gameManager) {
        //set up
        viewPort = new StretchViewport(GameManager.WORLDWIDTH, GameManager.WORLDHEIGHT);
        inGameStage = new Stage(viewPort, gameManager.batch);

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
                Gdx.input.setInputProcessor(gamePausedStage);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                pauseButtonPressed = false;
            }
        });
        inGameStage.addActor(pauseButton);


        //------------- GAME PAUSED STAGE ------------------
        gamePausedStage = new Stage(viewPort, gameManager.batch);
        Group gamePausedGroup = new Group();

        //pause game background
        gamePausedBackgroundTexture = new Texture("images/WhiteRectangle.png");
        gamePausedBackground = new VisImage(gamePausedBackgroundTexture);
        gamePausedBackground.setSize(gameManager.WORLDWIDTH-25,gameManager.WORLDHEIGHT-25);
        gamePausedBackground.setPosition(gameManager.WORLDWIDTH/2- gamePausedBackground.getWidth()/2,gameManager.WORLDHEIGHT/2- gamePausedBackground.getHeight()/2);
        gamePausedBackground.setColor(0, 0, 0, 0.5f);
        gamePausedStage.addActor(gamePausedBackground);

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
        gamePausedGroup.addActor(continueButton);


        //menu button
        menuButtonTexture = new Texture("images/menubutton.png");
        menuButton = new VisImage(menuButtonTexture);
        menuButton.setTouchable(Touchable.enabled);
        menuButton.setSize(52,52);
        menuButton.setPosition(continueButton.getX()+80,gameManager.WORLDHEIGHT/2-menuButton.getHeight()/2);
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
        gamePausedGroup.addActor(menuButton);
        gamePausedStage.addActor(gamePausedGroup);


        //------COUNT DOWN LABEL --------------------
        VisTable table = new VisTable();
        table.setFillParent(true);
        table.top();

        countDownLabel = new VisLabel(""+(int)countDownTime);
        table.add(countDownLabel).padTop(100f);
        inGameStage.addActor(table);

    }

    public float getCountDownTime()
    {
        return countDownTime;
    }

    public void setCountDownTime(float countDownTime)
    {
        this.countDownTime = countDownTime;
    }

    public void setCountDownText(String text)
    {
        countDownLabel.setText(text);
    }


    public void draw()
    {

        inGameStage.draw();
        inGameStage.act();

        if(drawPauseGameStage)
        {
            gamePausedStage.draw();
            gamePausedStage.act();
        }

        //since the frame is so fast that the replayButtonPressed and pauseButtonPressed is not returned to false in 2-3 frames
        //and furthermore, we want the result to be justTouched-like event, setting these variable to false after one frame is necessary
        continueButtonPressed = false;
        pauseButtonPressed = false;
        menuButtonPressed = false;


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

    public Stage getGamePausedStage() {
        return gamePausedStage;
    }

    public void dispose() {
        if (inGameStage != null)
            inGameStage.dispose();

        if(pauseButtonTexture!=null)
            pauseButtonTexture.dispose();

        if(continueButtonTexture!=null)
            continueButtonTexture.dispose();

        if(gamePausedStage !=null)
            gamePausedStage.dispose();

        if(gamePausedBackgroundTexture !=null)
            gamePausedBackgroundTexture.dispose();

        if(menuButtonTexture!=null)
            menuButtonTexture.dispose();

    }


    public boolean isMenuButtonPressed() {
        return menuButtonPressed;
    }
}
