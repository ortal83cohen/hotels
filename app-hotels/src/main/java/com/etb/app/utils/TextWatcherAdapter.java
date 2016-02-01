package com.etb.app.utils;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * @author alex
 * @date 2015-06-09
 */
public abstract class TextWatcherAdapter implements TextWatcher {

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
