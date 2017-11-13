package noshanabi.game.Extensions;

/**
 * Created by 2SMILE2 on 11/11/2017.
 */

public interface PlayerServices {
    public void signInToGoogle();
    public void signInToFacebook();

    public boolean isSignedIn();

    public void signOut();

    public String getUserName();

}
