package com.etb.app.drawable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;

/**
 * @author user
 * @date 2015-06-21
 */
public class TriangleDrawable extends Drawable {
    private final int mColor;
    private final Path mPath;
    private Paint mPaint;
    private float mFromDegrees = 0;

    public TriangleDrawable(Context context, @ColorRes int colorRes) {
        super();
        mPaint = new Paint();

        //R.color.theme_primary
        mColor = context.getResources().getColor(colorRes);

        setBounds(0, 0, 35, 50);

        Rect bounds = getBounds();

        mPath = new Path();
        mPath.moveTo(bounds.left, bounds.bottom / 4);
        mPath.lineTo(bounds.right, bounds.bottom / 4);
        mPath.lineTo(bounds.left + (bounds.right - bounds.left) / 2, bounds.bottom / 4 * 3);
        mPath.lineTo(bounds.left, bounds.bottom / 4);
    }

    @Override
    public int getOpacity() {
        return 1;
    }

    @Override
    public void draw(Canvas canvas) {
        final Rect bounds = getBounds();
        final int w = bounds.right - bounds.left;
        final int h = bounds.bottom - bounds.top;

        final int saveCount = canvas.save();
        canvas.rotate(mFromDegrees, bounds.left, bounds.top);

        mPaint.setColor(mColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawPath(mPath, mPaint);

        canvas.restoreToCount(saveCount);
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter cf) {

    }

    public void setFromDegrees(float fromDegrees) {
        mFromDegrees = fromDegrees;
    }
}
