package noshanabi.game.PlayScreenUI;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;

import noshanabi.game.ButtonPrefabs.ContinueButton;
import noshanabi.game.ButtonPrefabs.MenuButton;
import noshanabi.game.ButtonPrefabs.PauseButton;
import noshanabi.game.GameManager;
import noshanabi.game.Resourses;

/**
 * Created by 2SMILE2 on 02/10/2017.
 */

public class InGameUI {

    //manage on-screen buttons
    Stage stage;
    //viewport
    Viewport viewPort;

    //Pause TouchableImage
    boolean pauseButtonPressed = false;
    PauseButton pauseButton;

    //GAME PAUSED UI
    Group gamePausedGroup;

    //continue button
    ContinueButton continueButton;
    boolean continueButtonPressed = false;

    VisImage gamePausedBackground;
    Texture gamePausedBackgroundTexture;

    //menu TouchableImage
    MenuButton menuButton;

    private boolean menuButtonPressed =false;


    //just for holding count down label
    VisLabel countDownLabel;
    float countDownTime = 3;

    public InGameUI(GameManager gameManager) {
        //set up
        viewPort = new StretchViewport(Resourses.WORLDWIDTH, Resourses.WORLDHEIGHT);
        stage = new Stage(viewPort, gameManager.batch);

        //--------------PAUSE BUTTON ------------------------
        pauseButton = new PauseButton();
        pauseButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                pauseButtonPressed = true;
                gamePausedGroup.setVisible(true);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                pauseButtonPressed = false;
            }
        });
        stage.addActor(pauseButton);


        //------------- GAME PAUSED STAGE ------------------
        //gamePausedStage = new Stage(viewPort, gameManager.batch);
        gamePausedGroup = new Group();

        //pause game background
        gamePausedBackgroundTexture = new Texture(Resourses.InGameBackground);
        gamePausedBackground = new VisImage(gamePausedBackgroundTexture);
        gamePausedBackground.setSize(Resourses.WORLDWIDTH-25,Resourses.WORLDHEIGHT-25);
        gamePausedBackground.setPosition(Resourses.WORLDWIDTH/2- gamePausedBackground.getWidth()/2,Resourses.WORLDHEIGHT/2- gamePausedBackground.getHeight()/2);
        gamePausedBackground.setColor(0, 0, 0, 0.5f);
        gamePausedGroup.addActor(gamePausedBackground);

        //continue button
        continueButton = new ContinueButton();
        continueButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                gamePausedGroup.setVisible(false);
                continueButtonPressed = true;
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
        menuButton = new MenuButton();
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
        stage.addActor(gamePausedGroup);
        gamePausedGroup.setVisible(false);

        //------COUNT DOWN LABEL --------------------
        VisTable table = new VisTable();
        table.setFillParent(true);
        table.top();

        countDownLabel = new VisLabel(""+(int)countDownTime);
        table.add(countDownLabel).padTop(100f);
        stage.addActor(table);


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

        stage.draw();
        stage.act();


        //since the frame is so fast that the reviewButtonPressed and pauseButtonPressed is not returned to false in 2-3 frames
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

    public Stage getStage() {
        return stage;
    }

    public boolean isContinueButtonPressed() {
        return continueButtonPressed;
    }


    public void dispose() {
        if (stage != null)
            stage.dispose();

        if(pauseButton!=null)
            pauseButton.dispose();

        if(continueButton!=null)
            continueButton.dispose();

        if(gamePausedBackgroundTexture !=null)
            gamePausedBackgroundTexture.dispose();

        if(menuButton!=null)
            menuButton.dispose();

    }


    public boolean isMenuButtonPressed() {
        return menuButtonPressed;
    }
}
