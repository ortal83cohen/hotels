package com.etb.app.fragment;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import com.etb.app.R;
import com.etb.app.adapter.CountryAdapter;
import com.etb.app.adapter.StateAdapter;
import com.etb.app.model.BillingAddressPage;

import java.security.InvalidParameterException;

import butterknife.ButterKnife;

/**
 * @author alex
 * @date 2015-04-28
 */
public class BillingAddressFragment extends PageFragment {
    Spinner mCountryView;
    Spinner mStateView;
    EditText mCityView;
    EditText mAddressView;
    EditText mZipCodeView;
    String mCountryName = "";
    String mState = "";
    private CountryAdapter mCountryAdapter;
    private StateAdapter mStateAdapter;

    public static BillingAddressFragment newInstance() {
        return new BillingAddressFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_booking_payment_billing_address, container, false);
        initViews(view);

        initViewsData();
        return view;
    }

    private void initViews(View view) {
        mCountryView = ButterKnife.findById(view, R.id.billing_country);
        mCityView = ButterKnife.findById(view, R.id.billing_city);
        mStateView = ButterKnife.findById(view, R.id.billing_state);
        mAddressView = ButterKnife.findById(view, R.id.billing_address);
        mZipCodeView = ButterKnife.findById(view, R.id.billing_postal);
    }

    private void initViewsData() {

        mCountryAdapter = new CountryAdapter(getActivity());
        mStateAdapter = new StateAdapter(getActivity());

        mCountryView.setAdapter(mCountryAdapter);
        mStateView.setAdapter(mStateAdapter);

        mCountryName = getActivity().getResources().getConfiguration().locale.getDisplayCountry();
        int spinnerPosition = mCountryAdapter.getPosition(mCountryName);
        mCountryView.setSelection(spinnerPosition);
        //mStateView.setSelection(0);


        mCountryView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCountryView.focusSearch(View.FOCUS_DOWN).requestFocus();
                mCountryName = mCountryAdapter.getItem(position);
                String countryIso = mCountryAdapter.getISOCodeAt(position);
                mStateAdapter.setCountryCode(countryIso);
                setStateVisibility();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mStateView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mStateView.focusSearch(View.FOCUS_DOWN).requestFocus();
                mState = mStateAdapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        setStateVisibility();

        if (mPage != null) {
            Bundle pageData = mPage.getData();
            mCityView.setText(pageData.getString(BillingAddressPage.DATA_BILLING_CITY));
            mAddressView.setText(pageData.getString(BillingAddressPage.DATA_BILLING_ADDRESS));
            mZipCodeView.setText(pageData.getString(BillingAddressPage.DATA_BILLING_ZIP));
        }
    }

    private void setStateVisibility() {
        String countryCode = mCountryAdapter.getISOCode(mCountryName);

        if (countryCode.equals("US") || countryCode.equals("CA") || countryCode.equals("AU")) {
            mStateView.setVisibility(View.VISIBLE);
            mCountryView.setNextFocusDownId(R.id.billing_state);
        } else {
            mStateView.setVisibility(View.GONE);
            mCountryView.setNextFocusDownId(R.id.billing_city);
        }
    }

    private void validate() {

        Resources r = getActivity().getResources();
        if (mCityView.length() == 0) {
            mCityView.requestFocus();
            mCityView.setError("City" + r.getString(R.string.is_required));
            throw new InvalidParameterException(r.getString(R.string.all_fields_must_be_filled));
        }
        if (mAddressView.length() == 0) {
            mAddressView.requestFocus();
            mAddressView.setError("Address" + r.getString(R.string.is_required));
            throw new InvalidParameterException(r.getString(R.string.all_fields_must_be_filled));
        }
        if (mZipCodeView.length() == 0) {
            mZipCodeView.requestFocus();
            mZipCodeView.setError("ZipCode" + r.getString(R.string.is_required));
            throw new InvalidParameterException(r.getString(R.string.all_fields_must_be_filled));
        }

    }

    @Override
    public String getPageKey() {
        return BillingAddressPage.KEY;
    }

    @Override
    protected void onPageDataSave(Bundle pageData) {
        validate();
        pageData.putString(BillingAddressPage.DATA_BILLING_COUNTRY_ISO, mCountryAdapter.getISOCode(mCountryName));
        pageData.putString(BillingAddressPage.DATA_BILLING_COUNTRY, mCountryName);
        pageData.putString(BillingAddressPage.DATA_BILLING_CITY, mCityView.getText().toString());
        pageData.putString(BillingAddressPage.DATA_STATE, mStateAdapter.getISOCode(mState));
        pageData.putString(BillingAddressPage.DATA_BILLING_ADDRESS, mAddressView.getText().toString());
        pageData.putString(BillingAddressPage.DATA_BILLING_ZIP, mZipCodeView.getText().toString());

    }
}
