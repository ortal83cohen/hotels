package com.etb.app.provider;

/**
 * @author user
 * @date 2015-07-12
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DbDatabase extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 41;
    // Database Name
    private static final String DATABASE_NAME = "EtbHotelsDB";

    public DbDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static void deleteDatabase(Context context) {
        context.deleteDatabase(DATABASE_NAME);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create Hotel table
        String CREATE_HOTEL_TABLE = "CREATE TABLE IF NOT EXISTS " + DbContract.Tables.TABLE_LIKED_HOTELS + " ( " +
                DbContract.LikedHotelsColumns.KEY_ID + " INTEGER ," +
                DbContract.LikedHotelsColumns.CITY + " STRING , " +
                DbContract.LikedHotelsColumns.COUNTRY + " STRING , " +
                " PRIMARY KEY (" + DbContract.LikedHotelsColumns.KEY_ID + ") ) ";

        String CREATE_SEARCH_HISTORY_TABLE = "CREATE TABLE IF NOT EXISTS " + DbContract.Tables.TABLE_SEARCH_HISTORY + " ( " +
                DbContract.SearchHistoryColumns.LOCATION_NAME + " STRING ," +
                DbContract.SearchHistoryColumns.LAT + " STRING ," +
                DbContract.SearchHistoryColumns.LON + " STRING ," +
                DbContract.SearchHistoryColumns.NORTHEAST_LAT + " STRING," +
                DbContract.SearchHistoryColumns.NORTHEAST_LON + " STRING," +
                DbContract.SearchHistoryColumns.SOUTHWEST_LAT + " STRING," +
                DbContract.SearchHistoryColumns.SOUTHWEST_LON + " STRING," +
                DbContract.SearchHistoryColumns.TYPES + " STRING," +
                DbContract.SearchHistoryColumns.NUMBER_GUESTS + " STRING ," +
                DbContract.SearchHistoryColumns.NUMBER_ROOMS + " STRING ," +
                DbContract.SearchHistoryColumns.FROM_DATE + " STRING ," +
                DbContract.SearchHistoryColumns.TO_DATE + " STRING," +
                DbContract.SearchHistoryColumns.CREATE_AT + " TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                " PRIMARY KEY (" + DbContract.SearchHistoryColumns.LOCATION_NAME + "," + DbContract.SearchHistoryColumns.NUMBER_GUESTS + ","
                + DbContract.SearchHistoryColumns.NUMBER_ROOMS + "," + DbContract.SearchHistoryColumns.FROM_DATE + "," +
                DbContract.SearchHistoryColumns.TO_DATE + ")  )";

        String CREATE_BOOKINGS_TABLE = "CREATE TABLE IF NOT EXISTS " + DbContract.Tables.TABLE_BOOKINGS + " ( " +
                DbContract.BookingsColumns.ORDER_ID + " STRING , " +
                DbContract.BookingsColumns.ARRIVAL + " STRING , " +
                DbContract.BookingsColumns.CITY + " STRING , " +
                DbContract.BookingsColumns.COUNTRY + " STRING , " +
                DbContract.BookingsColumns.CURRENCY + " STRING , " +
                DbContract.BookingsColumns.DEPARTURE + " STRING , " +
                DbContract.BookingsColumns.HOTEL_NAME + " STRING , " +
                DbContract.BookingsColumns.IMAGE + " STRING , " +
                DbContract.BookingsColumns.ROOMS + " STRING , " +
                DbContract.BookingsColumns.IS_CANCELLED + " STRING , " +
                DbContract.BookingsColumns.RATE_NAME + " STRING , " +
                DbContract.BookingsColumns.REFERENCE + " STRING , " +
                DbContract.BookingsColumns.STARS + " STRING , " +
                DbContract.BookingsColumns.TOTAL_VALUE + " STRING , " +
                DbContract.BookingsColumns.RATE_ID + " STRING , " +
                DbContract.BookingsColumns.CONFIRMATION_ID + " STRING , " +
                " PRIMARY KEY (" + DbContract.BookingsColumns.ORDER_ID + ") ) ";
        // create Hotels table
        db.execSQL(CREATE_HOTEL_TABLE);
        db.execSQL(CREATE_SEARCH_HISTORY_TABLE);
        db.execSQL(CREATE_BOOKINGS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {

//            db.execSQL("DROP TABLE IF EXISTS " + DbContract.Tables.TABLE_LIKED_HOTELS);
//            db.execSQL("DROP TABLE IF EXISTS " + DbContract.Tables.TABLE_SEARCH_HISTORY);
//            db.execSQL("DROP TABLE IF EXISTS " + DbContract.Tables.TABLE_BOOKINGS);
            if (oldVersion <= 37) {
                db.execSQL("ALTER TABLE " + DbContract.Tables.TABLE_SEARCH_HISTORY + " ADD COLUMN " + DbContract.SearchHistoryColumns.NORTHEAST_LAT + " STRING");
                db.execSQL("ALTER TABLE " + DbContract.Tables.TABLE_SEARCH_HISTORY + " ADD COLUMN " + DbContract.SearchHistoryColumns.NORTHEAST_LON + " STRING");
                db.execSQL("ALTER TABLE " + DbContract.Tables.TABLE_SEARCH_HISTORY + " ADD COLUMN " + DbContract.SearchHistoryColumns.SOUTHWEST_LAT + " STRING");
                db.execSQL("ALTER TABLE " + DbContract.Tables.TABLE_SEARCH_HISTORY + " ADD COLUMN " + DbContract.SearchHistoryColumns.SOUTHWEST_LON + " STRING");
                db.execSQL("ALTER TABLE " + DbContract.Tables.TABLE_SEARCH_HISTORY + " ADD COLUMN " + DbContract.SearchHistoryColumns.TYPES + " STRING");
            }
        }
        this.onCreate(db);
    }

}