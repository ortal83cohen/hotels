package com.etb.app.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appyvet.rangebar.RangeBar;
import com.easytobook.api.model.SearchRequest;
import com.etb.app.HotelsApplication;
import com.etb.app.R;
import com.etb.app.activity.PoisFiltersActivity;
import com.etb.app.map.PoiMarker;
import com.etb.app.widget.CheckBoxGroup;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;


public class PoisFilterFragment extends BaseFragment implements RangeBar.OnRangeBarChangeListener, CheckBoxGroup.OnCheckedChangeListener {

    @Bind(R.id.pois_types_list)
    GridLayout mPoisTypesList;
    @Bind(R.id.apply)
    Button mApplyButton;
    @Bind(R.id.poi_message)
    TextView mPoiMassage;
    HashMap<Integer, Integer> mTypes;

    private Listener mListener;

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pois_filter, container, false);
        ButterKnife.bind(this, view);

        mTypes = ((PoisFiltersActivity) getActivity()).getTypes();
        final boolean[] selectedAccTypes = ((PoisFiltersActivity) getActivity()).getFilters();

        mPoisTypesList.post(new Runnable() {
            @Override
            public void run() {
                addPoisTypesCheckboxes(selectedAccTypes);
            }
        });

        mApplyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onFiltersApply(selectedAccTypes);
            }
        });

        return view;
    }

    private void addPoisTypesCheckboxes(final boolean[] selectedAccTypes) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        if (mTypes != null) {
            mPoiMassage.setVisibility(View.GONE);
            for (final Map.Entry<Integer, Integer> entry : mTypes.entrySet()) {
                View view = inflater.inflate(R.layout.results_landmark_filter_item, mPoisTypesList, false);
                final TextView title = (TextView) view.findViewById(android.R.id.title);
                final TextView amount = (TextView) view.findViewById(R.id.amount);
                LinearLayout frame = (LinearLayout) view.findViewById(R.id.frame);
                frame.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!selectedAccTypes[entry.getKey()]) {
                            colorSelected(title, amount, entry.getKey());
                            selectedAccTypes[entry.getKey()] = true;
                        } else {
                            colorUnSelected(title, amount, entry.getKey());
                            selectedAccTypes[entry.getKey()] = false;
                        }
                    }
                });
                title.setText(PoiMarker.getNameByType(entry.getKey()));
                amount.setText(String.valueOf(entry.getValue()));
                if (selectedAccTypes[entry.getKey()]) {
                    colorSelected(title, amount, entry.getKey());
                } else {
                    colorUnSelected(title, amount, entry.getKey());
                }
                mPoisTypesList.addView(view);
            }
        } else {
            mPoiMassage.setVisibility(View.VISIBLE);
        }
    }

    private void colorSelected(final TextView title, final TextView amount, int key) {
        title.setCompoundDrawablesWithIntrinsicBounds(PoiMarker.getSelectedImageByType(key), 0, R.drawable.check_mark_green, 0);
        amount.setTextColor(getResources().getColor(R.color.theme_accent));
//                        title.setTextColor(getResources().getColor(R.color.theme_accent));
    }

    private void colorUnSelected(final TextView title, final TextView amount, int key) {
        title.setCompoundDrawablesWithIntrinsicBounds(PoiMarker.getImageByType(key), 0, 0, 0);
//                       title.setTextColor(Color.BLACK);
        amount.setTextColor(getResources().getColor(R.color.theme_accent_2));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (Listener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCheckedChanged(CheckBoxGroup group, SparseBooleanArray checked) {
        SearchRequest request = getHotelsRequest();
        if (group.getId() == R.id.group_stars) {
            request.getFilter().setStars(checked);
        } else if (group.getId() == R.id.group_ratings) {
            request.getFilter().setRating(checked);
        }
    }

    @Override
    public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {
        int priceFrom = Integer.valueOf(leftPinValue);
        int priceTo = Integer.valueOf(rightPinValue);

        SearchRequest request = getHotelsRequest();

        if (rangeBar.getTickEnd() == priceTo) {
            request.getFilter().setMaxRate(0);
        } else {
            request.getFilter().setMaxRate(priceTo);
        }
        if (rangeBar.getTickStart() == priceFrom) {
            request.getFilter().setMinRate(0);
        } else {
            request.getFilter().setMinRate(priceFrom);
        }
    }


    public interface Listener {
        void onFiltersApply(boolean[] selectedAccTypes);
    }
}
