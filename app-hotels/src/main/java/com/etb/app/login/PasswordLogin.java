package com.etb.app.login;

import android.content.Context;
import android.widget.Toast;

import com.etb.app.App;
import com.etb.app.R;
import com.etb.app.etbapi.RetrofitCallback;
import com.etb.app.events.Events;
import com.etb.app.events.UserLoginEvent;
import com.etb.app.member.MemberService;
import com.etb.app.member.MemberStorage;
import com.etb.app.member.model.AccessToken;
import com.etb.app.member.model.User;
import com.squareup.okhttp.ResponseBody;

import retrofit.Callback;
import retrofit.Response;

/**
 * @author alex
 * @date 2015-08-13
 */
public class PasswordLogin {
    private final Context mContext;
    private final Listener mListener;
    protected MemberService mMemberService;
    protected MemberStorage mMemberStorage;


    private Callback<User> mProfileCallback = new RetrofitCallback<User>() {
        @Override
        protected void failure(ResponseBody response, boolean isOffline) {
            Toast.makeText(mContext, R.string.profile_error, Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void success(User user, Response<User> response) {
            mMemberStorage.saveUser(user);
            Events.post(new UserLoginEvent(user));
            mListener.onPasswordLogin();
        }
    };

    private Callback<AccessToken> mLoginCallback = new RetrofitCallback<AccessToken>() {
        @Override
        protected void failure(ResponseBody response, boolean isOffline) {
            Toast.makeText(mContext, R.string.login_error, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void success(AccessToken accessToken, Response response) {
            onAccessToken(accessToken);
        }

    };

    public PasswordLogin(Context context, Listener listener) {
        mContext = context;
        mListener = listener;
        mMemberStorage = App.provide(context).memberStorage();
        mMemberService = App.provide(context).memberService();
    }

    public void login(String email, String password) {
        mMemberService.login(email, password).enqueue(mLoginCallback);
    }

    public void onAccessToken(AccessToken accessToken) {
        mMemberStorage.saveAccessToken(accessToken);
        mMemberService.profile().enqueue(mProfileCallback);
    }

    public interface Listener {
        void onPasswordLogin();
    }

}
