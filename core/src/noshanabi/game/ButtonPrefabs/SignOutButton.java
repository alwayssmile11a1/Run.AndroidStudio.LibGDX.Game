package noshanabi.game.ButtonPrefabs;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import noshanabi.game.GameManager;
import noshanabi.game.Resourses;

/**
 * Created by 2SMILE2 on 15/11/2017.
 */

public class SignOutButton extends Button{

    private boolean isPressed = false;
    GameManager gameManager;

    public SignOutButton(final GameManager gameManager)
    {
        super(new Skin(Gdx.files.internal(Resourses.SignoutSkinJSON),new TextureAtlas(Resourses.SignoutSkinTextureAtlas)));
        this.gameManager = gameManager;

        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                gameManager.getAssetManager().get(Resourses.ClickSound, Sound.class).play();
                isPressed = true;
                return true;
            }

        });

        this.setSize(50,50);
        this.setPosition(Resourses.WORLDWIDTH-this.getWidth()-10,Resourses.WORLDHEIGHT-60);
    }

    public void update(float dt) {

        if (isPressed) {
            isPressed = false;

            if (gameManager.getPlayerServices() != null) {
                gameManager.getPlayerServices().signOut();
            }
            gameManager.getServer().getSocket().disconnect();
            Gdx.input.setInputProcessor(gameManager.getLoginScreen().getStage());
            gameManager.setScreen(gameManager.getLoginScreen());

        }
    }

    public void dispose()
    {
        getSkin().dispose();
    }
}
