package noshanabi.game.ButtonPrefabs;

import noshanabi.game.Resourses;

/**
 * Created by 2SMILE2 on 22/11/2017.
 */

public class ContinueButton extends TouchableImage {

    public ContinueButton()
    {
        super(Resourses.ContinueButton);

        setPosition(Resourses.WORLDWIDTH/2-this.getWidth()/2, Resourses.WORLDHEIGHT/2-this.getHeight()/2);

    }


}
