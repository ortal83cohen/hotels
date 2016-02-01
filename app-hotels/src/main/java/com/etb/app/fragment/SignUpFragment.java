package com.etb.app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.etb.app.App;
import com.etb.app.R;
import com.etb.app.member.MemberService;
import com.etb.app.member.model.AccessToken;
import com.etb.app.member.model.JoinRequest;
import com.etb.app.utils.AppLog;
import com.etb.app.utils.CountryCode;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * @author alex
 * @date 2015-08-12
 */
public class SignUpFragment extends LoginFragment implements Callback<AccessToken> {

    @Bind(R.id.password_confirm)
    EditText mPasswordConfirmView;

    public static SignUpFragment newInstance() {
        return new SignUpFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        ButterKnife.bind(this, view);

        setupViews();

        return view;
    }

    protected void onLoginButtonClick() {

        if (!mPasswordView.getText().toString().equals(mPasswordConfirmView.getText().toString())) {
            Toast.makeText(getActivity(), R.string.password_doesnt_match, Toast.LENGTH_SHORT).show();
            return;
        }

        JoinRequest request = new JoinRequest();
        request.email = mEmailView.getText().toString();
        request.password = mPasswordView.getText().toString();
        // Detect country code
        request.countryIso = CountryCode.detect(getActivity());

        MemberService memberService = App.provide(getActivity()).memberService();
        memberService.join(request).enqueue(this);
    }


    @Override
    public void onResponse(Response<AccessToken> response, Retrofit retrofit) {
        getActivity().finish();
        Toast.makeText(getActivity(), R.string.signup_message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailure(Throwable t) {
        AppLog.e(t);
        Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
