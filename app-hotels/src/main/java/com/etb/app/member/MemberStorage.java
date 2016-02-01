package com.etb.app.member;

import android.content.Context;
import android.content.SharedPreferences;

import com.etb.app.member.model.AccessToken;
import com.etb.app.member.model.Profile;
import com.etb.app.member.model.User;

/**
 * @author alex
 * @date 2015-08-06
 */
public class MemberStorage {
    private static final String FILE_NAME = "member";
    private final SharedPreferences mPrefs;

    public MemberStorage(Context context) {
        mPrefs = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    }

    public User loadUser() {
        String email = mPrefs.getString("email", null);
        if (email == null) {
            return null;
        }

        User user = new User();
        user.email = email;
        user.id = mPrefs.getInt("id", 0);
        user.profile = new Profile();
        user.profile.firstName = mPrefs.getString("first_name", null);
        user.profile.lastName = mPrefs.getString("last_name", null);
        user.profile.birthDate = mPrefs.getString("birth_date", null);
        user.profile.countryIso = mPrefs.getString("country_iso", null);
        user.profile.city = mPrefs.getString("city", null);
        user.profile.gender = mPrefs.getInt("gender", 0);
        user.profile.imageUrl = mPrefs.getString("image_url", null);
        user.profile.phone = mPrefs.getString("phone", null);

        return user;
    }

    public void saveUser(User user) {
        SharedPreferences.Editor edit = mPrefs.edit();

        edit.putString("email", user.email);
        edit.putInt("id", user.id);
        edit.putString("first_name", user.profile.firstName);
        edit.putString("last_name", user.profile.lastName);
        edit.putString("birth_date", user.profile.birthDate);
        edit.putString("country_iso", user.profile.countryIso);
        edit.putString("city", user.profile.city);
        edit.putInt("gender", user.profile.gender);
        edit.putString("image_url", user.profile.imageUrl);
        edit.putString("phone", user.profile.phone);

        edit.apply();
    }

    public String loadAccessToken() {
        return mPrefs.getString("access_token", null);
    }

    public void saveAccessToken(AccessToken accessToken) {
        SharedPreferences.Editor edit = mPrefs.edit();
        edit.putString("access_token", accessToken.token);
        edit.apply();
    }

    public void clear() {
        SharedPreferences.Editor edit = mPrefs.edit();
        edit.clear();
        edit.apply();
    }
}
