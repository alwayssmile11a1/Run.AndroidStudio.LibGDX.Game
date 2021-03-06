package noshanabi.game.ButtonPrefabs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
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

public class GoogleLoginButton extends TextButton {

    private boolean isPressed = false;
    private float stateTime = 0;
    private float maxTime = 0.2f;
    GameManager gameManager;

    public GoogleLoginButton(final GameManager gameManager)
    {
        super("GOOGLE LOGIN",new Skin(Gdx.files.internal(Resourses.GoogleLoginSkinJSON),new TextureAtlas(Resourses.GoogleSkinTextureAtlas)));
        this.gameManager = gameManager;

        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                gameManager.getAssetManager().get(Resourses.ClickSound, Sound.class).play();
                isPressed = true;

                return true;
            }

        });
    }

    public boolean isPressed()
    {
        return  isPressed;
    }

    public void update(float dt) {

        if (isPressed) {
            stateTime += dt;
            if (stateTime > maxTime) {

                if(gameManager.getPlayerServices()!=null) {

                    gameManager.getPlayerServices().signInToGoogle();

                }
                else //desktop test
                {
                    gameManager.connectToServer();
                    Gdx.input.setInputProcessor(gameManager.getModeSelectionScreen().getStage());
                    gameManager.setScreen(gameManager.getModeSelectionScreen());
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
