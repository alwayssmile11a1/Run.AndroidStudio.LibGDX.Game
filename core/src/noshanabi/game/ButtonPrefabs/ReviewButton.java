package noshanabi.game.ButtonPrefabs;

import noshanabi.game.Resourses;

/**
 * Created by 2SMILE2 on 22/11/2017.
 */

public class ReviewButton extends TouchableImage {


    public ReviewButton()
    {
        super(Resourses.ReviewButton);

        setSize(64,64);

        setPosition(Resourses.WORLDWIDTH/2- this.getWidth()/2,Resourses.WORLDHEIGHT/2- this.getHeight()/2);

    }

}
