package com.etb.app.fragment;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easytobook.api.contract.Facility;
import com.easytobook.api.model.Accommodation;
import com.etb.app.R;
import com.etb.app.hoteldetails.HotelSnippet;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author alex
 * @date 2015-06-14
 */
public class HotelFacilitiesFragment extends BaseFragment {
    @Bind(R.id.hotel_description)
    TextView mHotelDetailsText;
    @Bind(R.id.payment_methods_text)
    TextView mPaymentMethodsText;
    @Bind(R.id.services)
    TextView mServices;
    @Bind(R.id.general_facilities)
    TextView mGeneralFacilities;
    @Bind(R.id.facilities)
    LinearLayout mFacilities;
    @Bind(R.id.payment_methods_images)
    LinearLayout mPaymentMethodsImages;


    private HotelSnippet mHotelSnippet;

    public static HotelFacilitiesFragment newInstance(HotelSnippet hotelSnippet) {
        HotelFacilitiesFragment fragment = new HotelFacilitiesFragment();
        Bundle args = new Bundle();
        args.putParcelable("snippet", hotelSnippet);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hoteldetails_facilities, container, false);
        ButterKnife.bind(this, view);

        mHotelSnippet = getArguments().getParcelable("snippet");
        addFacilities();
        addPaymentMethods();
        addServices();
        return view;
    }


    private void addFacilities() {
        mFacilities.removeAllViews();
        if (mHotelSnippet.getcheckInFrom().equals("") && mHotelSnippet.getcheckOutFrom().equals("")) {
            addFacilityView(R.drawable.ic_time, "Check-in / Check-out", "no information");
        } else {
            addFacilityView(R.drawable.ic_time, "Check-in / Check-out", mHotelSnippet.getcheckInFrom() + " / " + mHotelSnippet.getcheckOutFrom());
        }
        ArrayList<Accommodation.Facility> facilities = mHotelSnippet.getFacilities();

        String internetFacility = null;
        String parkingFacility = null;

        if (facilities != null) {
            for (Accommodation.Facility facility : facilities) {
                if (facility.id.equals(Facility.Extended.ID_INTERNET_WIRELESS)) {
                    internetFacility = "WIFI (" + (facility.data.free ? "complimentary" : "not complimentary") + ")";
                    break;
                } else if (facility.id.equals(Facility.Extended.ID_INTERNET_WIRED)) {
                    internetFacility = "Wired (" + (facility.data.free ? "complimentary" : "not complimentary") + ")";
                } else if (facility.category == Facility.Category.Internet) {
                    internetFacility = "Available";
                }
                if (Facility.Extended.ID_PARKING.equals(facility.id)) {
                    parkingFacility = facility.data.free ? "Free" : "Paid";
                } else if (facility.category == Facility.Category.Parking) {
                    parkingFacility = "Available";
                }
            }
        }

        if (internetFacility != null) {
            addFacilityView(R.drawable.wifi, "Internet", internetFacility);
        }
        if (parkingFacility != null) {
            addFacilityView(R.drawable.parking, "Parking", parkingFacility);
        }

        if (mHotelSnippet.getPetsPolicy() != null) {
            if (mHotelSnippet.getPetsPolicy().petsAllowed > 0) {
                addFacilityView(R.drawable.pets, "Pets Policy", "Allowed");
            } else if (mHotelSnippet.getPetsPolicy().petsAllowedOnRequest) {
                addFacilityView(R.drawable.pets, "Pets Policy", "Allowed On Request");
            } else {
                addFacilityView(R.drawable.pets, "Pets Policy", "Not allowed");
            }
        }
        if (mHotelSnippet.getNumberOfRooms() > 0) {
            addFacilityView(R.drawable.ic_key, "Rooms", String.valueOf(mHotelSnippet.getNumberOfRooms()));
        }
    }

    private void addPaymentMethods() {
        mPaymentMethodsImages.removeAllViews();

        ArrayList<Accommodation.Details.PostpaidCreditCard> creditCards = mHotelSnippet.getPostpaidCreditCards();
        if (creditCards != null) {
            String paymentMethodsText = "";
            for (Accommodation.Details.PostpaidCreditCard creditCard : creditCards) {
                switch (creditCard.code) {
                    case "VI":
                        addPaymentMethodView(R.drawable.credit_card_visa);
                        break;
                    case "MC":
                        addPaymentMethodView(R.drawable.credit_card_mastercard);
                        break;
                    case "AX":
                        addPaymentMethodView(R.drawable.credit_card_american_express);
                        break;
                    case "DN":
                        addPaymentMethodView(R.drawable.credit_card_diners_club);
                        break;
                    case "JC":
                        addPaymentMethodView(R.drawable.credit_card_jcb);
                        break;
                    case "DI":
                        addPaymentMethodView(R.drawable.credit_card_discover);
                        break;

                }
                paymentMethodsText += ", " + creditCard.name;
            }
            if (paymentMethodsText.length() > 3) {
                paymentMethodsText = paymentMethodsText.substring(2);
                mPaymentMethodsText.setText(paymentMethodsText);
            }
        }
    }

    private void addServices() {

        String services = "";
        String generalFacilities = "";

        for (Accommodation.Facility facility : mHotelSnippet.getFacilities()) {

            if (facility.category == Facility.Category.Service) {
                services += "&#8226;   " + facility.name + "<br/>";
            } else if (facility.category == Facility.Category.General) {
                generalFacilities += "&#8226;   " + facility.name + "<br/>";
            }

            mServices.setText(Html.fromHtml(services));
            mGeneralFacilities.setText(Html.fromHtml(generalFacilities));
        }
    }


    private void addFacilityView(@DrawableRes int iconRes, String text1, String text2) {
        FrameLayout view = (FrameLayout) getActivity().getLayoutInflater().inflate(R.layout.facilities_list_facility, mFacilities, false);
        ImageView image = (ImageView) view.findViewById(android.R.id.icon);
        image.setImageDrawable(getActivity().getResources().getDrawable(iconRes));
        TextView textView1 = (TextView) view.findViewById(android.R.id.text1);
        TextView textView2 = (TextView) view.findViewById(android.R.id.text2);
        textView1.setText(text1);
        textView2.setText(text2);
        mFacilities.addView(view);
    }

    private void addPaymentMethodView(@DrawableRes int iconRes) {
        ImageView image = (ImageView) getActivity().getLayoutInflater().inflate(R.layout.view_image, mPaymentMethodsImages, false);
        image.setImageDrawable(getActivity().getResources().getDrawable(iconRes));
        image.setPadding(10, 10, 10, 10);
        mPaymentMethodsImages.addView(image);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadData();
    }


    private void loadData() {
        mHotelDetailsText.setText(Html.fromHtml(mHotelSnippet.getDescription()));
    }

    @OnClick(R.id.read_more_description)
    public void onReadMoreDescriptionPressed(final Button readMoreDescription) {
        ObjectAnimator textAnimation = ObjectAnimator.ofInt(
                mHotelDetailsText,
                "maxLines",
                30);
        textAnimation.setDuration(1000);
        final int oldHeight = readMoreDescription.getLayoutParams().height;
        Animation buttonAnimation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                ViewGroup.LayoutParams params = readMoreDescription.getLayoutParams();
                params.height = (int) (oldHeight * (1 - interpolatedTime));
                readMoreDescription.setLayoutParams(params);
            }
        };
        buttonAnimation.setDuration(700);
        textAnimation.start();
        readMoreDescription.startAnimation(buttonAnimation);
        readMoreDescription.setVisibility(View.INVISIBLE);
    }

}
