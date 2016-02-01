package com.etb.app.randerscript;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * @author user
 * @date 2015-06-30
 */
public class BlurBuilder {
    private float BLUR_RADIUS;
    private Context mContext;

    public BlurBuilder(Context context) {
        mContext = context;
        BLUR_RADIUS = 7.5f;
    }

    public Bitmap blur(View v) {
        return blur(getScreenshot(v), 0);
    }

    public Bitmap blur(View v, int color) {
        BLUR_RADIUS = 10.0f;
        return blur(getScreenshot(v), color);
    }

    public Bitmap blur(Drawable drawable, int color, float blurRadius) {
        BLUR_RADIUS = blurRadius;
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        return blur(bitmapDrawable.getBitmap(), color);
    }

    public Bitmap blur(Bitmap bitmap, int color) {

        RenderScript rs = RenderScript.create(mContext);
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        Allocation allocation = Allocation.createFromBitmap(rs, bitmap);
        theIntrinsic.setRadius(BLUR_RADIUS);
        theIntrinsic.setInput(allocation);
        theIntrinsic.forEach(allocation);
        allocation.copyTo(bitmap);

        if (color != 0) {
                Paint paint = new Paint();
                ColorFilter filter = new PorterDuffColorFilter(mContext.getResources().getColor(color), PorterDuff.Mode.SRC_IN);
                paint.setColorFilter(filter);
                Canvas canvas = new Canvas(bitmap);
                canvas.drawBitmap(bitmap, 0, 0, paint);
        }
        return bitmap;
    }

    private Bitmap getScreenshot(View v) {
        Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.draw(c);
        return b;
    }


}