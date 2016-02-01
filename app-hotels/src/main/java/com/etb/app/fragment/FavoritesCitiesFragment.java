package com.etb.app.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.etb.app.R;
import com.etb.app.activity.FavoritesActivity;
import com.etb.app.adapter.FavoritesCitiesAdapter;
import com.etb.app.provider.DbContract;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author alex
 * @date 2015-05-17
 */
public class FavoritesCitiesFragment extends BaseFragment {
    @Bind(android.R.id.list)
    ListView mRecyclerView;
    @Bind(R.id.hotel_list_no_result)
    LinearLayout mNoResult;

    public static FavoritesCitiesFragment newInstance() {
        return new FavoritesCitiesFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites_cities, container, false);

        ButterKnife.bind(this, view);
        Cursor cursor = getActivity().getContentResolver().query(DbContract.LikedHotels.CONTENT_URI.buildUpon().
                appendQueryParameter("group by", DbContract.LikedHotelsColumns.CITY + "," + DbContract.LikedHotelsColumns.COUNTRY).build(), null, null, null, null);

        if (cursor == null) {
            mNoResult.setVisibility(View.VISIBLE);
            return view;
        }

        if (cursor.getCount() == 0) {
            mNoResult.setVisibility(View.VISIBLE);
            cursor.close();
        } else {
            mNoResult.setVisibility(View.GONE);
            mRecyclerView.setAdapter(new FavoritesCitiesAdapter(getActivity(), cursor));
            mRecyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                    ((FavoritesActivity) getActivity()).showFavoritesList(cursor.getString(cursor.getColumnIndex(DbContract.LikedHotelsColumns.CITY)), cursor.getString(cursor.getColumnIndex(DbContract.LikedHotelsColumns.COUNTRY)), cursor.getString(cursor.getColumnIndex("count")));
                }
            });
            getActivity().setTitle(R.string.favorites);
        }


        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onResume() {
        super.onResume();
    }


}
