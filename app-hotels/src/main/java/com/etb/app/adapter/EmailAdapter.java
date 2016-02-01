package com.etb.app.adapter;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.util.Patterns;
import android.widget.ArrayAdapter;

import java.util.HashSet;
import java.util.regex.Pattern;

/**
 * @author alex
 * @date 2015-05-04
 */
public class EmailAdapter extends ArrayAdapter<String> {

    public EmailAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_1);

        Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        Account[] accounts = AccountManager.get(context).getAccounts();
        HashSet<String> set = new HashSet<>(accounts.length);
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                set.add(account.name);
            }
        }

        addAll(set);
    }
}
