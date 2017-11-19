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

import noshanabi.game.GameManager;

/**
 * Created by 2SMILE2 on 02/10/2017.
 */

public class PlayScreenUI {

    //manage on-screen buttons
    Stage stage;
    //viewport
    Viewport viewPort;

    boolean screenPressed = false;

    //Pause Button
    Texture pauseButtonTexture;
    VisImage pauseButton;

    public PlayScreenUI(GameManager gameManager)
    {
        //set up
        viewPort = new StretchViewport(GameManager.WORLDWIDTH, GameManager.WORLDHEIGHT);
        //camera.position.set(viewPort.getWorldWidth() / 2, viewPort.getWorldHeight() / 2, 0);
        stage = new Stage(viewPort,gameManager.batch);
        //set this stage that will receive all touch events
        Gdx.input.setInputProcessor(stage);

        Group group = new Group();
        group.setSize(gameManager.WORLDWIDTH,gameManager.WORLDHEIGHT);
        //table.setSize(GameManager.WORLDWIDTH,GameManager.WORLDHEIGHT);
//        Image image = new Image(new Texture("images/bg.png"));
//        image.setSize(500,viewPort.getWorldHeight());
//        image.setColor(0f,0f,0f,0f);

        group.addListener(new InputListener()
        {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                screenPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                screenPressed = false;
            }
        });


        stage.addActor(group);


        //--------------PAUSE BUTTON ------------------------
        pauseButtonTexture = new Texture("images/pausebutton.png");
        pauseButton = new VisImage(pauseButtonTexture);
        pauseButton.setSize(32,32);
        pauseButton.setPosition(gameManager.WORLDWIDTH-pauseButton.getWidth()-10,gameManager.WORLDHEIGHT-pauseButton.getHeight()-10);

        pauseButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {

            }
        });

        stage.addActor(pauseButton);



    }


    public void draw()
    {
        stage.draw();
    }

    public void resize(int width, int height)
    {
        viewPort.update(width,height);
    }

    public boolean isScreenPressed()
    {
        return screenPressed;
    }

    public void dispose() {
        if (stage != null)
            stage.dispose();

        if(pauseButtonTexture!=null)
            pauseButtonTexture.dispose();

    }

    public Stage getStage() {
        return stage;
    }
}
