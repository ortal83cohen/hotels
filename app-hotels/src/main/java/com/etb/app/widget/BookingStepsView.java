package com.etb.app.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.etb.app.R;
import com.etb.app.utils.FontUtils;

public class BookingStepsView extends View {

    private final Paint mStepPaint;
    private final Paint mStrokePaint;

    private final int mOneDpInPx;
    private final RectF mTempRectF = new RectF();
    private final Rect mTmpBounds = new Rect();
    private final int mTextRadius;
    private int mColorHightlight;
    private int mColorActive;
    private int mColorNotActive;
    private int mCurrentPage;
    private int mPageCount = 3;

    public BookingStepsView(Context context) {
        this(context, null, 0);
    }

    public BookingStepsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BookingStepsView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        final Resources res = getResources();

        mColorActive = Color.RED;
        int mColorBackground = getBackground() instanceof ColorDrawable ? ((ColorDrawable) getBackground()).getColor() : Color.WHITE;
        mColorNotActive = Color.DKGRAY;
        mColorHightlight = Color.GREEN;

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BookingStepsView, defStyle, 0);
        int n = ta.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = ta.getIndex(i);
            if (attr == R.styleable.BookingStepsView_bookingStepColorActive) {
                mColorActive = ta.getColor(attr, mColorActive);
            } else if (attr == R.styleable.BookingStepsView_bookingStepColorNotActive) {
                mColorNotActive = ta.getColor(attr, mColorNotActive);
            } else if (attr == R.styleable.BookingStepsView_bookingStepColorHighlight) {
                mColorHightlight = ta.getColor(attr, mColorHightlight);
            }
        }

        ta.recycle();

        Paint tmpPaint = new Paint();
        tmpPaint.setColor(Color.RED);

        Paint mTabPaint = new Paint();
        mTabPaint.setColor(mColorBackground);

        Paint mNextTabPaint = new Paint();
        mNextTabPaint.setColor(mColorBackground);

        Typeface typeface = FontUtils.loadFontBold(getContext());
        int labelSize = res.getDimensionPixelSize(R.dimen.hotels_booking_pager_label_size);

        mOneDpInPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());

        mStepPaint = new Paint();
        mStepPaint.setAntiAlias(true);
        mStepPaint.setColor(Color.BLACK);
        mStepPaint.setTextSize(labelSize);
        mStepPaint.setTypeface(typeface);
        mStepPaint.setTextAlign(Paint.Align.CENTER);
        mStepPaint.setStyle(Paint.Style.FILL);

        mStrokePaint = new Paint();
        mStrokePaint.setAntiAlias(true);
        mStrokePaint.setColor(Color.BLACK);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setStrokeWidth(mOneDpInPx);

        mTextRadius = res.getDimensionPixelSize(R.dimen.hotels_booking_pager_tab_radius);

        mStepPaint.getTextBounds("9", 0, 1, mTmpBounds);
        int mTabWidth = mTextRadius;
        int mTabHeight = mTextRadius;
    }

    public void setOnPageSelectedListener(OnPageSelectedListener onPageSelectedListener) {
    }

    public void setCurrentPage(int currentPage) {
        mCurrentPage = currentPage;
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mPageCount == 0) {
            return;
        }

        float totalWidth = getMeasuredWidth();
        float totalLeft = getPaddingLeft() - mTextRadius;
        float availWidth = (totalWidth - (getPaddingLeft() + getPaddingRight()));
        int spaceCount = (mPageCount - 1);
        float tabSpacing = (availWidth - (mPageCount * mTextRadius)) / spaceCount;

        mTempRectF.top = mOneDpInPx + getPaddingTop();
        mTempRectF.bottom = getHeight();
        mTempRectF.left = totalLeft;
        mTempRectF.right = totalWidth;

        //canvas.drawRect(mTempRectF, tmpPaint);

        float prevCircleRight = -1;
        for (int i = 0; i < mPageCount; i++) {
            float tabWidth = mTextRadius;

            mTempRectF.left = totalLeft + (i * (tabWidth + tabSpacing));
            mTempRectF.right = mTempRectF.left + tabWidth;

            drawTab(i, prevCircleRight, mTempRectF, canvas);
            prevCircleRight = mTempRectF.left + mTextRadius + mTextRadius;
        }
    }

    protected void drawTab(int idx, float prevLabelRight, RectF rect, Canvas canvas) {

        float cx = rect.left + mTextRadius;
        float cy = rect.top + mTextRadius;

        int labelColor;
        int lineColor;
        if (idx > mCurrentPage) {
            labelColor = mColorNotActive;
            lineColor = mColorNotActive;
        } else if (idx == mCurrentPage) {
            labelColor = mColorActive;
            lineColor = mColorHightlight;
        } else {
            labelColor = mColorHightlight;
            lineColor = mColorHightlight;
        }

        if (prevLabelRight != -1) {
            mStrokePaint.setColor(lineColor);
            canvas.drawLine(prevLabelRight, cy, rect.left, cy, mStrokePaint);
        }

        //  mStepPaint.setColor(mColorBackground);
        //  canvas.drawCircle(cx, cy, mTextRadius, mStepPaint);

        String stepNum = String.format("%d", idx + 1);

        mStepPaint.getTextBounds(stepNum, 0, stepNum.length(), mTmpBounds);
        mStepPaint.setColor(labelColor);
        canvas.drawText(stepNum, cx, cy + (mTmpBounds.height() / 2), mStepPaint);


    }
//
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        setMeasuredDimension(
//                View.resolveSize(
//                        mPageCount * (mTabWidth + mTabSpacing) - mTabSpacing
//                                + getPaddingLeft() + getPaddingRight(),
//                        widthMeasureSpec),
//                View.resolveSize(
//                        mTabHeight
//                                + getPaddingTop() + getPaddingBottom(),
//                        heightMeasureSpec));
//    }

    public int getPageCount() {
        return mPageCount;
    }

    public void setPageCount(int count) {
        mPageCount = count;
        invalidate();
    }

    public interface OnPageSelectedListener {
        void onPageStripSelected(int position);
    }

}