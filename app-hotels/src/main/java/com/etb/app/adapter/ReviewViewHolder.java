package com.etb.app.adapter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.etb.app.R;
import com.etb.app.core.model.ReviewInfo;
import com.etb.app.utils.AppLog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final java.text.DateFormat mDateFormat;
    private final SimpleDateFormat mDateParser;

    @Bind(R.id.name)
    TextView mName;
    @Bind(R.id.purpose)
    TextView mPurpose;
    @Bind(R.id.date)
    TextView mDate;
    @Bind(android.R.id.title)
    TextView mTitle;
    @Bind(android.R.id.text1)
    TextView mTextShort;
    @Bind(R.id.tripadvisor_rating)
    RatingBar mRatingBar;
    @Bind(R.id.reviews)
    TextView mRating;


    public ReviewViewHolder(View itemView, Context context) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        mDateFormat = DateFormat.getDateFormat(context);
        // 2015-06-14T14:48:03+02:00
        // TODO: test timezone issue for different android verison
        mDateParser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US);
    }

    public void bindReview(ReviewInfo info) {
        Date date = null;
        try {
            date = mDateParser.parse(info.isoDate);
        } catch (ParseException e) {
            AppLog.e("Unable to parse: '" + info.isoDate + "'", e);
        }

        if (date != null) {
            mDate.setText(mDateFormat.format(date));
        } else {
            mDate.setText("");
        }

        if (TextUtils.isEmpty(info.review.author.name)) {
            mName.setText(R.string.anonymous);
        } else {
            mName.setText(info.review.author.name);
        }
        if (TextUtils.isEmpty(info.purpose)) {
            mPurpose.setVisibility(View.GONE);
        } else {
            mPurpose.setText(new StringBuilder().append("(").append(info.purpose).append(")").toString());
            mPurpose.setVisibility(View.VISIBLE);
        }
        if (TextUtils.isEmpty(info.review.title)) {
            mTitle.setVisibility(View.GONE);
        } else {
            mTitle.setText(new StringBuilder().append("\"").append(info.review.title).append("\"").toString());
            mTitle.setVisibility(View.VISIBLE);
        }
        if (TextUtils.isEmpty(info.review.text)) {
            mTextShort.setVisibility(View.GONE);
        } else {
            mTextShort.setText(new StringBuilder().append("\"").append(info.review.text).append("\"").toString());
            mTextShort.setVisibility(View.VISIBLE);
            mTextShort.setOnClickListener(this);
        }
        mRating.setText(String.format("%.1f", info.review.rating));
        mRatingBar.setRating(info.review.rating);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == android.R.id.text1) {
            ObjectAnimator animation = ObjectAnimator.ofInt(mTextShort, "maxLines", 40);
            animation.setDuration(200).start();
        }
    }
}