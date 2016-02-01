package com.etb.app.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;

import com.etb.app.R;
import com.etb.app.anim.ResizeAnimator;

public class ScrollAwareViewBehavior extends CoordinatorLayout.Behavior<View> {
    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();
    private boolean mIsAnimating = false;
    private int mViewHeight = -1;
    private View mView;
    private Handler mDelayedHandler;
    private Runnable mDelayedShowRunnable = new Runnable() {
        @Override
        public void run() {
            animateShow(mView);
        }
    };

    public ScrollAwareViewBehavior(Context context, AttributeSet attrs) {
        super();
    }

    @Override
    public boolean onStartNestedScroll(final CoordinatorLayout coordinatorLayout, final View child,
                                       final View directTargetChild, final View target, final int nestedScrollAxes) {
        if (mView == null) {
            mView = child.findViewById(R.id.panel_top); // TODO: extract to attribute
        }
        if (mView == null) {
            return false;
        }
        // Ensure we react to vertical scrolling
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL
                || super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
    }

    @Override
    public void onNestedScroll(final CoordinatorLayout coordinatorLayout, final View child,
                               final View target, final int dxConsumed, final int dyConsumed,
                               final int dxUnconsumed, final int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        if (mIsAnimating) {
            return;
        }
        if (dyConsumed > 0 && mView.getVisibility() == View.VISIBLE) {
            // User scrolled down and the FAB is currently visible -> hide the FAB
            cancelDelayed();
            animateHide(mView);
        } else if (dyConsumed < 0 && mView.getVisibility() != View.VISIBLE) {
            // User scrolled up and the FAB is currently not visible -> show the FAB
            cancelDelayed();
            animateShow(mView);
        }
    }

    private void cancelDelayed() {
        if (mDelayedHandler != null) {
            mDelayedHandler.removeCallbacks(mDelayedShowRunnable);
            mDelayedHandler = null;
        }
    }

    private void startShowDelayed(final View view) {
        mDelayedHandler = new Handler();
        mDelayedHandler.postDelayed(mDelayedShowRunnable, 1000/* 1sec delay */);
    }

    private void animateHide(final View view) {

        if (mViewHeight == -1) {
            mViewHeight = view.getMeasuredHeight();
        }

        mIsAnimating = true;
        ValueAnimator anim = createAnimationHide(view);
        anim.start();
    }


    private void animateShow(final View view) {
        mIsAnimating = true;
        view.setVisibility(View.VISIBLE);
        ValueAnimator anim = createAnimationShow(view);
        anim.start();
    }

    private ValueAnimator createAnimationHide(final View view) {
        ValueAnimator anim = ResizeAnimator.height(mViewHeight, 0, view, 300);
        anim.setInterpolator(INTERPOLATOR);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mIsAnimating = true;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mIsAnimating = false;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mIsAnimating = false;
                view.setVisibility(View.GONE);
                startShowDelayed(view);
            }
        });
        return anim;
    }


    private ValueAnimator createAnimationShow(final View view) {
        ValueAnimator anim = ResizeAnimator.height(0, mViewHeight, view, 300);
        anim.setInterpolator(INTERPOLATOR);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                view.setVisibility(View.VISIBLE);
                mIsAnimating = true;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mIsAnimating = false;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mIsAnimating = false;
                cancelDelayed();
            }
        });
        return anim;
    }
}