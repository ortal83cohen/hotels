package com.etb.app.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.CheckBox;

import com.etb.app.R;

/**
 * @author alex
 * @date 2015-11-01
 */
public class TileCheckBox extends CheckBox {
    private final Rect mTempRect = new Rect();
    private Drawable mDrawable;
    private int mTileCount = 5;

    public TileCheckBox(Context context) {
        this(context, null);
    }

    public TileCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TileCheckBox);

        final Drawable d = a.getDrawable(R.styleable.TileCheckBox_tileDrawable);
        setDrawable(d);

        a.recycle();
    }

    public void setTileCount(int tileCount) {
        mTileCount = tileCount;
    }

    /**
     * Sets a drawable as the compound button image.
     *
     * @param drawable the drawable to set
     */
    public void setDrawable(Drawable drawable) {
        if (mDrawable != drawable) {
            if (mDrawable != null) {
                mDrawable.setCallback(null);
                unscheduleDrawable(mDrawable);

            }

            mDrawable = drawable.mutate();

            drawable.setCallback(this);
            //drawable.setLayouDirection(getLayoutDirection());
            if (drawable.isStateful()) {
                drawable.setState(getDrawableState());
            }
            drawable.setVisible(getVisibility() == VISIBLE, false);
            setMinHeight(drawable.getIntrinsicHeight());
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        canvas.save();

        final Drawable drawable = mDrawable;

        final int drawableHeight = drawable.getIntrinsicHeight();
        final int drawableWidth = drawable.getIntrinsicWidth() * mTileCount;

        drawable.setBounds(0, 0, drawableWidth, drawableHeight);

        canvas.translate((getWidth() - drawableWidth) / 2, (getHeight() - drawableHeight) / 2);

        drawable.draw(canvas);

        canvas.restore();
    }

    protected void drawableStateChanged() {
        super.drawableStateChanged();

        final Drawable drawable = mDrawable;
        if (drawable != null && drawable.isStateful()) {
            drawable.setState(getDrawableState());
            invalidate();
        }
    }
}
