package com.etb.app.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Parcelable;

import com.etb.app.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author alex
 * @date 2015-08-13
 */
public class BrowserUtils {

    public static void sendToWebBrowser(final Uri uri, Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
        String packageNameOfAppToHide = context.getPackageName();
        ArrayList<Intent> targetIntents = new ArrayList<>();
        for (ResolveInfo currentInfo : activities) {
            String packageName = currentInfo.activityInfo.packageName;
            if (!packageNameOfAppToHide.equals(packageName)) {
                Intent targetIntent = new Intent(Intent.ACTION_VIEW);
                targetIntent.setData(uri);
                targetIntent.setPackage(packageName);
                targetIntents.add(targetIntent);
            }
        }
        if (targetIntents.size() > 0) {
            Intent chooserIntent = Intent.createChooser(targetIntents.remove(0), context.getString(R.string.open_with));
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetIntents.toArray(new Parcelable[]{}));
            context.startActivity(chooserIntent);
        }
    }
}
