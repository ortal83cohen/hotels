package com.etb.app.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.TextView;

import com.appyvet.rangebar.RangeBar;
import com.easytobook.api.contract.AccType;
import com.easytobook.api.contract.Facility;
import com.easytobook.api.model.SearchRequest;
import com.easytobook.api.model.search.Filter;
import com.etb.app.R;
import com.etb.app.activity.HotelListFiltersActivity;
import com.etb.app.utils.AppLog;
import com.etb.app.utils.PriceRender;
import com.etb.app.widget.CheckBoxGroup;

import butterknife.Bind;
import butterknife.BindDimen;
import butterknife.ButterKnife;

/**
 * @author alex
 * @date 2015-05-25
 */
public class HotelListFilterFragment extends BaseFragment implements RangeBar.OnRangeBarChangeListener, CheckBoxGroup.OnCheckedChangeListener {

    @Bind(R.id.acctypes_list)
    GridLayout mAccTypesList;
    @Bind(R.id.facilities_list)
    GridLayout mFacilitiesList;
    @Bind(R.id.apply)
    Button mApplyButton;
    @Bind(R.id.price_rangebar)
    RangeBar mPriceRangeBar;
    @Bind(R.id.price_range)
    TextView mPriceText;
    @Bind(R.id.group_stars)
    CheckBoxGroup mStarsGroup;
    @Bind(R.id.group_ratings)
    CheckBoxGroup mRatingsGroup;

    @BindDimen(R.dimen.one_dp)
    int mColumnMargin;

    private int[] mAccTypesTitles = new int[]{
            R.string.acctype_hotel,
            R.string.acctype_hostel,
            R.string.acctype_b_and_b,
            R.string.acctype_apartment,
            R.string.acctype_residence,
            R.string.acctype_guesthouse,
    };

    private int[] mAccTypes = new int[]{
            AccType.HOTEL,
            AccType.HOSTEL,
            AccType.BED_AND_BREAKFAST,
            AccType.APARTMENT,
            AccType.RESIDENCE,
            AccType.GUESTHOUSE
    };

    private int[] mFacilitiesTitles = new int[]{
            R.string.facility_disabled,
            R.string.facility_internet,
            R.string.facility_parking,
            R.string.facility_pets_allowed,
            R.string.facility_child_discount,
            R.string.facility_swimming_pool,
            R.string.facility_air_condition,
            R.string.facility_fitness,
            R.string.facility_non_smoking
    };

    private int[] mFacilities = new int[]{
            Facility.Main.DISABLED,
            Facility.Main.INTERNET,
            Facility.Main.PARKING,
            Facility.Main.PETS_ALLOWED,
            Facility.Main.CHILD_DISCOUNTS,
            Facility.Main.SWIMMING_POOL,
            Facility.Main.AIR_CONDITIONING,
            Facility.Main.FITNESS_CENTER,
            Facility.Main.NON_SMOKING
    };

    private Listener mListener;
    private CompoundButton.OnCheckedChangeListener mFacilitiesChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            SearchRequest request = getHotelsRequest();
            int facility = (int) buttonView.getTag();
            if (isChecked) {
                request.getFilter().addMainFacility(facility);
            } else {
                request.getFilter().removeMainFacility(facility);
            }
        }
    };
    private CompoundButton.OnCheckedChangeListener mAccTypeChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            SearchRequest request = getHotelsRequest();
            int accType = (int) buttonView.getTag();
            if (isChecked) {
                request.getFilter().addAccType(accType);
            } else {
                request.getFilter().removeAccType(accType);
            }
        }
    };

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hotellist_filter, container, false);
        ButterKnife.bind(this, view);

        mPriceRangeBar.setPinTextFormatter(new RangeBar.PinTextFormatter() {
            @Override
            public String getText(String value) {
                return value;
            }
        });

        mPriceRangeBar.setOnRangeBarChangeListener(this);

        mApplyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onFiltersApply();
            }
        });

        mRatingsGroup.setOnCheckedChangeListener(this);

        mStarsGroup.setOnCheckedChangeListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Filter filter = getHotelsRequest().getFilter();
        int rateMaxPrice = ((HotelListFiltersActivity) getActivity()).getRateMaxPrice();
        int rateMinPrice = ((HotelListFiltersActivity) getActivity()).getRateMinPrice();

        int priceFrom = (filter.getMinRate() > 0) ? filter.getMinRate() : rateMinPrice;
        int priceTo = (filter.getMaxRate() > 0) ? filter.getMaxRate() : rateMaxPrice;

        AppLog.d(String.format("rateMinPrice: %d, rateMaxPrice: %d", rateMinPrice, rateMaxPrice));
        mPriceRangeBar.setTickEnd(rateMaxPrice);// end must be before start, else we will get error
        mPriceRangeBar.setTickStart(rateMinPrice);

        int diff = rateMaxPrice - rateMinPrice;
        if (diff > 100000) {
            mPriceRangeBar.setTickInterval(100);
        } else if (diff > 10000) {
            mPriceRangeBar.setTickInterval(50);
        } else if (diff > 1000) {
            mPriceRangeBar.setTickInterval(25);
        }

        mPriceText.setText(renderPriceRange(priceFrom, priceTo, getHotelsRequest().getNumberOfRooms()));
        mPriceRangeBar.setRangePinsByValue(priceFrom, priceTo);

        final boolean[] selectedAccTypes = calcSelected(filter.getAccTypes(), mAccTypes);
        mAccTypesList.post(new Runnable() {
            @Override
            public void run() {
                addAccTypesCheckboxes(selectedAccTypes);
            }
        });

        final boolean[] selectedFacilities = calcSelected(filter.getMainFacilities(), mFacilities);
        mFacilitiesList.post(new Runnable() {
            @Override
            public void run() {
                addFacilitiesCheckboxes(selectedFacilities);
            }
        });

        mRatingsGroup.setChecked(filter.getRating());
        mStarsGroup.setChecked(filter.getStars());

    }

    private String renderPriceRange(int priceFrom, int priceTo, int numberRooms) {
        PriceRender render = getPriceRender();
        // EUR 10 - EUR 1250
        return render.render(priceFrom, numberRooms).concat(" - ").concat(render.render(priceTo, numberRooms));
    }

    private void addAccTypesCheckboxes(boolean[] selectedAccTypes) {
        int columnWidth = mAccTypesList.getMeasuredWidth() / mAccTypesList.getColumnCount();

        LayoutInflater inflater = LayoutInflater.from(getActivity());

        int nextColumn = (int) Math.floor(mAccTypes.length / mAccTypesList.getColumnCount());

        for (int i = 0; i < mAccTypes.length; i++) {
            int accType = mAccTypes[i];
            CheckBox checkbox = (CheckBox) inflater.inflate(R.layout.results_filter_item, mAccTypesList, false);
            checkbox.setTag(accType);
            checkbox.setChecked(selectedAccTypes[i]);
            checkbox.setText(mAccTypesTitles[i]);
            checkbox.setOnCheckedChangeListener(mAccTypeChangeListener);

            GridLayout.LayoutParams lp = (GridLayout.LayoutParams) checkbox.getLayoutParams();
            lp.width = columnWidth;
            if (i < nextColumn) {
                lp.setMargins(0, 0, 0, mColumnMargin);
            } else {
                lp.setMargins(mColumnMargin, 0, 0, mColumnMargin);
            }
            checkbox.setLayoutParams(lp);

            mAccTypesList.addView(checkbox);
        }
    }

    private void addFacilitiesCheckboxes(boolean[] selectedFacilities) {
        int columnWidth = mFacilitiesList.getMeasuredWidth() / mFacilitiesList.getColumnCount();

        LayoutInflater inflater = LayoutInflater.from(getActivity());

        int nextColumn = (int) Math.floor(mFacilities.length / mAccTypesList.getColumnCount());

        for (int i = 0; i < mFacilities.length; i++) {
            int facility = mFacilities[i];
            CheckBox checkbox = (CheckBox) inflater.inflate(R.layout.results_filter_item, mFacilitiesList, false);
            checkbox.setTag(facility);
            checkbox.setText(mFacilitiesTitles[i]);
            checkbox.setChecked(selectedFacilities[i]);
            checkbox.setOnCheckedChangeListener(mFacilitiesChangeListener);

            GridLayout.LayoutParams lp = (GridLayout.LayoutParams) checkbox.getLayoutParams();
            lp.width = columnWidth;
            if (i < nextColumn) {
                lp.setMargins(0, 0, 0, mColumnMargin);
            } else {
                lp.setMargins(mColumnMargin, 0, 0, mColumnMargin);
            }
            checkbox.setLayoutParams(lp);

            mFacilitiesList.addView(checkbox);
        }
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

    private boolean[] calcSelected(SparseBooleanArray filter, int[] values) {
        boolean[] selected = new boolean[values.length];
        if (filter == null) {
            return selected;
        }

        for (int i = 0; i < values.length; i++) {
            selected[i] = filter.get(values[i]);
        }

        return selected;
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
        mPriceText.setText(renderPriceRange(priceFrom, priceTo, request.getNumberOfRooms()));

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
        void onFiltersApply();
    }
}
