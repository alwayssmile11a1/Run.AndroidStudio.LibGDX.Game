package noshanabi.game.ButtonPrefabs;


import noshanabi.game.Resourses;

/**
 * Created by 2SMILE2 on 15/11/2017.
 */

public class ReturnScreenButton extends TouchableImage {

    public ReturnScreenButton()
    {
        super(Resourses.ReturnScreenButton);

        setSize(50, 50);
        setOrigin(this.getWidth() / 2, this.getHeight() / 2);
        //just to flip this image
        setScaleX(-1);
        setPosition(10, Resourses.WORLDHEIGHT - 60);

    }


}
