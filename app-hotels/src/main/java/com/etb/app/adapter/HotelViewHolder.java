package com.etb.app.adapter;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.easytobook.api.model.Accommodation;
import com.easytobook.api.utils.ImageUtils;
import com.etb.app.App;
import com.etb.app.R;
import com.etb.app.preferences.UserPreferences;
import com.etb.app.provider.LikedHotels;
import com.etb.app.utils.PriceRender;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HotelViewHolder extends RecyclerView.ViewHolder {

    private final int mPictureWidth;
    private final int mPictureHeight;

    private final Context mContext;
    private final HotelViewHolder.Listener mListener;
    private final PriceRender mPriceRender;
    @Bind(R.id.image)
    public ImageView mImageView;
    @Bind(android.R.id.title)
    public TextView mTitleView;
    @Bind(R.id.price)
    public TextView mPrice;
    @Bind(R.id.base_rate)
    public TextView mBaseRate;
    @Bind(R.id.discount)
    public TextView mDiscount;
    @Bind(R.id.triangle)
    public View mTriangle;
    @Bind(R.id.star_rating)
    public RatingBar mStarRating;
    @Bind(R.id.tripadvisor_rating)
    public RatingBar mTripadvisorRating;
    @Bind(R.id.tripadvisor_image)
    public ImageView mTripadvisorImage;
    @Bind(R.id.heart_icon)
    public ImageView mHeartIcon;
    @Bind(R.id.background)
    public FrameLayout mBackground;
    private Accommodation mItem;
    private int mPosition;

    public interface Listener {
        void onHotelClick(Accommodation acc, int position);
    }

    public HotelViewHolder(View v, Context context, int pictureWidth, int pictureHeight, PriceRender priceRender, HotelViewHolder.Listener listener) {
        super(v);
        mListener = listener;
        ButterKnife.bind(this, v);
        mPictureWidth = pictureWidth;
        mPictureHeight = pictureHeight;
        mContext = context;
        mPriceRender = priceRender;
    }

    public void assignItem(final Accommodation item, int numberRooms, int position) {
        mPosition = position;
        mItem = item;
        final Resources r = mContext.getResources();
        if (mItem.images != null && mItem.images.size() > 0) {
            if (mPictureWidth > 0 && mPictureHeight > 0) {
                String imageUrl = ImageUtils.resizeUrl(mItem.images.get(0), mPictureWidth, mPictureHeight);
                Picasso.with(mContext)
                        .load(imageUrl)
                        .resize(mPictureWidth, mPictureHeight)
                        .centerCrop()
                        .placeholder(R.drawable.place_holder_img)
                        .into(mImageView);
            } else {
                Picasso.with(mContext)
                        .load(mItem.images.get(0))
                        .fit().centerCrop()
                        .placeholder(R.drawable.place_holder_img)
                        .into(mImageView);
            }
        } else {
            mImageView.setImageResource(R.drawable.place_holder_img);
        }

        mTitleView.setText(mItem.name);

        mTitleView.setTag(position);

        UserPreferences userPrefs = App.provide(mContext).getUserPrefs();
        String curCode = userPrefs.getCurrencyCode();
        mPrice.setText(mPriceRender.render(mPriceRender.price(mItem, curCode), numberRooms));
        if (mItem.getFirstRate() != null) {
            mPrice.setVisibility(View.VISIBLE);
            int discountNumber = mPriceRender.discount(mItem.getFirstRate(), curCode);
            if (discountNumber < 10) {
                mDiscount.setVisibility(View.GONE);
                mBaseRate.setVisibility(View.GONE);
                mTriangle.setVisibility(View.INVISIBLE); // Keep layout height
            } else {
                mTriangle.setVisibility(View.VISIBLE);
                mBaseRate.setVisibility(View.VISIBLE);
                mBaseRate.setText(mPriceRender.render(mPriceRender.basePrice(mItem.getFirstRate(), curCode), numberRooms));
                mBaseRate.setPaintFlags(mBaseRate.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                mDiscount.setVisibility(View.VISIBLE);
                mDiscount.setText(String.valueOf(discountNumber).concat("%"));
            }
        } else {
            mPrice.setVisibility(View.GONE);
            mDiscount.setVisibility(View.GONE);
            mBaseRate.setVisibility(View.GONE);
            mTriangle.setVisibility(View.INVISIBLE); // Keep layout height
        }

        mStarRating.setRating(mItem.starRating);
        if (mItem.summary.reviewScore > 0) {
            mTripadvisorImage.setVisibility(View.VISIBLE);
            mTripadvisorRating.setVisibility(View.VISIBLE);
            mTripadvisorRating.setRating(mItem.summary.reviewScore);
        } else {
            mTripadvisorImage.setVisibility(View.GONE);
            mTripadvisorRating.setVisibility(View.GONE);
        }

        boolean isLiked = LikedHotels.isLiked(mItem.id, mContext);

        if (isLiked) {
            mHeartIcon.setImageDrawable(r.getDrawable(R.drawable.btn_favorite_selected));
        } else {
            mHeartIcon.setImageDrawable(r.getDrawable(R.drawable.btn_favorite));
        }
        mHeartIcon.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {

                      boolean isLiked = LikedHotels.isLiked(mItem.id, mContext);
                      if (isLiked) {
                          LikedHotels.delete(mItem.id, mContext);
                          mHeartIcon.setImageDrawable(r.getDrawable(R.drawable.btn_favorite));
                      } else {
                          mHeartIcon.setImageDrawable(r.getDrawable(R.drawable.btn_favorite_selected));
                          LikedHotels.insert(mItem.id, mItem.summary.city, mItem.summary.country, mContext);
                      }
                  }
              }
        );

        if (mItem.isUnavailable()) {
            setViewDisabled(r);
        } else {
            setViewEnabled(r);
        }
    }

    private void setViewEnabled(Resources r) {
        //.setBackgroundColor(r.getColor(android.R.color.transparent));
        mTripadvisorRating.setEnabled(true);
        mStarRating.setEnabled(true);
        mBackground.setEnabled(true);
        mTitleView.setTextColor(r.getColor(android.R.color.black));
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onHotelClick(mItem, mPosition);
            }
        });
        mImageView.setColorFilter(null);
        mTripadvisorImage.setColorFilter(null);
    }

    private void setViewDisabled(Resources r) {
        //.setBackgroundColor(r.getColor(R.color.transparent_black));
        mTripadvisorRating.setEnabled(false);
        mStarRating.setEnabled(false);
        mBackground.setEnabled(false);
        mTitleView.setTextColor(r.getColor(android.R.color.darker_gray));
        itemView.setOnClickListener(null);
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        mImageView.setColorFilter(filter);
        mTripadvisorImage.setColorFilter(filter);
    }

}