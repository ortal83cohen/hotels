package com.etb.app.adapter;


import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.etb.app.R;
import com.etb.app.utils.AppLog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RecentSearchesAdapter extends CursorAdapter {

    SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-d k", Locale.US);
    SimpleDateFormat output = new SimpleDateFormat("MMM d", Locale.getDefault());

    public RecentSearchesAdapter(Context context, Cursor c) {
        super(context, c, false);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // when the view will be created for first time,
        // we need to tell the adapters, how each item will look
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        return inflater.inflate(R.layout.autocomplite_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView textViewPersonName = (TextView) view.findViewById(android.R.id.title);
        Resources r = context.getResources();
        try {
            Date fromDate = input.parse(cursor.getString(cursor.getColumnIndex("from_date")) + " 23");
            Date toDate = input.parse(cursor.getString(cursor.getColumnIndex("to_date")) + " 23");

            String[] locationName = cursor.getString(cursor.getColumnIndex("location_name")).split(",");
            String title = "<font color=\"black\"> <b>" + locationName[0] + " </b>";
            for (int i = 1; i < locationName.length; i++) {
                title += locationName[i];
            }
            String subTitle = "";
            if (fromDate.after(new Date(System.currentTimeMillis()))) {
                subTitle = " <br>" + cursor.getString(cursor.getColumnIndex("number_guests")) + " " +
                        r.getQuantityString(R.plurals.guests, cursor.getInt(cursor.getColumnIndex("number_guests"))) + ", "
                        + output.format(fromDate) + " - "
                        + output.format(toDate);
            }
            textViewPersonName.setText(
                    Html.fromHtml(title + "</font>" + subTitle));

        } catch (ParseException e) {
            AppLog.e(e);
        }
        textViewPersonName.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.mainnav_recent_search, 0, 0, 0);
        int padding = (int) r.getDimension(R.dimen.minimum_default_padding);
        textViewPersonName.setPadding(0, padding, padding, padding);

    }
}