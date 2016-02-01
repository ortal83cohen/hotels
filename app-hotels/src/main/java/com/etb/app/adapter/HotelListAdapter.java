package com.etb.app.adapter;


import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.easytobook.api.model.Accommodation;
import com.etb.app.App;
import com.etb.app.R;
import com.etb.app.activity.BaseActivity;
import com.etb.app.model.HotelListRequest;
import com.etb.app.utils.AppLog;
import com.etb.app.utils.PriceRender;
import com.etb.app.widget.recyclerview.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

public class HotelListAdapter extends ArrayAdapter<Accommodation, RecyclerView.ViewHolder> implements Filterable {
    protected final HotelViewHolder.Listener mListener;
    protected final int mPictureWidth;
    protected final int mPictureHeight;
    private final HotelListRequest mRequest;
    protected final PriceRender mPriceRender;
    protected Context mContext;
    protected List<Accommodation> mAccommodations = new ArrayList<>();

    public HotelListAdapter(BaseActivity activity, HotelViewHolder.Listener listener) {
        super(new ArrayList<Accommodation>());
        mContext = activity;
        mListener = listener;
        Resources r = mContext.getResources();
        mPictureWidth = r.getDimensionPixelSize(R.dimen.listview_image_width);
        mPictureHeight = r.getDimensionPixelSize(R.dimen.listview_image_height);
        AppLog.d("Image size [" + mPictureWidth + "," + mPictureHeight + "]");
        mRequest = activity.getHotelsRequest();
        mPriceRender = activity.getPriceRender();
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return items().get(position).id;
    }


    @Override
    public int getItemCount() {
        return items().size();
    }

    protected List<Accommodation> items() {
        return mAccommodations;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.results_hotel_list_item, parent, false);
        return new HotelViewHolder(view, mContext, mPictureWidth, mPictureHeight, mPriceRender, mListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Accommodation item = items().get(position);
        if (item != null && holder instanceof HotelViewHolder) {
            ((HotelViewHolder) holder).assignItem(item, mRequest.getNumberOfRooms(), position);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                return new FilterResults();
            }
        };
    }

    @Override
    public void clear() {
        mAccommodations.clear();
        notifyDataSetChanged();
    }

    public void addHotels(List<Accommodation> accommodations, boolean withoutDates) {
        int objectsCount = getItemCount();
        int accommodationsCount = accommodations == null ? 0 : accommodations.size();
        if (accommodations != null) {
            mAccommodations.addAll(accommodations);
        }
        notifyItemRangeInserted(objectsCount, accommodationsCount);
    }


}
