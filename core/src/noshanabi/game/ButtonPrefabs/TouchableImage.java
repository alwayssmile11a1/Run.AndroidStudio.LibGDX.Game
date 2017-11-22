package noshanabi.game.ButtonPrefabs;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.kotcrab.vis.ui.widget.VisImage;

/**
 * Created by 2SMILE2 on 22/11/2017.
 */

public class TouchableImage extends VisImage{

    private Texture texture;

    public TouchableImage(String path)
    {
        texture = new Texture(path);
        setDrawable(new SpriteDrawable(new Sprite(texture)));
        setTouchable(Touchable.enabled);
        setBounds(0,0,texture.getWidth(),texture.getHeight());

    }

    public void setTexture(String path) {

        //dispose for sure
        if(texture!=null)
            texture.dispose();

        texture = new Texture(path);

        setDrawable(new SpriteDrawable(new Sprite(texture)));

    }

    public void dispose()
    {
        if(texture!=null)
            texture.dispose();


    }

}
