package noshanabi.game.ButtonPrefabs;

import noshanabi.game.Resourses;

/**
 * Created by 2SMILE2 on 26/12/2017.
 */

public class InspectButton extends TouchableImage{

    public InspectButton()
    {
        super(Resourses.InspectButton);

        setSize(64,64);

        setPosition(Resourses.WORLDWIDTH/2- this.getWidth()/2,Resourses.WORLDHEIGHT/2- this.getHeight()/2);

    }


}
