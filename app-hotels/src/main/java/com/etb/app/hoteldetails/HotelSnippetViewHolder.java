package com.etb.app.hoteldetails;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.etb.app.R;
import com.etb.app.adapter.ImagesPagerAdapter;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author alex
 * @date 2015-04-28
 */
public class HotelSnippetViewHolder {
    private final Context mContext;
    private final LayoutInflater mInflater;

    @Bind(R.id.pager)
    ViewPager mSnippetImagePager;
    @Bind(R.id.snippet_title)
    TextView mSnippetTitle;
    @Bind(R.id.star_rating)
    RatingBar mRatingBar;
    @Bind(R.id.reviewers)
    TextView mReviewers;
    @Bind(R.id.reviews)
    TextView mReviews;
    @Bind(R.id.number_images)
    TextView mNumberImages;
    @Bind(R.id.tripadvisor_rating)
    RatingBar mTripadvisorRating;
    @Bind(R.id.tripadvisor_bar_small)
    View mTripadvisorBar;
    @Bind(R.id.facilities)
    LinearLayout mFacilities;
    @Bind(R.id.facilities_bar)
    View mFacilitiesBar;

    public HotelSnippetViewHolder(View container, Context context) {
        mContext = context;
        ButterKnife.bind(this, container);
        mInflater = LayoutInflater.from(context);
    }

    public void render(HotelSnippet hotelSnippet) {
        mSnippetTitle.setText(hotelSnippet.getName());

        if (hotelSnippet.getReviewCount() > 0) {
            mReviewers.setText(mContext.getString(R.string.hotel_reviews, hotelSnippet.getReviewCount()));
            if (mReviews != null) {
                mReviews.setText(String.valueOf(hotelSnippet.getReviewScore()));
            }
            mTripadvisorRating.setRating(hotelSnippet.getReviewScore());
            mTripadvisorBar.setVisibility(View.VISIBLE);
        } else {
            mTripadvisorBar.setVisibility(View.GONE);

        }

        mRatingBar.setRating(hotelSnippet.getStarRating());
        if (hotelSnippet.getImageUrl() != null) {
            final ImagesPagerAdapter imagesPagerAdapter = new ImagesPagerAdapter(mContext);
            imagesPagerAdapter.addItems(hotelSnippet.getImagesUrl());
            mSnippetImagePager.setAdapter(imagesPagerAdapter);
            mSnippetImagePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    mNumberImages.setText(new StringBuilder().append(String.valueOf(position + 1)).append("/").append(String.valueOf(imagesPagerAdapter.getCount())).toString());
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            mNumberImages.setText(new StringBuilder().append("1/").append(String.valueOf(imagesPagerAdapter.getCount())).toString());
        }
        if (mFacilities != null) {
            ArrayList<Integer> facilities = hotelSnippet.getMainFacilities();

            mFacilities.removeAllViews();
            if (facilities != null) {
//                mFacilitiesNumber.setText("(" + String.valueOf(facilities.size()) + ")");
                for (int facility : facilities) {
                    switch (facility) {
//                        case 0:// disabled friendly
//                            addFacilityView(R.drawable.ic_cancellation_policy);
//                            break;
                        case 1:// internet
                            addFacilityView(R.drawable.wifi);
                            break;
                        case 2:// parking
                            addFacilityView(R.drawable.parking);
                            break;
                        case 3:// pets allowed
                            addFacilityView(R.drawable.pets);
                            break;
//                        case 4:// child mDiscount (only shown in certain cases)
//                            addFacilityView(R.drawable.ic_child);
//                            break;
                        case 5:// swimming pool
                            addFacilityView(R.drawable.swimming_pool);
                            break;
//                        case 6:// air conditioning
//                            addFacilityView(R.drawable.ic_cancellation_policy);
//                            break;
//                        case 7:// fitness center
//                            addFacilityView(R.drawable.ic_cancellation_policy);
//                            break;
//                        case 8:// non-smoking
//                            addFacilityView(R.drawable.ic_cancellation_policy);
//                            break;

                    }

                }

            }

            if (mFacilities.getChildCount() == 0) {
                mFacilitiesBar.setVisibility(View.GONE);
            } else {
                mFacilitiesBar.setVisibility(View.VISIBLE);
            }
        }

    }


    private void addFacilityView(@DrawableRes int iconRes) {
        ImageView view = (ImageView) mInflater.inflate(R.layout.hoteldetail_facility, mFacilities, false);
        view.setMaxHeight(16);
        view.setMaxWidth(16);
        view.setImageDrawable(mContext.getResources().getDrawable(iconRes));
        mFacilities.addView(view);
    }
}
