package com.etb.app.login;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;


/**
 * @author alex
 * @date 2015-05-06
 */
public class GooglePlusLogin implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener, ResultCallback<People.LoadPeopleResult> {

    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;
    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    private final AppCompatActivity mActivity;
    /* Client used to interact with Google APIs. */
    private GoogleApiClient mGoogleApiClient;
    /**
     * True if the sign-in button was clicked.  When true, we know to resolve all
     * issues preventing sign-in without waiting.
     */
    private boolean mSignInClicked;
    /**
     * True if we are in the process of resolving a ConnectionResult
     */
    private boolean mIntentInProgress;
    private Listener mListener;


    public GooglePlusLogin(AppCompatActivity activity, Bundle savedInstanceState) {

        mActivity = activity;

        mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                .build();
    }

    public void start() {
        mGoogleApiClient.connect();
    }

    public void stop() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            if (resultCode != Activity.RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnected()) {
                mGoogleApiClient.reconnect();
            }
            return true;
        } else if (requestCode == REQUEST_RESOLVE_ERROR) {
            if (resultCode == Activity.RESULT_OK) {
                // Make sure the app is not already connected or attempting to start
                if (!mGoogleApiClient.isConnecting() &&
                        !mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Plus.PeopleApi.loadConnected(mGoogleApiClient).setResultCallback(this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!mIntentInProgress) {
            if (mSignInClicked && result.hasResolution()) {
                // The user has already clicked 'sign-in' so we attempt to resolve all
                // errors until the user is signed in, or they cancel.
                try {
                    result.startResolutionForResult(mActivity, RC_SIGN_IN);
                    mIntentInProgress = true;
                } catch (IntentSender.SendIntentException e) {
                    // The intent was canceled before it was sent.  Return to the default
                    // state and attempt to start to get an updated ConnectionResult.
                    mIntentInProgress = false;
                    mGoogleApiClient.connect();
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (!mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onResult(People.LoadPeopleResult loadPeopleResult) {
        String email = Plus.AccountApi.getAccountName(mGoogleApiClient);

        Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);

        if (currentPerson != null && mListener != null) {
            mListener.onGooglePlusLogin(email, currentPerson.getName().getGivenName(), currentPerson.getName().getFamilyName());
        }
    }


    public interface Listener {
        void onGooglePlusLogin(String email, String firstName, String lastName);
    }

}
