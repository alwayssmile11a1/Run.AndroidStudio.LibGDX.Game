package noshanabi.game.ButtonPrefabs;


import noshanabi.game.Resourses;

/**
 * Created by 2SMILE2 on 15/11/2017.
 */

public class SignOutButton extends TouchableImage{

    public SignOutButton()
    {
        super(Resourses.SignOutButton);

        //set position and size
        setPosition(Resourses.WORLDWIDTH - 60, Resourses.WORLDHEIGHT - 60);
        setSize(50, 50);

    }
}
