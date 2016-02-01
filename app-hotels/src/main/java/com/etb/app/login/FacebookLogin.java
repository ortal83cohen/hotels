package com.etb.app.login;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;

import com.etb.app.utils.AppLog;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * @author alex
 * @date 2015-05-06
 */
public class FacebookLogin implements FacebookCallback<LoginResult>, View.OnClickListener {
    private final Listener mListener;
    private final LoginManager mLoginManager;
    private CallbackManager mFacebookCallback;
    private Fragment mFragment;

    public FacebookLogin(Fragment fragment, Button button, Listener listener) {
        mListener = listener;
        mFacebookCallback = CallbackManager.Factory.create();
        mFragment = fragment;
        mLoginManager = LoginManager.getInstance();

        button.setOnClickListener(this);

        if (AccessToken.getCurrentAccessToken() != null) {
            requestUserInfo(AccessToken.getCurrentAccessToken());
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mFacebookCallback.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSuccess(LoginResult loginResult) {
        AppLog.d("FacebookCallback", "onSuccess");

        requestUserInfo(loginResult.getAccessToken());
    }

    @Override
    public void onCancel() {
        AppLog.d("FacebookCallback", "onCancel");
    }

    @Override
    public void onError(FacebookException e) {
        AppLog.e("FacebookCallback", e.getMessage(), e);
        AppLog.e(e);
    }

    private void requestUserInfo(AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {

            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {

                try {
                    mListener.onFacebookLogin(object.getString("email"), object.getString("first_name"), object.getString("last_name"));
                } catch (JSONException e) {
                    AppLog.e("FacebookCallback", e.getMessage(), e);
                    AppLog.e(e);
                }

            }
        });
        request.executeAsync();
    }

    @Override
    public void onClick(View v) {
        mLoginManager.logInWithReadPermissions(mFragment, Arrays.asList("public_profile", "email"));
        mLoginManager.registerCallback(mFacebookCallback, this);
    }

    public interface Listener {
        void onFacebookLogin(String email, String firstName, String lastName);
    }
}
