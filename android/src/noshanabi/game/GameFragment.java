package noshanabi.game;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.badlogic.gdx.backends.android.AndroidFragmentApplication;

import noshanabi.game.Extensions.PlayerServices;

/**
 * Created by 2SMILE2 on 12/11/2017.
 */

public class GameFragment extends AndroidFragmentApplication implements PlayerServices {
    AndroidLauncher androidLauncher;

    public void setAndroidLauncher(AndroidLauncher androidLauncher)
    {
        this.androidLauncher = androidLauncher;
    }

    // 5. Add the initializeForView() code in the Fragment's onCreateView method.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {  return initializeForView(new GameManager( this));   }

    @Override
    public void signInToGoogle()
    {
        androidLauncher.signInToGoogle();
    }

    @Override
    public void signOutFromGoogle() {
        androidLauncher.signOutFromGoogle();
    }

    @Override
    public void signInToFacebook() {
        androidLauncher.signInToFacebook();
    }

    @Override
    public void signOutFromFacebook() {
        androidLauncher.signOutFromFacebook();
    }


}
