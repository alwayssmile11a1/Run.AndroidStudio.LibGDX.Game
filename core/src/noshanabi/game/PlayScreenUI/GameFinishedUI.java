package noshanabi.game.PlayScreenUI;

import com.badlogic.gdx.Gdx;
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

import noshanabi.game.ButtonPrefabs.MenuButton;
import noshanabi.game.ButtonPrefabs.PauseButton;
import noshanabi.game.ButtonPrefabs.ReplayButton;
import noshanabi.game.ButtonPrefabs.ReviewButton;
import noshanabi.game.GameManager;
import noshanabi.game.Resourses;

/**
 * Created by 2SMILE2 on 21/11/2017.
 */

public class GameFinishedUI {

    //viewport
    Viewport viewPort;


    //finish game stage
    Stage stage;


    //Pause TouchableImage
    boolean pauseButtonPressed = false;
    PauseButton pauseButton;

    //group
    Group gameFinishedGroup;
    //replay button
    ReviewButton reviewButton;
    boolean reviewButtonPressed = false;

    //background
    VisImage background;
    Texture backgroundTexture;

    //replay button
    ReplayButton replayButton;
    boolean replayButtonPressed = false;

    //menu button
    MenuButton menuButton;
    private boolean menuButtonPressed =false;


    //display playing time
    VisTable table;
    VisLabel playTimeLabel;


    private boolean needSetInputProcessor = true;
//    private boolean drawGameFinishedStageGroup = false;

    public GameFinishedUI(GameManager gameManager) {
        //set up
        viewPort = new StretchViewport(Resourses.WORLDWIDTH, Resourses.WORLDHEIGHT);
        //stage = new Stage(viewPort, gameManager.batch);
        stage = new Stage(viewPort, gameManager.batch);

        //--------------PAUSE BUTTON ------------------------
        pauseButton = new PauseButton();
        pauseButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                pauseButtonPressed = true;
                gameFinishedGroup.setVisible(true);
                table.setVisible(true);
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
        backgroundTexture = new Texture(Resourses.GameFinishedBackground);
        background = new VisImage(backgroundTexture);
        background.setSize(Resourses.WORLDWIDTH-25,Resourses.WORLDHEIGHT-25);
        background.setPosition(Resourses.WORLDWIDTH/2- background.getWidth()/2,Resourses.WORLDHEIGHT/2- background.getHeight()/2);
        background.setColor(0, 0, 0, 0.5f);
        gameFinishedGroup.addActor(background);

        //replay button
        replayButton = new ReplayButton();
        replayButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                replayButtonPressed = true;
                gameFinishedGroup.setVisible(false);
                table.setVisible(false);
                //drawGameFinishedStageGroup = false;
                //Gdx.input.setInputProcessor(stage);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                replayButtonPressed = false;
            }
        });
        //add to table
        gameFinishedGroup.addActor(replayButton);

        //review button
        reviewButton = new ReviewButton();
        reviewButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                reviewButtonPressed = true;
                gameFinishedGroup.setVisible(false);
                table.setVisible(false);
                //drawGameFinishedStageGroup = false;
                //Gdx.input.setInputProcessor(stage);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                reviewButtonPressed = false;
            }
        });
        //add to table
        gameFinishedGroup.addActor(reviewButton);


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
        gameFinishedGroup.addActor(menuButton);
        stage.addActor(gameFinishedGroup);


        //----------------PLAY TIME LABEL --------------------
        table = new VisTable();
        table.setFillParent(true);
        table.top();

        playTimeLabel = new VisLabel();
        table.add(playTimeLabel).padTop(100f);
        stage.addActor(table);

    }

    public void reset()
    {
        needSetInputProcessor = true;
        gameFinishedGroup.setVisible(true);
        table.setVisible(true);
        reviewButtonPressed = false;
        menuButtonPressed = false;
        pauseButtonPressed = false;
        replayButtonPressed = false;
    }

    public void setPlayTimeText(String text)
    {
        playTimeLabel.setText(text);
    }

    public void draw()
    {
        if(needSetInputProcessor) {
            Gdx.input.setInputProcessor(stage);
            needSetInputProcessor = false;
        }

        stage.draw();
        stage.act();

        //since the frame is so fast that the reviewButtonPressed and pauseButtonPressed is not returned to false in 2-3 frames
        //and furthermore, we want the result to be justTouched-like event, setting these variable to false after one frame is necessary
        reviewButtonPressed = false;
        menuButtonPressed = false;
        pauseButtonPressed = false;
        replayButtonPressed = false;

    }

    public void resize(int width, int height)
    {
        viewPort.update(width,height);
    }


    public Stage getStage() {
        return stage;
    }

    public boolean isReviewButtonPressed() {
        return reviewButtonPressed;
    }

    public void dispose() {
        if (stage != null)
            stage.dispose();

        if(reviewButton !=null)
            reviewButton.dispose();

        if(stage !=null)
            stage.dispose();

        if(backgroundTexture !=null)
            backgroundTexture.dispose();

        if(menuButton!=null)
            menuButton.dispose();

        if(pauseButton!=null)
            pauseButton.dispose();

        if(replayButton!=null)
            replayButton.dispose();

    }


    public boolean isMenuButtonPressed() {
        return menuButtonPressed;
    }

    public boolean isPauseButtonPressed() {
        return pauseButtonPressed;
    }

    public boolean isReplayButtonPressed() {
        return replayButtonPressed;
    }
}
