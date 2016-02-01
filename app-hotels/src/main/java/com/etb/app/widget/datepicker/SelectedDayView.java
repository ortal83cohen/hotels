package com.etb.app.widget.datepicker;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.etb.app.R;

import butterknife.ButterKnife;

/**
 * @author alex
 * @date 2015-04-26
 */
public class SelectedDayView extends LinearLayout {
    TextView mDayView;
    TextView mMonthView;
    TextView mTitleView;

    public SelectedDayView(Context context) {
        this(context, null);
    }

    public SelectedDayView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SelectedDayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context);

        setClickable(true);
        setBackgroundResource(R.drawable.datepicker_selected_view);

        TypedArray ta = context.obtainStyledAttributes(attrs,
                R.styleable.SelectedDayView, defStyleAttr, 0);
        int n = ta.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = ta.getIndex(i);
            if (attr == R.styleable.SelectedDayView_android_text) {
                setTitle(ta.getText(attr));
                break;
            }
        }
        ta.recycle();
    }

    public void setTitle(CharSequence text) {
        mTitleView.setText(text);
    }

    public void setDate(int day, String month) {
        mDayView.setText(String.valueOf(day));
        mMonthView.setText(month);
    }

    private void initViews(final Context context) {
        setOrientation(VERTICAL);
        setBackgroundColor(getResources().getColor(R.color.white));
        View view = LayoutInflater.from(context).inflate(R.layout.view_datepicker_selected_day, this);

        mDayView = ButterKnife.findById(view, R.id.datepicker_selected_day);
        mMonthView = ButterKnife.findById(view, R.id.datepicker_selected_month);
        mTitleView = ButterKnife.findById(view, R.id.datepicker_selected_title);

    }

}
