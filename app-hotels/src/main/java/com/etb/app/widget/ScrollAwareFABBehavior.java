package com.etb.app.widget;

import android.content.Context;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;

public class ScrollAwareFABBehavior extends FloatingActionButton.Behavior {
    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();
    private boolean mIsAnimating = false;
    private Handler mDelayedHandler;
    private FloatingActionButton mView;
    private Runnable mDelayedShowRunnable = new Runnable() {
        @Override
        public void run() {
            animateShow(mView);
        }
    };

    public ScrollAwareFABBehavior(Context context, AttributeSet attrs) {
        super();

    }

    @Override
    public boolean onStartNestedScroll(final CoordinatorLayout coordinatorLayout, final FloatingActionButton child,
                                       final View directTargetChild, final View target, final int nestedScrollAxes) {
        if (mView == null) {
            mView = child;
        }
        // Ensure we react to vertical scrolling
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL
                || super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
    }

    @Override
    public void onNestedScroll(final CoordinatorLayout coordinatorLayout, final FloatingActionButton child,
                               final View target, final int dxConsumed, final int dyConsumed,
                               final int dxUnconsumed, final int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        if (mIsAnimating) {
            return;
        }

        if (dyConsumed > 0 && child.getVisibility() == View.VISIBLE) {
            // User scrolled down and the FAB is currently visible -> hide the FAB
            cancelDelayed();
            animateHide(child);
        } else if (dyConsumed < 0 && child.getVisibility() != View.VISIBLE) {
            // User scrolled up and the FAB is currently not visible -> show the FAB
            cancelDelayed();
            animateShow(child);
        }
    }

    private void animateHide(final FloatingActionButton button) {
        mIsAnimating = true;
        createAnimationHide(button)
                .start();
    }

    private void animateShow(final View view) {
        mIsAnimating = true;
        view.setVisibility(View.VISIBLE);
        createAnimationShow(view).start();
    }

    private void startShowDelayed(final View view) {
        mDelayedHandler = new Handler();
        mDelayedHandler.postDelayed(mDelayedShowRunnable, 1000/* 1sec delay */);
    }

    private void cancelDelayed() {
        if (mDelayedHandler != null) {
            mDelayedHandler.removeCallbacks(mDelayedShowRunnable);
            mDelayedHandler = null;
        }
    }

    private ViewPropertyAnimatorCompat createAnimationHide(final FloatingActionButton button) {
        return ViewCompat.animate(button)
                .scaleX(0.0F).scaleY(0.0F).alpha(0.0F)
                .setInterpolator(INTERPOLATOR)
                .withLayer()
                .setListener(new ViewPropertyAnimatorListener() {
                    public void onAnimationStart(View view) {
                        mIsAnimating = true;
                    }

                    public void onAnimationCancel(View view) {
                        mIsAnimating = false;
                    }

                    public void onAnimationEnd(View view) {
                        mIsAnimating = false;
                        view.setVisibility(View.GONE);
                        startShowDelayed(view);
                    }
                });
    }

    private ViewPropertyAnimatorCompat createAnimationShow(final View View) {
        return ViewCompat.animate(View).scaleX(1.0F).scaleY(1.0F).alpha(1.0F)
                .setInterpolator(INTERPOLATOR)
                .withLayer()
                .setListener(new ViewPropertyAnimatorListener() {
                    public void onAnimationStart(View view) {
                        view.setVisibility(android.view.View.VISIBLE);
                        mIsAnimating = true;
                    }

                    public void onAnimationCancel(View view) {
                        mIsAnimating = false;
                    }

                    public void onAnimationEnd(View view) {
                        mIsAnimating = false;
                        cancelDelayed();
                    }
                });
    }
}