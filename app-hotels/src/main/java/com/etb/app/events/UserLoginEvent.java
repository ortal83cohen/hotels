package com.etb.app.events;

import com.etb.app.member.model.User;

/**
 * @author alex
 * @date 2015-08-11
 */
public class UserLoginEvent {
    private final User mUser;

    public UserLoginEvent(User user) {
        mUser = user;
    }

    public User getUser() {
        return mUser;
    }
}
