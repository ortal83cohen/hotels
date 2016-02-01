package com.etb.app.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.etb.app.R;
import com.etb.app.randerscript.BlurBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

/**
 * @author user
 * @date 2015-09-03
 */
public class SummaryImageViewHolder {
    private final Context mContext;
    ImageView mImage;
    TextView mSnippetTitle;
    RatingBar mRatingBar;

    public SummaryImageViewHolder(Context context, RatingBar ratingBar, TextView snippetTitle, ImageView image) {
        mContext = context;
        mImage = image;
        mSnippetTitle = snippetTitle;
        mRatingBar = ratingBar;
    }


    public void bind(String url, String name, int rating) {

        if (!TextUtils.isEmpty(url)) {
            Picasso.with(mContext)
                    .load(url)
                    .fit()
                    .centerCrop()
                    .placeholder(R.drawable.place_holder_img)
                    .into(mImage);
        }

        mSnippetTitle.setText(name);
        mRatingBar.setRating(rating);
    }

}