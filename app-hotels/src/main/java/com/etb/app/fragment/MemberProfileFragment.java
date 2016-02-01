package com.etb.app.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.etb.app.App;
import com.etb.app.R;
import com.etb.app.activity.MemberActivity;
import com.etb.app.adapter.CountryAdapter;
import com.etb.app.events.Events;
import com.etb.app.events.UserLogOutEvent;
import com.etb.app.events.UserProfileUpdateEvent;
import com.etb.app.member.MemberService;
import com.etb.app.member.model.User;
import com.etb.app.utils.AppLog;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * @author alex
 * @date 2015-08-11
 */
public class MemberProfileFragment extends BaseFragment implements View.OnClickListener {
    @Bind(R.id.profile_image)
    ImageView mProfileImage;

    @Bind(R.id.profile_name)
    TextView mFirstName;
    @Bind(R.id.profile_surname)
    TextView mLastName;
    @Bind(R.id.email)
    TextView mEmail;
    @Bind(R.id.profile_birthdate)
    TextView mBirthDate;

    @Bind(R.id.profile_country)
    AutoCompleteTextView mCountry;
    @Bind(R.id.phone_number)
    EditText mPhoneNumber;

    @Bind(R.id.btn_change_password)
    Button mChangePasswordButton;
    @Bind(R.id.btn_logout)
    Button mLogoutButton;

    @Bind(R.id.btn_profile_edit)
    ImageButton mEditButton;

    public static MemberProfileFragment newInstance() {
        return new MemberProfileFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Events.register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Events.unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_member_profile, container, false);
        ButterKnife.bind(this, view);

        User user = App.provide(getActivity()).memberStorage().loadUser();
        mFirstName.setText(user.profile.firstName);
        mLastName.setText(user.profile.lastName);
        mBirthDate.setText(user.profile.birthDate);

        mEmail.setText(user.email);

        CountryAdapter countryAdapter = new CountryAdapter(getActivity());
        mCountry.setAdapter(countryAdapter);
        mCountry.setText(countryAdapter.getCountryName(user.profile.countryIso));
        mPhoneNumber.setText(user.profile.phone);

        mChangePasswordButton.setOnClickListener(this);
        mLogoutButton.setOnClickListener(this);
        mEditButton.setOnClickListener(this);

        if (!TextUtils.isEmpty(user.profile.imageUrl)) {
            Uri uri = Uri.parse(user.profile.imageUrl);

            Picasso.with(getActivity()).load(uri)
                    .resize(mProfileImage.getMeasuredWidth(), mProfileImage.getMeasuredHeight())
                    .centerCrop()
                    .into(mProfileImage);
        }

        return view;
    }

    @Subscribe
    public void onUserProfileUpdate(UserProfileUpdateEvent event) {
        User user = event.getUser();
        mFirstName.setText(user.profile.firstName);
        mLastName.setText(user.profile.lastName);
        mBirthDate.setText(user.profile.birthDate);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_logout) {
            logout();
        } else if (id == R.id.btn_profile_edit) {
            ((MemberActivity) getActivity()).showProfileEdit();
        }
    }

    private void logout() {
        MemberService service = App.provide(getActivity()).memberService();
        Call<Void> call = service.logout();
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Response<Void> response, Retrofit retrofit) {

                if (response.isSuccess()) {
                    App.provide(getActivity()).memberStorage().clear();
                    Events.post(new UserLogOutEvent());
                    Toast.makeText(getActivity(), R.string.you_logged_out, Toast.LENGTH_SHORT).show();
                } else {
// TODO:
//                    ErrorResponse body = (ErrorResponse) error.getBodyAs(ErrorResponse.class);
//                    if (body.isInvalidGrant()) {
//                        App.provide(getActivity()).memberStorage().clear();
//                        Events.post(new UserLogOutEvent());
//                        Toast.makeText(getActivity(), R.string.you_logged_out, Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
                }


            }

            @Override
            public void onFailure(Throwable t) {
                AppLog.e(t);
            }
        });

    }

    private class MyClass implements Picasso.Listener {
        @Override
        public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
            // Display the exception
        }
    }

}
