package com.etb.app.widget.datepicker;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.easytobook.api.model.DateRange;
import com.etb.app.utils.CalendarUtils;
import com.etb.app.utils.MathUtils;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * @author alex
 * @date 2015-11-17
 */
public class DayPickerPagerAdapter extends PagerAdapter {
    private static final int DEFAULT_START_YEAR = 2015;
    private static final int DEFAULT_END_YEAR = 2030;

    private final SimpleMonthView.OnDayClickListener mDayClickListener;
    private final DateRange mMinMaxRange = DateRange.getInstance();
    private final ArrayList<ViewHolder> mCachedViews = new ArrayList<ViewHolder>();
    private final SparseArray<ViewHolder> mActiveViews = new SparseArray<ViewHolder>();
    private Context mContext;
    private DateRange mSelectedRange = DateRange.getInstance();
    private int mFirstDayOfWeek;
    private Calendar mTempCalendar;
    private ColorStateList mCalendarTextColors;
    private int mWeekDayTextColor = -1;
    private int mSelectedDayColor = -1;
    private int mHiglightedDayColor = -1;


    public DayPickerPagerAdapter(Context context, com.etb.app.widget.datepicker.SimpleMonthView.OnDayClickListener dayClickListener) {
        mContext = context;
        mDayClickListener = dayClickListener;
        mMinMaxRange.from.set(DEFAULT_START_YEAR, Calendar.JANUARY, 1);
        mMinMaxRange.to.set(DEFAULT_END_YEAR, Calendar.DECEMBER, 31);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        if (mCachedViews.size() > 0) {
            ViewHolder vh = mCachedViews.get(0);
            mCachedViews.remove(0);
            container.addView(vh.itemView);
            mActiveViews.put(position, vh);
            onBindViewHolder(vh, position);
            return vh;
        }

        SimpleMonthView v = new SimpleMonthView(mContext);

        // Set up the new view
        v.setClickable(true);
        v.setOnDayClickListener(mDayClickListener);

        if (mCalendarTextColors != null) {
            v.setTextColor(mCalendarTextColors);
        }
        if (mWeekDayTextColor != -1) {
            v.setWeekDayColor(mWeekDayTextColor);
        }
        if (mSelectedDayColor != -1) {
            v.setDayColor(mSelectedDayColor, mHiglightedDayColor);
        }
        ViewHolder vh = new ViewHolder(v);
        container.addView(v);
        mActiveViews.put(position, vh);
        onBindViewHolder(vh, position);

        return vh;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ViewHolder vh = (ViewHolder) object;
        container.removeView(vh.itemView);
        mActiveViews.remove(position);
        mCachedViews.add(vh);
    }


    public void onBindViewHolder(ViewHolder holder, int position) {
        SimpleMonthView v = (SimpleMonthView) holder.itemView;

        final int minMonth = mMinMaxRange.from.get(Calendar.MONTH);
        final int minYear = mMinMaxRange.from.get(Calendar.YEAR);
        final int currentMonth = position + minMonth;
        final int month = currentMonth % 12;
        final int year = currentMonth / 12 + minYear;
        final int compareFrom = CalendarUtils.compareDayInMonth(mSelectedRange.from, year, month);
        final int compareTo = CalendarUtils.compareDayInMonth(mSelectedRange.to, year, month);

        final int selectedDayFrom;
        final int selectedDayTo;
        final int highlihtedDayFrom;
        final int highlihtedDayTo;

        // TODO: Highlight correct range
        if (compareFrom == 0 && compareTo == 0) {
            selectedDayFrom = mSelectedRange.from.get(Calendar.DAY_OF_MONTH);
            selectedDayTo = mSelectedRange.to.get(Calendar.DAY_OF_MONTH);
            highlihtedDayFrom = selectedDayFrom;
            highlihtedDayTo = selectedDayTo;
        } else if (compareFrom == 0 && compareTo == 1) {
            selectedDayFrom = mSelectedRange.from.get(Calendar.DAY_OF_MONTH);
            selectedDayTo = -1;
            highlihtedDayFrom = selectedDayFrom;
            highlihtedDayTo = mSelectedRange.from.getActualMaximum(Calendar.DAY_OF_MONTH);
        } else if (compareTo == 0 && compareFrom == -1) {
            selectedDayTo = mSelectedRange.to.get(Calendar.DAY_OF_MONTH);
            selectedDayFrom = -1;
            highlihtedDayFrom = 1;
            highlihtedDayTo = selectedDayTo;
        } else if (compareFrom == -1 && compareTo == 1) {
            selectedDayTo = -1;
            selectedDayFrom = -1;
            highlihtedDayFrom = 1;
            highlihtedDayTo = mSelectedRange.from.getActualMaximum(Calendar.DAY_OF_MONTH);
        } else {
            selectedDayTo = -1;
            selectedDayFrom = -1;
            highlihtedDayFrom = -1;
            highlihtedDayTo = -1;
        }


        // Invokes requestLayout() to ensure that the recycled view is set with the appropriate
        // height/number of weeks before being displayed.
        v.reuse();

        final int enabledDayRangeStart;
        if (minMonth == month && minYear == year) {
            enabledDayRangeStart = mMinMaxRange.from.get(Calendar.DAY_OF_MONTH);
        } else {
            enabledDayRangeStart = 1;
        }

        final int enabledDayRangeEnd;
        if (mMinMaxRange.to.get(Calendar.MONTH) == month && mMinMaxRange.to.get(Calendar.YEAR) == year) {
            enabledDayRangeEnd = mMinMaxRange.to.get(Calendar.DAY_OF_MONTH);
        } else {
            enabledDayRangeEnd = 31;
        }


        v.setMonthParams(
                selectedDayFrom, selectedDayTo,
                highlihtedDayFrom, highlihtedDayTo,
                month, year, mFirstDayOfWeek,
                enabledDayRangeStart, enabledDayRangeEnd
        );
        v.invalidate();
    }

    @Override
    public int getCount() {
        final int diffYear = mMinMaxRange.to.get(Calendar.YEAR) - mMinMaxRange.from.get(Calendar.YEAR);
        final int diffMonth = mMinMaxRange.to.get(Calendar.MONTH) - mMinMaxRange.from.get(Calendar.MONTH);
        return diffMonth + 12 * diffYear + 1;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((ViewHolder) object).itemView;
    }

    public Calendar getDateOnPosition(int position) {
        final int minMonth = mMinMaxRange.from.get(Calendar.MONTH);
        final int minYear = mMinMaxRange.from.get(Calendar.YEAR);
        final int currentMonth = position + minMonth;
        final int month = currentMonth % 12;
        final int year = currentMonth / 12 + minYear;
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, 1);
        return cal;
    }

    public void setCalendarColors(ColorStateList textColors, int weekday, int selectedDay, int highlightedDay) {
        mCalendarTextColors = textColors;
        mWeekDayTextColor = weekday;
        mSelectedDayColor = selectedDay;
        mHiglightedDayColor = highlightedDay;
    }

    public void setMinRange(long minMillis) {
        mMinMaxRange.from.setTimeInMillis(minMillis);
    }

    public void setMinMaxRange(long minMillis, long maxMillis) {
        mMinMaxRange.from.setTimeInMillis(minMillis);
        mMinMaxRange.to.setTimeInMillis(maxMillis);
    }

    public void setFirstDayOfWeek(int firstDayOfWeek) {
        mFirstDayOfWeek = firstDayOfWeek;
    }

    public int getPositionFromDay(long timeInMillis) {
        final int diffMonthMax = getDiffMonths(mMinMaxRange.from, mMinMaxRange.to);
        final int diffMonth = getDiffMonths(mMinMaxRange.from, getTempCalendarForTime(timeInMillis));
        return MathUtils.constrain(diffMonth, 0, diffMonthMax);
    }

    private int getDiffMonths(Calendar start, Calendar end) {
        final int diffYears = end.get(Calendar.YEAR) - start.get(Calendar.YEAR);
        return end.get(Calendar.MONTH) - start.get(Calendar.MONTH) + 12 * diffYears;
    }

    private Calendar getTempCalendarForTime(long timeInMillis) {
        if (mTempCalendar == null) {
            mTempCalendar = Calendar.getInstance();
        }
        mTempCalendar.setTimeInMillis(timeInMillis);
        return mTempCalendar;
    }

    public void setSelectedRange(DateRange range) {
        mSelectedRange.set(range);
        notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {

        for (int i = 0; i < mActiveViews.size(); i++) {
            ViewHolder vh = mActiveViews.valueAt(i);
            int pos = mActiveViews.keyAt(i);
            onBindViewHolder(vh, pos);
        }

        super.notifyDataSetChanged();


    }

    static class ViewHolder {
        public View itemView;

        public ViewHolder(View itemView) {
            this.itemView = itemView;
        }
    }
}
