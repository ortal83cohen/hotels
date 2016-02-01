package com.etb.app.fragment;

import android.content.res.Resources;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.etb.app.App;
import com.etb.app.R;
import com.etb.app.adapter.EmailAdapter;
import com.etb.app.member.MemberStorage;
import com.etb.app.member.model.User;
import com.etb.app.model.PersonalInfoPage;

import java.security.InvalidParameterException;

import butterknife.ButterKnife;

/**
 * @author alex
 * @date 2015-04-28
 */
public class PersonalInfoFragment extends PageFragment {
    EditText mFirstNameView;
    EditText mLastNameView;
    AutoCompleteTextView mEmailView;
    EditText mPhoneNumber;

    public static PersonalInfoFragment newInstance() {
        return new PersonalInfoFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_booking_personal, container, false);
        injectViews(view);

        mEmailView.setAdapter(new EmailAdapter(getActivity()));
        Bundle pageData = null;
        if (mPage != null) {
            pageData = mPage.getData();
            if (pageData.getString(PersonalInfoPage.DATA_EMAIL) != null || pageData.getString(PersonalInfoPage.DATA_FIRST_NAME) != null) {
                mEmailView.setText(pageData.getString(PersonalInfoPage.DATA_EMAIL));
                mFirstNameView.setText(pageData.getString(PersonalInfoPage.DATA_FIRST_NAME));
                mLastNameView.setText(pageData.getString(PersonalInfoPage.DATA_LAST_NAME));
                mPhoneNumber.setText(pageData.getString(PersonalInfoPage.DATA_PHONE));
                mPhoneNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
            } else {
                MemberStorage memberStorage = App.provide(getActivity()).memberStorage();
                User user = memberStorage.loadUser();
                if (user != null) {
                    mEmailView.setText(user.email);
                    mFirstNameView.setText(user.profile.firstName);
                    mLastNameView.setText(user.profile.lastName);
                    mPhoneNumber.setText(user.profile.phone);
                }
            }
        }

        return view;
    }

    private void validate() {
        Resources r = getActivity().getResources();
        if (mFirstNameView.length() == 0) {
            mFirstNameView.requestFocus();
            mFirstNameView.setError("First name" +r.getString(R.string.is_required));
            throw new InvalidParameterException(r.getString(R.string.all_fields_must_be_filled));
        }
        if (mLastNameView.length() == 0) {
            mLastNameView.requestFocus();
            mLastNameView.setError("Last name" +r.getString(R.string.is_required));
            throw new InvalidParameterException(r.getString(R.string.all_fields_must_be_filled));
        }
        if (mEmailView.length() == 0) {
            mEmailView.requestFocus();
            mEmailView.setError("Email address" +r.getString(R.string.is_required));
            throw new InvalidParameterException(r.getString(R.string.all_fields_must_be_filled));
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(mEmailView.getText()).matches() ) {
            mEmailView.requestFocus();
            mEmailView.setError("Email address" + r.getString(R.string.must_be_valid_email_format));
            throw new InvalidParameterException(r.getString(R.string.all_fields_must_be_filled));
        }
        if (mPhoneNumber.length() == 0) {
            mPhoneNumber.requestFocus();
            mPhoneNumber.setError("Phone number" +r.getString(R.string.is_required));
            throw new InvalidParameterException(r.getString(R.string.all_fields_must_be_filled));
        }

    }

    private void injectViews(View view) {
        mEmailView = ButterKnife.findById(view, R.id.email);
        mFirstNameView = ButterKnife.findById(view, R.id.first_name);
        mLastNameView = ButterKnife.findById(view, R.id.last_name);
        mPhoneNumber = ButterKnife.findById(view, R.id.phone_number);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public String getPageKey() {
        return PersonalInfoPage.KEY;
    }

    @Override
    protected void onPageDataSave(Bundle pageData) {
        validate();
        pageData.putString(PersonalInfoPage.DATA_EMAIL, mEmailView.getText().toString());
        pageData.putString(PersonalInfoPage.DATA_FIRST_NAME, mFirstNameView.getText().toString());
        pageData.putString(PersonalInfoPage.DATA_LAST_NAME, mLastNameView.getText().toString());
        pageData.putString(PersonalInfoPage.DATA_PHONE, mPhoneNumber.getText().toString());
    }

}
