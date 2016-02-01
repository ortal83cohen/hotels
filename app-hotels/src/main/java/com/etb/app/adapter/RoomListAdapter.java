package com.etb.app.adapter;


import android.content.Context;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.util.SortedListAdapterCallback;
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
import com.etb.app.preferences.UserPreferences;
import com.etb.app.utils.PriceRender;

public class RoomListAdapter extends RecyclerView.Adapter<RoomViewHolder> implements Filterable {
    private final RoomViewHolder.BookNowListener mBookNowListener;
    private final HotelListRequest mRequest;
    private final PriceRender mPriceRender;
    public View mView = null;

    private SortedList<Accommodation.Rate> mSortedRooms;

    private Context mContext;

    public RoomListAdapter(BaseActivity context, RoomViewHolder.BookNowListener bookNowListener) {
        mContext = context;
        mBookNowListener = bookNowListener;

        UserPreferences userPrefs = App.provide(mContext).getUserPrefs();
        String currencyCode = userPrefs.getCurrencyCode();

        mSortedRooms = new SortedList<>(Accommodation.Rate.class, new RoomsListCallback(this, currencyCode));
        mRequest = context.getHotelsRequest();
        mPriceRender = context.getPriceRender();
        setHasStableIds(true);
    }


    @Override
    public long getItemId(int position) {
        return mSortedRooms.get(position).rateId;
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public RoomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        mView = inflater.inflate(R.layout.room_list_item, parent, false);
        return new RoomViewHolder(mView, mPriceRender, mContext, mBookNowListener);
    }


    @Override
    public void onBindViewHolder(RoomViewHolder holder, int position) {
        Accommodation.Rate item = mSortedRooms.get(position);

        holder.assignItem(item, mRequest.getNumberOfRooms());

    }

    @Override
    public int getItemCount() {
        if (mSortedRooms == null) {
            return 0;
        }
        return mSortedRooms.size();

    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                //                myData = (List<AbstractDataProvider.GroupData>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                return new FilterResults();
            }
        };
    }

    public void setRooms(Accommodation accommodation) {
        if (accommodation != null && accommodation.rates != null) {
            mSortedRooms.addAll(accommodation.rates);
        }
    }


    private static class RoomsListCallback extends SortedListAdapterCallback<Accommodation.Rate> {
        private final String mCurrencyCode;

        public RoomsListCallback(RoomListAdapter adapter, String currencyCode) {
            super(adapter);
            mCurrencyCode = currencyCode;
        }

        @Override
        public int compare(Accommodation.Rate rate1, Accommodation.Rate rate2) {
            return rate1.displayPrice.get(mCurrencyCode) > rate2.displayPrice.get(mCurrencyCode) ? 1 : -1;
        }

        @Override
        public boolean areContentsTheSame(Accommodation.Rate oldItem, Accommodation.Rate newItem) {
            return false;
        }

        @Override
        public boolean areItemsTheSame(Accommodation.Rate item1, Accommodation.Rate item2) {
            return false;
        }
    }
}
