package com.etb.app.widget;

import android.content.Context;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easytobook.api.model.DateRange;
import com.etb.app.R;
import com.etb.app.utils.CalendarUtils;
import com.etb.app.widget.datepicker.DayPickerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Locale;

import butterknife.ButterKnife;

/**
 * @author alex
 * @date 2015-04-22
 */
public abstract class BaseDatePicker extends FrameLayout implements View.OnClickListener, DayPickerView.OnChangeListener {

    protected final Locale mCurrentLocale;
    protected final DateRange mCurrentRange;
    protected final Context mContext;
    protected Calendar mCurrentMonthDate;
    protected SimpleDateFormat mYearFormat;
    protected SimpleDateFormat mMonthFormat;
    protected OnRangeChangedListener mRangeChangedListener;
    LinearLayout mMonthAndYearLayout;
    TextView mHeaderMonthTextView;
    TextView mHeaderYearTextView;
    DayPickerView mDayPickerView;
    View mArrowLeft;
    View mArrowRight;

    public BaseDatePicker(Context context, AttributeSet attrs, int defStyleAttr, @LayoutRes int layout) {
        super(context, attrs, defStyleAttr);
        mContext = context;

        View view = LayoutInflater.from(context).inflate(layout, this);
        injectViews(view);

        mCurrentLocale = Locale.getDefault();
        mCurrentMonthDate = Calendar.getInstance(mCurrentLocale);
        mCurrentRange = DateRange.getInstance();

        mYearFormat = new SimpleDateFormat("y", Locale.getDefault()); // 2015
        String pattern = (isInEditMode()) ? "MMMM" : "LLLL";
        mMonthFormat = new SimpleDateFormat(pattern, Locale.getDefault()); // Stand Alone Jan

        mDayPickerView.setFirstDayOfWeek(mCurrentMonthDate.getFirstDayOfWeek());

        Calendar range = Calendar.getInstance(mCurrentLocale);
        long minMillis = range.getTimeInMillis();
        range.add(Calendar.YEAR, 1);
        range.add(Calendar.DAY_OF_MONTH, -1);
        long maxMillis = range.getTimeInMillis();

        mDayPickerView.setMinMaxRange(minMillis, maxMillis);

        mDayPickerView.goTo(mCurrentRange.from.getTimeInMillis());
        mDayPickerView.setOnChangeListener(this);

        mHeaderYearTextView.setOnClickListener(this);
        mArrowLeft.setOnClickListener(this);
        mArrowRight.setOnClickListener(this);

        setupViews();

        updateDisplay();
    }

    public void setOnRangeChangedListener(OnRangeChangedListener listener) {
        mRangeChangedListener = listener;
    }

    protected void injectViews(View view) {
        mMonthAndYearLayout = ButterKnife.findById(view, R.id.datepicker_month_year_layout);
        mHeaderMonthTextView = ButterKnife.findById(view, R.id.datepicker_month);
        mHeaderYearTextView = ButterKnife.findById(view, R.id.datepicker_year);
        mDayPickerView = ButterKnife.findById(view, R.id.datepicker_day);
        mArrowLeft = ButterKnife.findById(view, R.id.datepicker_arrow_left);
        mArrowRight = ButterKnife.findById(view, R.id.datepicker_arrow_right);
    }

    public void goTo(Calendar date) {
        mDayPickerView.goTo(date.getTimeInMillis());
        mCurrentMonthDate = date;
        updateDisplay();
    }

    private void setupViews() {
        // Compute indices of Month, Day and Year views
        boolean beggnsWithYear = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            final String bestDateTimePattern = DateFormat.getBestDateTimePattern(mCurrentLocale, "yMMMd");
            final int[] viewIndices = CalendarUtils.getMonthDayYearIndexes(bestDateTimePattern);
            beggnsWithYear = viewIndices[CalendarUtils.YEAR_INDEX] == 0;
        }

        // Position Day and Month views within the MonthAndDay view.
        mMonthAndYearLayout.removeAllViews();
        if (beggnsWithYear) {
            mMonthAndYearLayout.addView(mHeaderYearTextView);
            mMonthAndYearLayout.addView(mHeaderMonthTextView);
        } else {
            mMonthAndYearLayout.addView(mHeaderMonthTextView);
            mMonthAndYearLayout.addView(mHeaderYearTextView);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.datepicker_arrow_left) {
            mDayPickerView.showPrevMonth();
        } else if (v.getId() == R.id.datepicker_arrow_right) {
            mDayPickerView.showNextMonth();
        }
    }

    @Override
    public void onDaySelected(DayPickerView view, Calendar day) {
        int state = mCurrentRange.getState();
        if (state == DateRange.RANGE_SET_TO) {
            TimeIgnoringComparator timeIgnoringComparator = new TimeIgnoringComparator();
            if (timeIgnoringComparator.compare(day, mCurrentRange.from) <= 0) {
                return;
            }
        }
        mCurrentRange.setDay(day);
        mDayPickerView.setSelectedRange(mCurrentRange);
        updateDisplay();
        if (mRangeChangedListener != null) {
            mRangeChangedListener.onRangeChanged(state, mCurrentRange);
        }


    }

    @Override
    public void onMonthChanged(DayPickerView view, Calendar date) {
        mCurrentMonthDate = date;
        updateDisplay();

    }

    public DateRange getSelectedRange() {
        return mCurrentRange;
    }

    public void setSelectedRange(DateRange dateRange) {
        mCurrentRange.set(dateRange);
        mDayPickerView.setSelectedRange(mCurrentRange);
        updateDisplay();
    }

    protected void updateDisplay() {
        mHeaderMonthTextView.setText(mMonthFormat.format(mCurrentMonthDate.getTime()));
        mHeaderYearTextView.setText(mYearFormat.format(mCurrentMonthDate.getTime()));
    }

    public interface OnRangeChangedListener {
        void onRangeChanged(int state, DateRange range);
    }

    public class TimeIgnoringComparator implements Comparator<Calendar> {
        public int compare(Calendar c1, Calendar c2) {
            if (c1.get(Calendar.YEAR) != c2.get(Calendar.YEAR))
                return c1.get(Calendar.YEAR) - c2.get(Calendar.YEAR);
            if (c1.get(Calendar.MONTH) != c2.get(Calendar.MONTH))
                return c1.get(Calendar.MONTH) - c2.get(Calendar.MONTH);
            return c1.get(Calendar.DAY_OF_MONTH) - c2.get(Calendar.DAY_OF_MONTH);
        }
    }

}
