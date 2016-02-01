package com.etb.app.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.FragmentActivity;

import java.util.ArrayList;

/**
 * @author alex
 * @date 2015-12-02
 */
public class LikedHotels {
    public static boolean isLiked(int hotelId, Context context) {
        Cursor c = context.getContentResolver().query(DbContract.LikedHotels.CONTENT_URI.buildUpon().appendPath(
                String.valueOf(hotelId)).build(), null, null, null, null);
        boolean liked = false;
        if (c != null) {
            if (c.moveToFirst()) {
                liked = true;
            }
            c.close();
        }
        return liked;
    }

    public static void delete(int hotelId, Context context) {
        context.getContentResolver().delete(DbContract.LikedHotels.CONTENT_URI.buildUpon().appendPath(String.valueOf(hotelId)).build(), null, null);
    }

    public static void insert(int hotelId, String city, String country, Context context) {
        ContentValues values = new ContentValues();
        values.put(DbContract.LikedHotelsColumns.KEY_ID, hotelId);
        values.put(DbContract.LikedHotelsColumns.CITY, city);
        values.put(DbContract.LikedHotelsColumns.COUNTRY, country);
        context.getContentResolver().insert(DbContract.LikedHotels.CONTENT_URI, values);

    }

    public static ArrayList<String> loadHotels(String city, String country, Context context) {
        Cursor cursor = context.getContentResolver().query(DbContract.LikedHotels.CONTENT_URI.buildUpon().
                appendQueryParameter("where", DbContract.LikedHotelsColumns.CITY + "='" + city + "' AND " + DbContract.LikedHotelsColumns.COUNTRY + "='" + country + "'").build(), null, null, null, null);
        ArrayList<String> hotels = new ArrayList<>();
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                hotels.add(cursor.getString(cursor.getColumnIndex(DbContract.LikedHotelsColumns.KEY_ID)));
            } while (cursor.moveToNext());
            cursor.close();
        }

        return hotels;
    }
}
