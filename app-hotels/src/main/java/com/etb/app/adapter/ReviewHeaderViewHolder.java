package com.etb.app.adapter;

import android.content.res.Resources;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.etb.app.R;
import com.etb.app.core.model.Ratings;
import com.etb.app.hoteldetails.HotelSnippet;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ReviewHeaderViewHolder extends RecyclerView.ViewHolder {
    private final Resources mResources;
    @Bind(R.id.tripadvisor_rating)
    RatingBar mTripadvisorRating;
    @Bind(R.id.reviewers)
    TextView mReviewers;
    @Bind(R.id.reviews)
    TextView mReviews;
    @Bind(R.id.ratings)
    LinearLayout mRatings;
    private LayoutInflater mLayoutInflater;

    public ReviewHeaderViewHolder(View itemView, Resources resources, LayoutInflater inflater) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mResources = resources;
        mLayoutInflater = inflater;
    }

    public void bindSnippetInfo(HotelSnippet snippet) {
        if (snippet != null && snippet.getReviewCount() > 0) {
            mReviewers.setText(mResources.getQuantityString(R.plurals.reviews_number_text, snippet.getReviewCount(), snippet.getReviewCount()));
            mReviews.setText(String.format("%.1f / 5", snippet.getReviewScore()));
            mTripadvisorRating.setRating(snippet.getReviewScore());
            mTripadvisorRating.setVisibility(View.VISIBLE);
        } else {
            mTripadvisorRating.setVisibility(View.GONE);
        }
    }

    public void bindRatings(Ratings ratings) {
        mRatings.removeAllViews(); // TODO: cache
        if (ratings == null) {
            return;
        }
        if (ratings.location != null) {
            addRating(ratings.location, R.string.rating_location);
        }
        if (ratings.rooms != null) {
            addRating(ratings.rooms, R.string.rating_rooms);
        }
        if (ratings.cleanliness != null) {
            addRating(ratings.cleanliness, R.string.rating_cleanliness);
        }
        if (ratings.value != null) {
            addRating(ratings.value, R.string.rating_value);
        }
        if (ratings.service != null) {
            addRating(ratings.service, R.string.rating_service);
        }
        if (ratings.sleepquality != null) {
            addRating(ratings.sleepquality, R.string.rating_sleep_quality);
        }
    }

    private void addRating(float location, @StringRes int textRes) {
        View ratingView = mLayoutInflater.inflate(R.layout.reviews_rating, mRatings, false);

        ((TextView) ratingView.findViewById(R.id.rating_title)).setText(textRes);
        ((ProgressBar) ratingView.findViewById(R.id.rating_progressbar)).setProgress((int) (location * 10));
        ((TextView) ratingView.findViewById(R.id.rating_value)).setText(String.format("%.1f", location));

        mRatings.addView(ratingView);
    }
}