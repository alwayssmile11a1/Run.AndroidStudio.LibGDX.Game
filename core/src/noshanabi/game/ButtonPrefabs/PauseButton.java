package noshanabi.game.ButtonPrefabs;

import noshanabi.game.Resourses;

/**
 * Created by 2SMILE2 on 22/11/2017.
 */

public class PauseButton extends TouchableImage {


    public PauseButton()
    {
        super(Resourses.PauseButton);

        setSize(32, 32);

        setPosition(Resourses.WORLDWIDTH - this.getWidth() - 10, Resourses.WORLDHEIGHT - this.getHeight() - 10);


    }


}
