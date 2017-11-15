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

public class ReturnScreenButton extends Image {

    private Texture returnScreenTexture;

    public ReturnScreenButton()
    {
        returnScreenTexture = new Texture("images/rightarrow.png");

        setDrawable(new SpriteDrawable(new Sprite(returnScreenTexture)));

        setBounds(0, 0, returnScreenTexture.getWidth(), returnScreenTexture.getHeight());
        setTouchable(Touchable.enabled);

        setSize(50, 50);
        setOrigin(this.getWidth() / 2, this.getHeight() / 2);
        //just to flip this image
        setScaleX(-1);
        setPosition(10, GameManager.WORLDHEIGHT - 60);

    }



    public void dispose()
    {
        if(returnScreenTexture!=null)
            returnScreenTexture.dispose();
    }

}
