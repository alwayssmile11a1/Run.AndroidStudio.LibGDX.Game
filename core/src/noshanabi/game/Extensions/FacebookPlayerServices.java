package noshanabi.game.Extensions;

/**
 * Created by 2SMILE2 on 11/11/2017.
 */

public interface FacebookPlayerServices {
    public void signIn();
    public void signOut();
    public void rateGame();
    public void unlockAchievement();
    public void submitScore(int highScore);
    public void showAchievement();
    public void showScore();
    public boolean isSignedIn();
}
