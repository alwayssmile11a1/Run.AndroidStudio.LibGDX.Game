package noshanabi.game.ButtonPrefabs;


import noshanabi.game.Resourses;

/**
 * Created by 2SMILE2 on 22/11/2017.
 */

public class ReplayButton extends TouchableImage {


    public ReplayButton()
    {
        super(Resourses.ReplayButton);

        setSize(52, 52);

        setPosition(Resourses.WORLDWIDTH/2 - this.getWidth()/2 - 75, Resourses.WORLDHEIGHT/2 - this.getHeight()/2);

    }



}
