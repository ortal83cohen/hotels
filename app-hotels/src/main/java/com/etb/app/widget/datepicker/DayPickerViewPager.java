package com.etb.app.widget.datepicker;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

import com.easytobook.api.model.DateRange;
import com.etb.app.R;

import java.util.Calendar;

/**
 * @author alex
 * @date 2015-11-17
 */
public class DayPickerViewPager extends ViewPager implements DayPickerView {
    private final Context mContext;
    private DayPickerPagerAdapter mAdapter;
    private OnChangeListener mChangeListener;
    private final OnPageChangeListener mPageChangeListener = new OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            Calendar date = mAdapter.getDateOnPosition(position);
            if (mChangeListener != null) {
                mChangeListener.onMonthChanged(DayPickerViewPager.this, date);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    public DayPickerViewPager(Context context) {
        this(context, null);
    }

    public DayPickerViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;

        final TypedArray array = context.obtainStyledAttributes(attrs,
                R.styleable.DatePickerView, 0,
                R.style.HotelsAppTheme_TextAppearance_Datepicker); // see below
        ColorStateList textColors = array.getColorStateList(R.styleable.DatePickerView_calendarTextColor);
        int weekday = array.getColor(R.styleable.DatePickerView_calendarWeekdayColor, Color.BLACK);
        int selectedDay = array.getColor(R.styleable.DatePickerView_calendarSelectedDayColor, Color.DKGRAY);
        int highlightedDay = array.getColor(R.styleable.DatePickerView_calendarHighlightedDayColor, Color.LTGRAY);
        array.recycle();

        // specify an adapter (see also next example)
        mAdapter = new DayPickerPagerAdapter(mContext, this);
        mAdapter.setCalendarColors(textColors, weekday, selectedDay, highlightedDay);
        addOnPageChangeListener(mPageChangeListener);
        setAdapter(mAdapter);
    }

    public void showNextMonth() {
        int pos = getCurrentItem();
        if (pos + 1 < mAdapter.getCount()) {
            setCurrentItem(pos + 1, true);
        }
    }

    public void showPrevMonth() {
        int pos = getCurrentItem();
        if (pos > 0) {
            setCurrentItem(pos - 1, true);
        }
    }

    public void setFirstDayOfWeek(int firstDayOfWeek) {
        mAdapter.setFirstDayOfWeek(firstDayOfWeek);
    }

    public void setMinRange(long min) {
        mAdapter.setMinRange(min);
    }

    public void setMinMaxRange(long minMillis, long maxMillis) {
        mAdapter.setMinMaxRange(minMillis, maxMillis);
    }

    public void setSelectedRange(DateRange range) {
        mAdapter.setSelectedRange(range);
    }

    public void goTo(long dayMillis) {
        final int position = mAdapter.getPositionFromDay(dayMillis);
        setCurrentItem(position);
    }

    @Override
    public void setOnChangeListener(OnChangeListener listener) {
        mChangeListener = listener;
    }

    @Override
    public void onDayClick(SimpleMonthView view, Calendar day) {
        if (mChangeListener != null) {
            mChangeListener.onDaySelected(this, day);
        }
    }
}
