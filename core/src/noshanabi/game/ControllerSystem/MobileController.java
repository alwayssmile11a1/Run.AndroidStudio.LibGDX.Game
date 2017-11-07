package noshanabi.game.ControllerSystem;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import noshanabi.game.GameManager;

/**
 * Created by 2SMILE2 on 02/10/2017.
 */

public class MobileController {

    //manage on-screen buttons
    Stage stage;
    //viewport
    Viewport viewPort;

    boolean leftScreenPressed = false;

    public MobileController(GameManager gameManager)
    {
        //set up
        viewPort = new StretchViewport(GameManager.WORLDWIDTH, GameManager.WORLDHEIGHT);
        //camera.position.set(viewPort.getWorldWidth() / 2, viewPort.getWorldHeight() / 2, 0);
        stage = new Stage(viewPort,gameManager.batch);
        //set this stage that will receive all touch events
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.right().setSize(500,viewPort.getScreenHeight());
        table.setFillParent(true);
        //table.setSize(GameManager.WORLDWIDTH,GameManager.WORLDHEIGHT);
//        Image image = new Image(new Texture("images/bg.png"));
//        image.setSize(500,viewPort.getWorldHeight());
//        image.setColor(0f,0f,0f,0f);

        table.addListener(new InputListener()
        {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                leftScreenPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                leftScreenPressed = false;
            }
        });

        table.add();
        //table.add(image);

        stage.addActor(table);

    }


    public void draw()
    {
        stage.draw();
    }

    public void resize(int width, int height)
    {
        viewPort.update(width,height);
    }

    public boolean isLeftScreenPressed()
    {
        return leftScreenPressed;
    }

    public void dispose() {
//        if (stage != null)
//            stage.dispose();
    }

}
