package com.etb.app.anim;

import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.etb.app.randerscript.BlurBuilder;

/**
 * @author user
 * @date 2015-07-01
 */
public class BlurAnimation {

    private static final long ANIM_DURATION = 700;

    public ValueAnimator blur(final View view, final Drawable original, float to) {

        ValueAnimator anim = ValueAnimator.ofFloat(0.1f, to);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float offset = (Float) valueAnimator.getAnimatedValue();
                if (offset % 3 != 0) {
                    final BlurBuilder blurBuilder = new BlurBuilder(view.getContext());
                    Bitmap image = blurBuilder.blur(original, 0, offset);
                    view.setBackground(new BitmapDrawable(view.getContext().getResources(), image));
                }
            }
        });
        anim.setDuration(ANIM_DURATION);
        anim.setInterpolator(new LinearInterpolator());
        return anim;
    }

}
