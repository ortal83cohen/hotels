package com.etb.app.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;

import com.etb.app.App;
import com.etb.app.R;
import com.etb.app.activity.MemberActivity;
import com.etb.app.events.Events;
import com.etb.app.events.UserProfileUpdateEvent;
import com.etb.app.jobs.ProfileUpdateService;
import com.etb.app.member.model.Profile;
import com.etb.app.member.model.User;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author alex
 * @date 2015-08-17
 */
public class MemberProfileEditFragment extends BaseFragment implements View.OnClickListener {

    @Bind(R.id.profile_name)
    EditText mFirstName;
    @Bind(R.id.profile_surname)
    EditText mLastName;

    @Bind(R.id.profile_birthdate)
    Button mDateOfBirth;

    @Bind(R.id.gender_female)
    RadioButton mGenderFemale;
    @Bind(R.id.gender_male)
    RadioButton mGenderMale;
    @Bind(R.id.gender_other)
    RadioButton mGenderOther;

    @Bind(R.id.btn_save)
    Button mSaveButton;

    private User mUser;


    public static MemberProfileEditFragment newInstance() {
        return new MemberProfileEditFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_member_profile_edit, container, false);
        ButterKnife.bind(this, view);

        mUser = App.provide(getActivity()).memberStorage().loadUser();
        mFirstName.setText(mUser.profile.firstName);
        mLastName.setText(mUser.profile.lastName);

        if (mUser.profile.birthDate.equals("0000-00-00 00:00:00")) {
            mDateOfBirth.setText("0000-00-00");
        } else {
            mDateOfBirth.setText(mUser.profile.birthDate);
        }
        mDateOfBirth.setOnClickListener(this);

        mGenderFemale.setOnClickListener(this);
        mGenderMale.setOnClickListener(this);
        mGenderOther.setOnClickListener(this);

        if (mUser.profile.gender == Profile.GENDER_MALE) {
            mGenderMale.setChecked(true);
        } else if (mUser.profile.gender == Profile.GENDER_FEMALE) {
            mGenderFemale.setChecked(true);
        } else if (mUser.profile.gender == Profile.GENDER_OTHER) {
            mGenderOther.setChecked(true);
        }

        mSaveButton.setOnClickListener(this);
        return view;
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.profile_birthdate) {
            DialogFragment newFragment = new DatePickerFragment();
            Bundle args = new Bundle();
            args.putString("date", mDateOfBirth.getText().toString());
            newFragment.setArguments(args);
            newFragment.show(getChildFragmentManager(), "datePicker");
        } else if (v.getId() == R.id.btn_save) {
            save();
        }
    }

    private void save() {
        mUser.profile.firstName = mFirstName.getText().toString();
        mUser.profile.lastName = mLastName.getText().toString();

        if (mGenderMale.isChecked()) {
            mUser.profile.gender = Profile.GENDER_MALE;
        } else if (mGenderFemale.isChecked()) {
            mUser.profile.gender = Profile.GENDER_FEMALE;
        } else if (mGenderOther.isChecked()) {
            mUser.profile.gender = Profile.GENDER_OTHER;
        }

        mUser.profile.birthDate = mDateOfBirth.getText().toString();

        App.provide(getActivity()).memberStorage().saveUser(mUser);
        Events.post(new UserProfileUpdateEvent(mUser));

        ProfileUpdateService.schedule(getActivity());

        ((MemberActivity) getActivity()).remove(this);
    }


    public void onDateSet(int year, int month, int day) {
        month = month + 1;
        mDateOfBirth.setText(new StringBuilder().append(year).append("-").append((month < 10) ? "0" : "").append(month).append("-").append((day < 10) ? "0" : "").append(day).toString());
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            String date = getArguments().getString("date", "0000-00-00");

            int year, month, day;
            if ("0000-00-00".equals(date)) {

                year = c.get(Calendar.YEAR) - 25;
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
            } else {
                String[] parts = date.split("-");

                year = Integer.valueOf(parts[0]);
                month = Integer.valueOf(parts[1]) - 1;
                day = Integer.valueOf(parts[2]);
            }
            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            ((MemberProfileEditFragment) getParentFragment()).onDateSet(year, month, day);
        }
    }

}
