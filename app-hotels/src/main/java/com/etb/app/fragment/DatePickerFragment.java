package com.etb.app.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.easytobook.api.model.DateRange;
import com.etb.app.App;
import com.etb.app.R;
import com.etb.app.widget.StayDatePicker;

import butterknife.ButterKnife;

/**
 * @author alex
 * @date 2015-04-22
 */
public class DatePickerFragment extends BaseFragment {
    Button mSelectButton;
    StayDatePicker mDatePicker;

    public static DatePickerFragment newInstance() {
        return new DatePickerFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_datepicker, container, false);
        injectViews(view);

        mDatePicker.setSelectedRange(getHotelsRequest().getDateRange());

        mSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentActivity activity = getActivity();

                DateRange range = mDatePicker.getSelectedRange();
                getHotelsRequest().getDateRange().set(range);
                if (activity instanceof Listener) {
                    ((Listener) activity).onDateRangeSelected(range);
                }
                activity.getSupportFragmentManager().popBackStack();
            }
        });

        return view;
    }

    private void injectViews(View view) {
        mSelectButton = ButterKnife.findById(view, R.id.date_select_button);
        mDatePicker = ButterKnife.findById(view, R.id.stay_datepicker);
    }

    public interface Listener {
        void onDateRangeSelected(DateRange range);
    }


}
