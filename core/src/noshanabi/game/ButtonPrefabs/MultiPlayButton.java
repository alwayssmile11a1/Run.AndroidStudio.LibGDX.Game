package noshanabi.game.ButtonPrefabs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import noshanabi.game.GameManager;
import noshanabi.game.Resourses;

/**
 * Created by 2SMILE2 on 22/11/2017.
 */

public class MultiPlayButton extends TextButton {

    private boolean isPressed = false;
    private float stateTime = 0;
    private float maxTime = 0.3f;
    GameManager gameManager;

    public MultiPlayButton(GameManager gameManager)
    {
        super("MultiPlayer",new Skin(Gdx.files.internal(Resourses.ButtonSkinJSON),new TextureAtlas(Resourses.ButtonSkinTextureAtlas)));
        this.gameManager = gameManager;

        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                isPressed = true;
                Gdx.input.setInputProcessor(null);
                return true;
            }

        });

    }

    public void update(float dt) {
        if (isPressed) {
            stateTime += dt;
            if (stateTime > maxTime)
            {
                if (gameManager.getPlayerServices() == null) //desktop test
                {
                    Gdx.input.setInputProcessor(gameManager.getLoginScreen().getStage());
                    gameManager.setScreen(gameManager.getLoginScreen());

                } else
                    {

                    if (gameManager.getPlayerServices().isSignedIn()) {
                        gameManager.connectToServer();
                        Gdx.input.setInputProcessor(gameManager.getModeSelectionScreen().getStage());
                        gameManager.setScreen(gameManager.getModeSelectionScreen());
                    }
                    else
                    {
                        Gdx.input.setInputProcessor(gameManager.getLoginScreen().getStage());
                        gameManager.setScreen(gameManager.getLoginScreen());
                    }

                }

                isPressed = false;
                stateTime = 0;
            }
        }
    }

    public void dispose()
    {
        getSkin().dispose();
    }


}
