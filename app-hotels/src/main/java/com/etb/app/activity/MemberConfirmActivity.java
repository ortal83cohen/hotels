package com.etb.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.etb.app.App;
import com.etb.app.R;
import com.etb.app.etbapi.RetrofitCallback;
import com.etb.app.login.PasswordLogin;
import com.etb.app.member.model.AccessToken;
import com.etb.app.utils.AppLog;
import com.etb.app.utils.BrowserUtils;
import com.squareup.okhttp.ResponseBody;

import retrofit.Response;

/**
 * @author alex
 * @date 2015-08-13
 */
public class MemberConfirmActivity extends Activity implements PasswordLogin.Listener {

    private PasswordLogin mPasswordLogin;

    @Override
    public void onStart() {
        super.onStart();

        mPasswordLogin = new PasswordLogin(this, this);

        Uri data = getIntent().getData();
        AppLog.d(data.toString());

        String token = data.getQueryParameter("token");

        App.provide(this).memberService().confirm(token).enqueue(new RetrofitCallback<AccessToken>() {
            @Override
            protected void failure(ResponseBody response, boolean isOffline) {
                Toast.makeText(MemberConfirmActivity.this, R.string.cannot_confirm_token, Toast.LENGTH_SHORT).show();
                BrowserUtils.sendToWebBrowser(getIntent().getData(), MemberConfirmActivity.this);
                finish();
            }

            @Override
            protected void success(AccessToken accessToken, Response<AccessToken> response) {
                mPasswordLogin.onAccessToken(accessToken);
            }
        });

    }


    @Override
    public void onPasswordLogin() {
        Toast.makeText(this, R.string.signup_confirm_message, Toast.LENGTH_SHORT).show();
        Intent homeIntent = new Intent(this, HomeActivity.class);
        startActivity(homeIntent);
        finish();
    }

}
