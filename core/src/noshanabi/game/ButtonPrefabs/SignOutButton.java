package noshanabi.game.ButtonPrefabs;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

import noshanabi.game.GameManager;

/**
 * Created by 2SMILE2 on 15/11/2017.
 */

public class SignOutButton extends Image{

    private Texture signOutTexture;

    public SignOutButton()
    {
        signOutTexture = new Texture("images/signout.png");

        setDrawable(new SpriteDrawable(new Sprite(signOutTexture)));

        setBounds(0, 0, signOutTexture.getWidth(), signOutTexture.getHeight());

        setTouchable(Touchable.enabled);

        //set position and size
        setPosition(GameManager.WORLDWIDTH - 60, GameManager.WORLDHEIGHT - 60);
        setSize(50, 50);

    }

    public void dispose()
    {
        if(signOutTexture!=null)
            signOutTexture.dispose();
    }


}
