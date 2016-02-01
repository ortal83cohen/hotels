package com.etb.app.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.etb.app.R;
import com.etb.app.adapter.ViewPagerAdapter;
import com.etb.app.fragment.LoginFragment;
import com.etb.app.fragment.SignUpFragment;
import com.etb.app.login.GooglePlusLogin;
import com.facebook.FacebookSdk;

/**
 * @author user
 * @date 2015-08-02
 */
public class LoginActivity extends TabActivity implements GooglePlusLogin.Listener {

    private GooglePlusLogin mGooglePlusLogin;

    public static Intent createIntent(int tabId, Context context) {
        Intent bookIntent = new Intent(context, LoginActivity.class);
        bookIntent.putExtra(EXTRA_TABID, tabId);
        return bookIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getApplicationContext());
        super.onCreate(savedInstanceState);

        mGooglePlusLogin = new GooglePlusLogin(this, savedInstanceState);
        mGooglePlusLogin.setListener(this);

    }

    public GooglePlusLogin getGooglePlusLogin() {
        return mGooglePlusLogin;
    }


    @Override
    protected void onCreateTabFragments(ViewPagerAdapter adapter, Bundle savedInstanceState) {
        adapter.addFragment(LoginFragment.newInstance(), getString(R.string.tab_title_login), "fragment_login");
        adapter.addFragment(SignUpFragment.newInstance(), getString(R.string.tab_title_sign_up), "fragment_sign_up");
    }

    @Override
    public void onStart() {
        super.onStart();
        mGooglePlusLogin.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGooglePlusLogin.stop();
    }

    @Override
    protected void onCreateContentView() {
        setContentView(R.layout.activity_login);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!mGooglePlusLogin.onActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onGooglePlusLogin(String email, String firstName, String lastName) {

    }
}


