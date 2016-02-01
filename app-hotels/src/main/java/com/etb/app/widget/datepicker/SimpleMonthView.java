/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.etb.app.widget.datepicker;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.os.Build;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.etb.app.R;
import com.etb.app.utils.FontUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * A calendar-like view displaying a specified month and the appropriate selectable day numbers
 * within the specified month.
 */
class SimpleMonthView extends View {
    private static final int DEFAULT_HEIGHT = 32;
    private static final int MIN_HEIGHT = 10;

    private static final int DEFAULT_SELECTED_DAY = -1;
    private static final int DEFAULT_WEEK_START = Calendar.SUNDAY;
    private static final int DEFAULT_NUM_DAYS = 7;
    private static final int DEFAULT_NUM_ROWS = 6;
    private static final int MAX_NUM_ROWS = 6;

    private static final int HIGHLIGHTED_CIRCLE_ALPHA = 60;

    private static final int DAY_SEPARATOR_WIDTH = 1;

    private final int mMiniDayNumberTextSize;
    private final int mMonthDayLabelTextSize;
    private final int mMonthHeaderSize;
    private final int mDaySelectedCircleSize;
    private final Calendar mCalendar = Calendar.getInstance();
    private final Calendar mDayLabelCalendar = Calendar.getInstance();
    /**
     * Single-letter (when available) formatter for the day of week label.
     */
    private SimpleDateFormat mDayFormatter = new SimpleDateFormat("EEEEE", Locale.getDefault());
    // affects the padding on the sides of this view
    private int mPadding = 0;
    private Paint mDayNumberPaint;
    private Paint mDayNumberDisabledPaint;
    private Paint mDayNumberSelectedPaint;
    private Paint mDayNumberHighlightedPaint;
    private Paint mMonthDayLabelPaint;
    private int mMonth;
    private int mYear;
    // Quick reference to the width of this view, matches parent
    private int mViewWidth;
    // The height this view should draw at in pixels, set by height param
    private int mRowHeight = DEFAULT_HEIGHT;
    // If this view contains the today
    private boolean mHasToday = false;
    // Which day is selected [0-6] or -1 if no day is selected
    private int mSelectedDayFrom = -1;
    private int mSelectedDayTo = -1;
    // Which day is today [0-6] or -1 if no day is today
    private int mToday = DEFAULT_SELECTED_DAY;
    // Which day of the week to start on [0-6]
    private int mWeekStart = DEFAULT_WEEK_START;
    // How many days to display
    private int mNumDays = DEFAULT_NUM_DAYS;
    // The number of days + a spot for week number if it is displayed
    private int mNumCells = mNumDays;
    private int mDayOfWeekStart = 0;
    // First enabled day
    private int mEnabledDayStart = 1;
    // Last enabled day
    private int mEnabledDayEnd = 31;
    private int mNumRows = DEFAULT_NUM_ROWS;

    // Optional listener for handling day click actions
    private OnDayClickListener mOnDayClickListener;

    // Whether to prevent setting the accessibility delegate
    private boolean mLockAccessibilityDelegate;

    private int mNormalTextColor;
    private int mDisabledTextColor;
    private int mSelectedTextColor;
    private int mSelectedDayColor;
    private int mWeekDayTextColor;
    private int mHiglightedDayColor;

    private int mHiglightDayFrom;
    private int mHiglightDayTo;
    private boolean mHasHighlighted;
    private float mRectCorner;

    public SimpleMonthView(Context context) {
        this(context, null);
    }

    public SimpleMonthView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.datePickerStyle);
    }

    public SimpleMonthView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final Resources res = context.getResources();

        mMiniDayNumberTextSize = res.getDimensionPixelSize(R.dimen.datepicker_day_number_size);
        mMonthDayLabelTextSize = res.getDimensionPixelSize(R.dimen.datepicker_month_day_label_text_size);
        mMonthHeaderSize = res.getDimensionPixelOffset(R.dimen.datepicker_month_list_item_header_height);
        mDaySelectedCircleSize = res.getDimensionPixelSize(R.dimen.datepicker_day_number_select_circle_radius);

        mRectCorner = res.getDimensionPixelSize(R.dimen.datepicker_round_corner);
        mViewWidth = res.getDimensionPixelOffset(R.dimen.datepicker_view_width);
        mRowHeight = (res.getDimensionPixelOffset(R.dimen.datepicker_view_height)
                - mMonthHeaderSize) / MAX_NUM_ROWS;

        // Set up accessibility components.
        setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_YES);
        mLockAccessibilityDelegate = true;

        mNormalTextColor = res.getColor(R.color.datepicker_normal_text_color);
        mDisabledTextColor = res.getColor(R.color.datepicker_disabled_text_color);
        mSelectedTextColor = res.getColor(R.color.datepicker_selected_text_color);

        mWeekDayTextColor = res.getColor(R.color.datepicker_normal_weekday_color);

        mSelectedDayColor = res.getColor(R.color.datepicker_selected_day_color);
        mHiglightedDayColor = res.getColor(R.color.datepicker_highlighted_day_color);
        // Sets up any standard paints that will be used1
        initView();
        if (isInEditMode()) {
            setMonthParams(8, 10, 8, 10, 5, 2015, 1, 1, 30);
        }
    }

    private static boolean isValidDayOfWeek(int day) {
        return day >= Calendar.SUNDAY && day <= Calendar.SATURDAY;
    }

    private static boolean isValidMonth(int month) {
        return month >= Calendar.JANUARY && month <= Calendar.DECEMBER;
    }

    private static int getDaysInMonth(int month, int year) {
        switch (month) {
            case Calendar.JANUARY:
            case Calendar.MARCH:
            case Calendar.MAY:
            case Calendar.JULY:
            case Calendar.AUGUST:
            case Calendar.OCTOBER:
            case Calendar.DECEMBER:
                return 31;
            case Calendar.APRIL:
            case Calendar.JUNE:
            case Calendar.SEPTEMBER:
            case Calendar.NOVEMBER:
                return 30;
            case Calendar.FEBRUARY:
                return (year % 4 == 0) ? 29 : 28;
            default:
                throw new IllegalArgumentException("Invalid Month");
        }
    }

    void setTextColor(ColorStateList colors) {
        final Resources res = getContext().getResources();

        mNormalTextColor = colors.getColorForState(ENABLED_STATE_SET, res.getColor(R.color.datepicker_normal_text_color));
        mDayNumberPaint.setColor(mNormalTextColor);

        mDisabledTextColor = colors.getColorForState(EMPTY_STATE_SET, res.getColor(R.color.datepicker_disabled_text_color));
        mDayNumberDisabledPaint.setColor(mDisabledTextColor);

        mSelectedTextColor = colors.getColorForState(ENABLED_SELECTED_STATE_SET, res.getColor(R.color.datepicker_selected_text_color));
    }

    void setWeekDayColor(int color) {
        mWeekDayTextColor = color;
        mMonthDayLabelPaint.setColor(mWeekDayTextColor);
    }

    void setDayColor(int selectedDayColor, int highlightedDayColor) {
        mSelectedDayColor = selectedDayColor;
        mDayNumberSelectedPaint.setColor(mSelectedDayColor);
        mHiglightedDayColor = highlightedDayColor;
        mDayNumberHighlightedPaint.setColor(mHiglightedDayColor);
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        mDayFormatter = new SimpleDateFormat("EEEEE", newConfig.locale);
    }

    @Override
    public void setAccessibilityDelegate(AccessibilityDelegate delegate) {
        // Workaround for a JB MR1 issue where accessibility delegates on
        // top-level ListView items are overwritten.
        if (!mLockAccessibilityDelegate) {
            super.setAccessibilityDelegate(delegate);
        }
    }

    public void setOnDayClickListener(OnDayClickListener listener) {
        mOnDayClickListener = listener;
    }

    @Override
    public boolean dispatchHoverEvent(MotionEvent event) {
        return super.dispatchHoverEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                final int day = getDayFromLocation(event.getX(), event.getY());
                if (day >= 0) {
                    onDayClick(day);
                }
                break;
        }
        return true;
    }

    /**
     * Sets up the text and style properties for painting.
     */
    private void initView() {

        // TODO: Extract font to configuration
        Typeface typeface = FontUtils.loadFontReqular(getContext());
        Typeface typefaceMedium = FontUtils.loadFontMedium(getContext());

        mMonthDayLabelPaint = new Paint();
        mMonthDayLabelPaint.setAntiAlias(true);
        mMonthDayLabelPaint.setColor(mWeekDayTextColor);
        mMonthDayLabelPaint.setTextSize(mMonthDayLabelTextSize);
        mMonthDayLabelPaint.setTypeface(typefaceMedium);
        mMonthDayLabelPaint.setTextAlign(Align.CENTER);
        mMonthDayLabelPaint.setStyle(Style.FILL);
        mMonthDayLabelPaint.setFakeBoldText(false);

        mDayNumberSelectedPaint = new Paint();
        mDayNumberSelectedPaint.setAntiAlias(true);
        mDayNumberSelectedPaint.setColor(mSelectedDayColor);
        mDayNumberSelectedPaint.setTextSize(mMiniDayNumberTextSize);
        mDayNumberSelectedPaint.setTextAlign(Align.CENTER);
        mDayNumberSelectedPaint.setTypeface(typeface);
        mDayNumberSelectedPaint.setStyle(Style.FILL);
        mDayNumberSelectedPaint.setFakeBoldText(false);

        mDayNumberHighlightedPaint = new Paint();
        mDayNumberHighlightedPaint.setAntiAlias(true);
        mDayNumberHighlightedPaint.setColor(mHiglightedDayColor);
        mDayNumberHighlightedPaint.setStyle(Style.FILL);

        mDayNumberPaint = new Paint();
        mDayNumberPaint.setAntiAlias(true);
        mDayNumberPaint.setColor(mNormalTextColor);
        mDayNumberPaint.setTextSize(mMiniDayNumberTextSize);
        mDayNumberPaint.setTextAlign(Align.CENTER);
        mDayNumberPaint.setTypeface(typeface);
        mDayNumberPaint.setStyle(Style.FILL);
        mDayNumberPaint.setFakeBoldText(false);

        mDayNumberDisabledPaint = new Paint();
        mDayNumberDisabledPaint.setAntiAlias(true);
        mDayNumberDisabledPaint.setColor(mDisabledTextColor);
        mDayNumberDisabledPaint.setTextSize(mMiniDayNumberTextSize);
        mDayNumberDisabledPaint.setTextAlign(Align.CENTER);
        mDayNumberDisabledPaint.setTypeface(typeface);
        mDayNumberDisabledPaint.setStyle(Style.FILL);
        mDayNumberDisabledPaint.setFakeBoldText(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawWeekDayLabels(canvas);
        drawDays(canvas);
    }

    /**
     * Sets all the parameters for displaying this week. Parameters have a default value and
     * will only update if a new value is included, except for focus month, which will always
     * default to no focus month if no value is passed in. The only required parameter is the
     * week start.
     *
     * @param selectedDayFrom the selected day of the month, or -1 for no selection.
     * @param selectedDayTo   the selected day of the month, or -1 for no selection.
     * @param month           the month.
     * @param year            the year.
     * @param weekStart       which day the week should start on. {@link Calendar#SUNDAY} through
     *                        {@link Calendar#SATURDAY}.
     * @param enabledDayStart the first enabled day.
     * @param enabledDayEnd   the last enabled day.
     */
    void setMonthParams(int selectedDayFrom, int selectedDayTo, int highlightDayFrom, int highlightDayTo, int month, int year, int weekStart, int enabledDayStart,
                        int enabledDayEnd) {
        if (mRowHeight < MIN_HEIGHT) {
            mRowHeight = MIN_HEIGHT;
        }

        mSelectedDayFrom = selectedDayFrom;
        mSelectedDayTo = selectedDayTo;
        mHiglightDayFrom = highlightDayFrom;
        mHiglightDayTo = highlightDayTo;

        mHasHighlighted = (highlightDayFrom != -1 && highlightDayTo != -1);


        if (isValidMonth(month)) {
            mMonth = month;
        }
        mYear = year;

        mCalendar.set(Calendar.MONTH, mMonth);
        mCalendar.set(Calendar.YEAR, mYear);
        mCalendar.set(Calendar.DAY_OF_MONTH, 1);
        mDayOfWeekStart = mCalendar.get(Calendar.DAY_OF_WEEK);

        if (isValidDayOfWeek(weekStart)) {
            mWeekStart = weekStart;
        } else {
            mWeekStart = mCalendar.getFirstDayOfWeek();
        }

        if (enabledDayStart > 0 && enabledDayEnd < 32) {
            mEnabledDayStart = enabledDayStart;
        }
        if (enabledDayEnd > 0 && enabledDayEnd < 32 && enabledDayEnd >= enabledDayStart) {
            mEnabledDayEnd = enabledDayEnd;
        }

        mNumCells = getDaysInMonth(mMonth, mYear);
        mNumRows = calculateNumRows();

        // Figure out what day today is
        initToday();
    }

    private void initToday() {
        final Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();
        mHasToday = false;
        mToday = -1;

        if (mYear != today.year || mMonth != today.month) {
            return;
        }

        for (int i = 0; i < mNumCells; i++) {
            final int day = i + 1;
            if (sameDay(day, today)) {
                mHasToday = true;
                mToday = day;
            }
        }
    }

    public void reuse() {
        mNumRows = DEFAULT_NUM_ROWS;
        requestLayout();
    }

    private int calculateNumRows() {
        int offset = findDayOffset();
        int dividend = (offset + mNumCells) / mNumDays;
        int remainder = (offset + mNumCells) % mNumDays;
        return (dividend + (remainder > 0 ? 1 : 0));
    }

    private boolean sameDay(int day, Time today) {
        return mYear == today.year &&
                mMonth == today.month &&
                day == today.monthDay;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int measuredHeight = mRowHeight * mNumRows + mMonthHeaderSize;
        int measuredWidth = mViewWidth;

        int viewWidth = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        if (viewWidth > mViewWidth) {
            mPadding = (viewWidth - mViewWidth) / 2;
            measuredWidth += (viewWidth - mViewWidth);
        }

        setMeasuredDimension(measuredWidth, measuredHeight);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // mViewWidth = w;
    }

    private void drawWeekDayLabels(Canvas canvas) {
        final int y = mMonthHeaderSize - (mMonthDayLabelTextSize / 2);
        final int dayWidthHalf = mViewWidth / (mNumDays * 2);

        String lang = Locale.getDefault().getLanguage();

        for (int i = 0; i < mNumDays; i++) {
            final int calendarDay = (i + mWeekStart) % mNumDays;

            final String dayLabel = renderDayLabel(calendarDay, lang);
            final int x = (2 * i + 1) * dayWidthHalf + mPadding;
            mMonthDayLabelPaint.setColor(Color.BLACK);
            canvas.drawText(dayLabel, x, y, mMonthDayLabelPaint);
        }
    }

    private String renderDayLabel(int calendarDay, String lang) {
        // For EN languages we want to show 2 letters dat of week
        // Formatter use one letter for short representations
        // For other locales fallback to formatter
        if ("en".equals(lang)) {
            switch (calendarDay) {
                case 0: // 0???
                case Calendar.SATURDAY:
                    return "SA";
                case Calendar.MONDAY:
                    return "MO";
                case Calendar.TUESDAY:
                    return "TU";
                case Calendar.WEDNESDAY:
                    return "WE";
                case Calendar.THURSDAY:
                    return "TH";
                case Calendar.FRIDAY:
                    return "FR";
                case Calendar.SUNDAY:
                    return "SU";
            }
        }

        mDayLabelCalendar.set(Calendar.DAY_OF_WEEK, calendarDay);
        return mDayFormatter.format(mDayLabelCalendar.getTime());
    }

    /**
     * Draws the month days.
     */
    private void drawDays(Canvas canvas) {
        int y = (((mRowHeight + mMiniDayNumberTextSize) / 2) - DAY_SEPARATOR_WIDTH)
                + mMonthHeaderSize;
        int dayWidthHalf = mViewWidth / (mNumDays * 2);
        int col = findDayOffset();

        int prevHighlihtedRightX = -1;

        for (int day = 1; day <= mNumCells; day++) {
            int x = (2 * col + 1) * dayWidthHalf + mPadding;
            boolean selected = false;
            if (mSelectedDayFrom == day || mSelectedDayTo == day) {
                selected = true;
                int cy = y - (mMiniDayNumberTextSize / 3);
                if (mHasHighlighted) {
                    // Draw highlight start bg
                    if (mSelectedDayFrom == day && mHiglightDayFrom == day) {
                        prevHighlihtedRightX = x + mDaySelectedCircleSize;
                        if (col < mNumDays - 1) { // not for the last column
                            drawRoundRectCompat(x, cy - mDaySelectedCircleSize, prevHighlihtedRightX, cy + mDaySelectedCircleSize, mDayNumberHighlightedPaint, canvas);
                        }
                        // Draw highlight end bg
                    } else if (mSelectedDayTo == day && mSelectedDayTo == day) {
                        // Span over previous cell
                        if (col > 0) { // not for the first column
                            int left = (prevHighlihtedRightX == -1) ? x - mDaySelectedCircleSize : prevHighlihtedRightX;
                            drawRoundRectCompat(left, cy - mDaySelectedCircleSize, x, cy + mDaySelectedCircleSize, mDayNumberHighlightedPaint, canvas);
                        }
                        prevHighlihtedRightX = -1;
                    }
                }

                drawRoundRectCompat(x - mDaySelectedCircleSize, cy - mDaySelectedCircleSize, x + mDaySelectedCircleSize, cy + mDaySelectedCircleSize, mDayNumberSelectedPaint, canvas);
            } else if (mHasHighlighted && (day >= mHiglightDayFrom && day <= mHiglightDayTo)) {
                int cy = y - (mMiniDayNumberTextSize / 3);
                if (col == 0) { // first day in column
                    prevHighlihtedRightX = x + mDaySelectedCircleSize;
                    drawRoundRectCompat(x - mDaySelectedCircleSize, cy - mDaySelectedCircleSize, prevHighlihtedRightX, cy + mDaySelectedCircleSize, mDayNumberHighlightedPaint, canvas);
                    // canvas.drawCircle(x, cy, mDaySelectedCircleSize, mDayNumberHighlightedPaint);
                } else if (col + 1 == mNumDays) { // last day in column
                    int left = (prevHighlihtedRightX == -1) ? x - mDaySelectedCircleSize : prevHighlihtedRightX;
                    prevHighlihtedRightX = -1;
                    drawRoundRectCompat(left, cy - mDaySelectedCircleSize, x + mDaySelectedCircleSize, cy + mDaySelectedCircleSize, mDayNumberHighlightedPaint, canvas);
                    // canvas.drawCircle(x, cy, mDaySelectedCircleSize, mDayNumberHighlightedPaint);
                } else {
                    // Span over previous cell
                    int left = (prevHighlihtedRightX == -1) ? x - mDaySelectedCircleSize : prevHighlihtedRightX;
                    prevHighlihtedRightX = x + mDaySelectedCircleSize;
                    drawRoundRectCompat(left, cy - mDaySelectedCircleSize, prevHighlihtedRightX, cy + mDaySelectedCircleSize, mDayNumberHighlightedPaint, canvas);
                }
            }

            if (selected) {
                mDayNumberSelectedPaint.setColor(mSelectedTextColor);
                canvas.drawText(String.format("%d", day), x, y, mDayNumberSelectedPaint);
                // Restore color
                mDayNumberSelectedPaint.setColor(mSelectedDayColor);
            } else {
                if (mHasToday && mToday == day) {
                    mDayNumberPaint.setColor(mSelectedDayColor);
                } else if (mHasHighlighted && (day >= mHiglightDayFrom && day <= mHiglightDayTo)) {
                    mDayNumberPaint.setColor(mSelectedDayColor);
                } else {
                    mDayNumberPaint.setColor(mNormalTextColor);
                }
                final Paint paint = (day < mEnabledDayStart || day > mEnabledDayEnd) ? mDayNumberDisabledPaint : mDayNumberPaint;
                canvas.drawText(String.format("%d", day), x, y, paint);
            }
            col++;
            if (col == mNumDays) {
                col = 0;
                y += mRowHeight;
            }
        }
    }

    private void drawRoundRectCompat(float left, float top, float right, float bottom, Paint paint, final Canvas canvas) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawRoundRect(left, top, right, bottom, mRectCorner, mRectCorner, paint);
        } else {
            canvas.drawRect(left, top, right, bottom, paint);
        }
    }

    private int findDayOffset() {
        return (mDayOfWeekStart < mWeekStart ? (mDayOfWeekStart + mNumDays) : mDayOfWeekStart)
                - mWeekStart;
    }

    /**
     * Calculates the day that the given x position is in, accounting for week
     * number. Returns the day or -1 if the position wasn't in a day.
     *
     * @param x The x position of the touch event
     * @return The day number, or -1 if the position wasn't in a day
     */
    private int getDayFromLocation(float x, float y) {
        int dayStart = mPadding;
        if (x < dayStart || x > mViewWidth + mPadding) {
            return -1;
        }
        // Selection is (x - start) / (pixels/day) == (x -s) * day / pixels
        int row = (int) (y - mMonthHeaderSize) / mRowHeight;
        int column = (int) ((x - dayStart) * mNumDays / mViewWidth);

        int day = column - findDayOffset() + 1;
        day += row * mNumDays;
        if (day < 1 || day > mNumCells) {
            return -1;
        }
        return day;
    }

    /**
     * Called when the user clicks on a day. Handles callbacks to the
     * {@link OnDayClickListener} if one is set.
     *
     * @param day The day that was clicked
     */
    private void onDayClick(int day) {
        if (mOnDayClickListener != null) {
            final boolean disabled = (day < mEnabledDayStart || day > mEnabledDayEnd);
            if (!disabled) {
                Calendar date = Calendar.getInstance();
                date.set(mYear, mMonth, day);
                mOnDayClickListener.onDayClick(this, date);
            }
        }
    }

    public int getViewWidth() {
        return mViewWidth;
    }


    /**
     * Handles callbacks when the user clicks on a time object.
     */
    public interface OnDayClickListener {
        void onDayClick(SimpleMonthView view, Calendar day);
    }
}