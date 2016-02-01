package com.etb.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.etb.app.R;
import com.etb.app.core.model.Ratings;
import com.etb.app.core.model.ReviewInfo;
import com.etb.app.hoteldetails.HotelSnippet;
import com.etb.app.widget.recyclerview.HeaderAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author alex
 * @date 2015-06-15
 */
public class HotelReviewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements HeaderAdapter {

    private static final int VIEW_TYPE_HEADER = 1;
    private static final int VIEW_TYPE_ITEM = 0;
    private final Context mContext;
    private LayoutInflater mLayoutInflater;

    private HotelSnippet mHotelSnippet;
    private Ratings mRatings;

    private List<ReviewInfo> mReviews = new ArrayList<>();

    public HotelReviewsAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    public void setHotelSnippet(HotelSnippet hotelSnippet) {
        mHotelSnippet = hotelSnippet;
    }

    public void setRatings(Ratings ratings) {
        mRatings = ratings;
        notifyItemChanged(0);
    }

    @Override
    public int getHeaderCount() {
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_HEADER;
        }
        return VIEW_TYPE_ITEM;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mReviews.size() + 1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            View header = mLayoutInflater.inflate(R.layout.reviews_header, parent, false);
            return new ReviewHeaderViewHolder(header, mContext.getResources(), mLayoutInflater);
        }
        View header = mLayoutInflater.inflate(R.layout.reviews_item, parent, false);
        return new ReviewViewHolder(header, mContext);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position == 0) {
            ReviewHeaderViewHolder header = (ReviewHeaderViewHolder) holder;
            header.bindSnippetInfo(mHotelSnippet);
            header.bindRatings(mRatings);
            return;
        }

        ((ReviewViewHolder) holder).bindReview(mReviews.get(position - 1));

    }

    public void addAll(List<ReviewInfo> reviews) {
        mReviews.addAll(reviews);
        notifyItemRangeInserted(getItemCount(), reviews.size());
    }

    public boolean isEmpty() {
        return mReviews.size() == 0;
    }
}
