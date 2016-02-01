package com.etb.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.etb.app.R;
import com.etb.app.activity.LoginActivity;
import com.etb.app.adapter.EmailAdapter;
import com.etb.app.login.FacebookLogin;
import com.etb.app.login.PasswordLogin;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author alex
 * @date 2015-08-11
 */
public class LoginFragment extends BaseFragment implements FacebookLogin.Listener, View.OnClickListener, PasswordLogin.Listener {
    protected FacebookLogin mFacebookLogin;
    @Bind(R.id.btn_login_facebook)
    Button mFacebookLoginButton;
    @Bind(R.id.btn_login_google)
    Button mGoogleLoginButton;
    @Bind(R.id.email)
    AutoCompleteTextView mEmailView;
    @Bind(R.id.password)
    EditText mPasswordView;
    @Bind(R.id.login_button)
    Button mLoginButton;
    private PasswordLogin mPasswordLogin;


    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);

        setupViews();

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mPasswordLogin = new PasswordLogin(getActivity(), this);

    }

    protected void setupViews() {
        mEmailView.setAdapter(new EmailAdapter(getActivity()));

        // Callback registration
        mFacebookLogin = new FacebookLogin(this, mFacebookLoginButton, this);
        mGoogleLoginButton.setOnClickListener(((LoginActivity) getActivity()).getGooglePlusLogin());
        mLoginButton.setOnClickListener(this);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFacebookLogin.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.login_button) {
            onLoginButtonClick();
        }
    }

    protected void onLoginButtonClick() {
        mPasswordLogin.login(mEmailView.getText().toString(), mPasswordView.getText().toString());
    }


    @Override
    public void onFacebookLogin(String email, String firstName, String lastName) {

    }


    @Override
    public void onPasswordLogin() {
        getActivity().finish();
    }
}
