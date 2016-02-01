package com.etb.app.adapter;


import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.etb.app.R;

public class FavoritesCitiesAdapter extends CursorAdapter {

    public FavoritesCitiesAdapter(Context context, Cursor c) {
        super(context, c, false);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // when the view will be created for first time,
        // we need to tell the adapters, how each item will look
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        return inflater.inflate(R.layout.favorites_city_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView textViewPersonName = (TextView) view.findViewById(android.R.id.title);
        textViewPersonName.setText(new StringBuilder().append(cursor.getString(2)).append(", ").append(cursor.getString(3)).append(" (").append(cursor.getString(0)).append(")").toString());

    }
}