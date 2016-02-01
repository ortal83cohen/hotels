package com.etb.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.easytobook.api.model.Accommodation;
import com.etb.app.R;
import com.etb.app.activity.BaseActivity;
import com.etb.app.fragment.HotelsListFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * @author user
 * @date 2015-10-26
 */
public class FavoritesHotelListAdapter extends HotelListAdapter {
    private static final int HEADER = 0;
    private static final int OTHER = 1;
    List<Accommodation> mAccommodationsWithoutDates = new ArrayList<>();

    public FavoritesHotelListAdapter(BaseActivity context, HotelViewHolder.Listener listener) {
        super(context, listener);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == HEADER) {
            return new ViewHolder(
                    inflater.inflate(R.layout.results_hotel_list_header, parent, false)
            );
        }

        View view = inflater.inflate(R.layout.results_hotel_list_item, parent, false);
        return new HotelViewHolder(view, mContext, mPictureWidth, mPictureHeight, mPriceRender, mListener);

    }

    @Override
    public int getItemViewType(int position) {
        if (position == mAccommodations.size())
            return HEADER;
        else
            return OTHER;
    }

    @Override
    public int getItemCount() {
        if (items().size() > mAccommodations.size()) {
            return items().size() + 1;
        }
        return items().size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position > mAccommodations.size()) {
            position = position - 1;
        }
        super.onBindViewHolder(holder, position);
    }

    @Override
    public long getItemId(int position) {
        if (position > mAccommodations.size()) {
            position = position - 1;
        }
        return super.getItemId(position);
    }

    protected List<Accommodation> items() {

        if (mAccommodations.isEmpty() || mAccommodationsWithoutDates.isEmpty()) {
            return mAccommodations;
        }
        List<Accommodation> items = new ArrayList<>();
        items.addAll(mAccommodations);
        for (Accommodation accWithoutDates : mAccommodationsWithoutDates) {
            boolean nonExistFlag = true;
            for (Accommodation acc : items) {
                if (accWithoutDates.id == acc.id) {
                    nonExistFlag = false;
                    break;
                }
            }
            if (nonExistFlag) {
                accWithoutDates.markUnavailable();
                items.add(accWithoutDates);
            }
        }
        return items;
    }

    public void addHotels(List<Accommodation> accommodations, boolean withoutDates) {
        int objectsCount = getItemCount();
        int accommodationsCount = accommodations == null ? 0 : accommodations.size();
        if (accommodations != null) {
            mAccommodations.addAll(accommodations);
            if (withoutDates) {
                mAccommodationsWithoutDates.addAll(accommodations);
            }
        }
        notifyItemRangeInserted(objectsCount, accommodationsCount);
    }

}
