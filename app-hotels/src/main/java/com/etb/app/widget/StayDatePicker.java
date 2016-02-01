package com.etb.app.widget;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.util.AttributeSet;
import android.view.View;

import com.easytobook.api.model.DateRange;
import com.etb.app.R;
import com.etb.app.widget.datepicker.SelectedDayView;

import java.util.Calendar;
import java.util.Locale;

import butterknife.ButterKnife;

/**
 * @author alex
 * @date 2015-04-22
 */
public class StayDatePicker extends com.etb.app.widget.BaseDatePicker {
    SelectedDayView mArrivalView;
    SelectedDayView mDepartureView;

    public StayDatePicker(Context context) {
        this(context, null);
    }

    public StayDatePicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StayDatePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, R.layout.view_datepicker);
    }

    public StayDatePicker(Context context, AttributeSet attrs, int defStyleAttr, @LayoutRes int layout) {
        super(context, attrs, defStyleAttr, layout);

        mArrivalView.setOnClickListener(this);
        mDepartureView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.datepicker_selected_arrival) {
            mCurrentRange.setState(DateRange.RANGE_SET_FROM);
            updateStateViews(DateRange.RANGE_SET_FROM);
        } else if (v.getId() == R.id.datepicker_selected_departure) {
            mCurrentRange.setState(DateRange.RANGE_SET_TO);
            updateStateViews(DateRange.RANGE_SET_TO);
        } else {
            super.onClick(v);
        }
    }

    @Override
    protected void injectViews(View view) {
        super.injectViews(view);
        mArrivalView = ButterKnife.findById(view, R.id.datepicker_selected_arrival);
        mDepartureView = ButterKnife.findById(view, R.id.datepicker_selected_departure);
    }

    @Override
    protected void updateDisplay() {
        super.updateDisplay();
        mArrivalView.setDate(mCurrentRange.from.get(Calendar.DAY_OF_MONTH), mCurrentRange.from.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()));
        mDepartureView.setDate(mCurrentRange.to.get(Calendar.DAY_OF_MONTH), mCurrentRange.to.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()));
        updateStateViews(mCurrentRange.getState());

    }

    public void updateStateViews(int state) {
        mArrivalView.setSelected(state == DateRange.RANGE_SET_FROM);
        mDepartureView.setSelected(state == DateRange.RANGE_SET_TO);
    }
}
