package noshanabi.game;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.badlogic.gdx.backends.android.AndroidFragmentApplication;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;

import noshanabi.game.Extensions.PlayerServices;

public class AndroidLauncher extends FragmentActivity implements AndroidFragmentApplication.Callbacks, PlayerServices {

	private FirebaseAuth mAuth;
	FirebaseAuth.AuthStateListener mAuthListener;
	private boolean isSignedIn;


	//GOOGLE SIGN IN VARIABLES
	private static int RC_SIGN_IN = 1;
	GoogleApiClient mGoogleApiClient;

	//FACEBOOK SIGN IN VARIABLES
	private CallbackManager mCallbackManager;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 6. Finally, replace the AndroidLauncher activity content with the Libgdx Fragment.
		GameFragment fragment = new GameFragment();
		fragment.setAndroidLauncher(this);
		FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
		trans.replace(android.R.id.content, fragment);
		trans.commit();

		//Get authentication instance
		mAuth = FirebaseAuth.getInstance();
		mAuthListener = new FirebaseAuth.AuthStateListener() {
			@Override
			public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
				if(firebaseAuth.getCurrentUser()!=null)
				{
					isSignedIn = true;
				}
				else
				{
					isSignedIn = false;

				}
			}
		};

		//---SETUP FOR GOOGLE SIGN IN
		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
				.requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();

		// Build a GoogleSignInClient with the options specified by gso.
		mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
			@Override
			public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
				Log.d("GOOGLE LOGIN", "Failed to connect");
			}
		}).addApi(Auth.GOOGLE_SIGN_IN_API,gso).build();



		//-----SETUP FOR FACEBOOK SIGN IN
		mCallbackManager = CallbackManager.Factory.create();

		LoginManager.getInstance().registerCallback(mCallbackManager,
				new FacebookCallback<LoginResult>() {
					@Override
					public void onSuccess(LoginResult loginResult) {
						Log.d("SUCCESS", "SUCCESS LOGIN TO FACEBOOK");
						handleFacebookAccessToken(loginResult.getAccessToken());
					}

					@Override
					public void onCancel() {
						Log.d("LOGIN CANCEL", "CANCEL LOGIN TO FACEBOOK");
					}

					@Override
					public void onError(FacebookException exception) {
						Log.d("LOGIN ERROR", "ERROR LOGIN TO FACEBOOK");
					}
				});

	}

	@Override
	public void onStart() {
		super.onStart();

		mAuth.addAuthStateListener(mAuthListener);
	}

	@Override
	public void onStop() {
		super.onStop();
		mAuth.removeAuthStateListener(mAuthListener);
	}

	@Override
	public void exit() {}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
		if (requestCode == RC_SIGN_IN) {
			GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
			if(result.isSuccess())
			{
				// Google Sign In was successful, authenticate with Firebase
				GoogleSignInAccount account = result.getSignInAccount();
				firebaseAuthWithGoogle(account);
			} else {
				// Google Sign In failed, update UI appropriately
				Log.d("GOOGLE LOGIN", "failed on sign in api");

			}
		}

		if(mCallbackManager.onActivityResult(requestCode, resultCode, data)) {
			return;
		}


	}


	private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
		Log.d("GOOGLE LOGIN", "firebaseAuthWithGoogle:" + acct.getId());

		AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
		mAuth.signInWithCredential(credential)
				.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
					@Override
					public void onComplete(@NonNull Task<AuthResult> task) {
						if (task.isSuccessful()) {
							// Sign in success, update UI with the signed-in user's information
							Log.d("GOOGLE LOGIN", "signInWithCredential:success");

						} else {
							// If sign in fails, display a message to the user.
							Log.w("GOOGLE LOGIN", "signInWithCredential:failure", task.getException());

						}

					}
				});
	}

	private void handleFacebookAccessToken(AccessToken token) {
		Log.d("FACEBOOK LOGIN", "handleFacebookAccessToken:" + token);

		AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
		mAuth.signInWithCredential(credential)
				.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
					@Override
					public void onComplete(@NonNull Task<AuthResult> task) {
						if (task.isSuccessful()) {
							// Sign in success, update UI with the signed-in user's information
							Log.d("FACEBOOK LOGIN", "signInWithCredential:success");


						} else {
							// If sign in fails, display a message to the user.
							Log.w("FACEBOOK LOGIN", "signInWithCredential:failure", task.getException());

						}

						// ...
					}
				});
	}

	@Override
	public void signInToGoogle() {

		Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
		startActivityForResult(signInIntent, RC_SIGN_IN);
	}

	@Override
	public void signOut()
	{
		Auth.GoogleSignInApi.signOut(mGoogleApiClient);
		LoginManager.getInstance().logOut();
		FirebaseAuth.getInstance().signOut();
		isSignedIn = false;
	}

	@Override
	public void signInToFacebook() {
		LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends"));
	}


	@Override
	public boolean isSignedIn()
	{
		return isSignedIn;
	}

	@Override
	public String getUserName()
	{
		return FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
	}

}
