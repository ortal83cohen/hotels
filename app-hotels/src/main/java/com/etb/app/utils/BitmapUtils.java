package com.etb.app.utils;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;

/**
 * @author alex
 * @date 2015-04-19
 */
public class BitmapUtils {

    static final PorterDuff.Mode DEFAULT_TINT_MODE = PorterDuff.Mode.SRC_IN;

    /**
     * Sets the text size for a Paint object so a given string of text will be a
     * given width.
     *
     * @param paint        the Paint to set the text size for
     * @param desiredWidth the desired width
     * @param text         the text that should be that width
     */
    public static void setTextSizeForWidth(Paint paint, float desiredWidth,
                                           String text) {

        // Pick a reasonably large value for the test. Larger values produce
        // more accurate results, but may cause problems with hardware
        // acceleration. But there are workarounds for that, too; refer to
        // http://stackoverflow.com/questions/6253528/font-size-too-large-to-fit-in-cache
        final float testTextSize = 48f;

        // Get the bounds of the text, using our testTextSize.
        paint.setTextSize(testTextSize);
        Rect bounds = new Rect();
        // TODO: Marker min text length
        if (text.length() < 4) {
            int maxLen = 4;
            // Right pad with 00
            text = String.format("%1$-" + maxLen + "s", text).replace(" ", "0");
            paint.getTextBounds(text, 0, maxLen, bounds);
        } else {
            paint.getTextBounds(text, 0, text.length(), bounds);
        }


        // Calculate the desired size as a proportion of our testTextSize.
        float desiredTextSize = testTextSize * desiredWidth / bounds.width();

        // Set the paint for that size.
        paint.setTextSize(desiredTextSize);
    }

    /**
     * setTint ported from v21 for older version
     */
    public static PorterDuffColorFilter createTintFilter(BitmapDrawable drawable,
                                                         ColorStateList tint) {
        if (tint == null) {
            return null;
        }

        final int color = tint.getColorForState(drawable.getState(), Color.TRANSPARENT);
        return new PorterDuffColorFilter(color, DEFAULT_TINT_MODE);
    }
}
