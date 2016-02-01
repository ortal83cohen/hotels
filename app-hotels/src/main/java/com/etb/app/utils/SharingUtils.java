package com.etb.app.utils;

import android.content.Context;
import android.content.Intent;

/**
 * @author user
 * @date 2015-11-24
 */
public class SharingUtils {

    public static void shareText(Context context, String subject, String shareBody) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        context.startActivity(Intent.createChooser(sharingIntent, "Shared via www.easytobook.com"));
    }
}
