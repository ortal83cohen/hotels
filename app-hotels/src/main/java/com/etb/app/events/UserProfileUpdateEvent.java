package com.etb.app.events;

import com.etb.app.member.model.User;

/**
 * @author alex
 * @date 2015-08-17
 */
public class UserProfileUpdateEvent {
    private final User mUser;

    public UserProfileUpdateEvent(User user) {
        mUser = user;
    }

    public User getUser() {
        return mUser;
    }
}
