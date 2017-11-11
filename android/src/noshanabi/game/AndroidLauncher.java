package noshanabi.game;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import noshanabi.game.Extensions.FacebookPlayerServices;

public class AndroidLauncher extends AndroidApplication implements FacebookPlayerServices, GoogleApiClient.OnConnectionFailedListener {


	private FirebaseAuth mAuth;
	private GoogleSignInClient mGoogleSignInClient;
	private static int RC_SIGN_IN = 1;

	private String TAG = "GOOGLE LOGIN";

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new GameManager(this), config);

		mAuth = FirebaseAuth.getInstance();


		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
				.requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();

		// Build a GoogleSignInClient with the options specified by gso.
		mGoogleSignInClient = GoogleSignIn.getClient(this, gso);




	}

	@Override
	public void onStart() {
		super.onStart();

		// Check if user is signed in (non-null) and update UI accordingly.
		FirebaseUser currentUser = mAuth.getCurrentUser();
	}

	@Override
	public void onStop() {
		super.onStop();

	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
		Log.d(TAG,"Connection failed");
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
		if (requestCode == RC_SIGN_IN) {
			Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
			try {
				// Google Sign In was successful, authenticate with Firebase
				GoogleSignInAccount account = task.getResult(ApiException.class);
				firebaseAuthWithGoogle(account);
			} catch (ApiException e) {
				// Google Sign In failed, update UI appropriately
				Log.w(TAG, "Google sign in failed", e);
				// ...
			}
		}
	}

	private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
		Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

		AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
		mAuth.signInWithCredential(credential)
				.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
					@Override
					public void onComplete(@NonNull Task<AuthResult> task) {
						if (task.isSuccessful()) {
							// Sign in success, update UI with the signed-in user's information
							Log.d(TAG, "signInWithCredential:success");
							FirebaseUser user = mAuth.getCurrentUser();

						} else {
							// If sign in fails, display a message to the user.
							Log.w(TAG, "signInWithCredential:failure", task.getException());

						}

					}
				});
	}

	@Override
	public void signIn() {
		Intent signInIntent = mGoogleSignInClient.getSignInIntent();
		startActivityForResult(signInIntent, RC_SIGN_IN);
	}

	@Override
	public void signOut() {
		FirebaseAuth.getInstance().signOut();
	}

}
