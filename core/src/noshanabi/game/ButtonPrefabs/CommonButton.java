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
 * Created by 2SMILE2 on 22/12/2017.
 */

public class CommonButton extends TextButton {

    private boolean isPressed = false;
    private float stateTime = 0;
    private float maxTime = 0.3f;
    GameManager gameManager;

    CommonButtonListener listener;

    public CommonButton(final GameManager gameManager)
    {
        super("",new Skin(Gdx.files.internal(Resourses.ButtonSkinJSON2),new TextureAtlas(Resourses.ButtonSkinTextureAtlas2)));
        this.gameManager = gameManager;

        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                gameManager.getAssetManager().get(Resourses.ClickSound, Sound.class).play();
                isPressed = true;
                Gdx.input.setInputProcessor(null);
                return true;
            }

        });

    }

    public void update(float dt) {
        if (isPressed) {
            stateTime += dt;
            if (stateTime > maxTime) {
                listener.OnClick();
                isPressed = false;
                stateTime = 0;
            }
        }
    }

    public void setListener(CommonButtonListener listener)
    {
        this.listener = listener;
    }




    public void dispose()
    {
        getSkin().dispose();
    }

}
