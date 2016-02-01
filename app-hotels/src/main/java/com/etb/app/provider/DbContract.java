package com.etb.app.provider;

import android.net.Uri;
import android.provider.BaseColumns;

import com.etb.app.BuildConfig;

import java.util.List;

/**
 * @author user
 * @date 2015-07-13
 */
public class DbContract {
    public static final String CONTENT_AUTHORITY = BuildConfig.APPLICATION_ID + ".provider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    private static final String PATH_LIKED_HOTELS = "favorites";
    private static final String PATH_SEARCH_HISTORY = "search_history";
    private static final String PATH_BOOKINGS = "bookings";

    interface Tables {
        String TABLE_LIKED_HOTELS = "liked_hotels";
        String TABLE_SEARCH_HISTORY = "search_history";
        String TABLE_BOOKINGS = "bookings";
    }

    public interface LikedHotelsColumns {
        String KEY_ID = "_id";
        String CITY = "city";
        String COUNTRY = "country";
    }

    public interface BookingsColumns {
        String ORDER_ID = "orderId";
        String REFERENCE = "reference";
        String CITY = "city";
        String COUNTRY = "country";
        String HOTEL_NAME = "hotelName";
        String STARS = "hotelStars";
        String IMAGE = "hotelImage";
        String ARRIVAL = "arrival";
        String DEPARTURE = "departure";
        String CONFIRMATION_ID = "confirmationId";
        String RATE_ID = "rateId";
        String ROOMS = "rooms";
        String RATE_NAME = "rateName";
        //        public List<Integer> tags;
        String CURRENCY = "currency";
        String TOTAL_VALUE = "totalValue";
        String IS_CANCELLED = "isCancelled";
    }

    public interface SearchHistoryColumns {
        String LOCATION_NAME = "location_name";
        String LAT = "lat";
        String LON = "lon";
        String NORTHEAST_LAT = "northeast_lat";
        String NORTHEAST_LON = "northeast_lon";
        String SOUTHWEST_LAT = "southwest_lat";
        String SOUTHWEST_LON = "southwest_lon";
        String TYPES = "types";
        String NUMBER_GUESTS = "number_guests";
        String NUMBER_ROOMS = "number_rooms";
        String FROM_DATE = "from_date";
        String TO_DATE = "to_date";
        String CREATE_AT = "created_at";
    }

    public static class LikedHotels implements LikedHotelsColumns, BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LIKED_HOTELS).build();
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/int";

        public static Uri buildHotelUri(String hotelId, String city, String country) {
            return CONTENT_URI.buildUpon().appendPath(hotelId).appendPath(city).appendPath(country).build();
        }

        public static String getHotelId(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static class SearchHistory implements SearchHistoryColumns, BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SEARCH_HISTORY).build();
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/int";

        public static Uri buildSearchHistoryUri(String location, String lat, String lon, String northeastLat, String northeastLon, String southwestLat, String southwestLon,
                                                String types, String numberGuests, String numberRooms, String fromDate, String toDate) {
            return CONTENT_URI.buildUpon().appendPath(location).appendPath(lat).appendPath(lon).appendPath(northeastLat).appendPath(northeastLon).appendPath(southwestLat).appendPath(southwestLon).appendPath(types).appendPath(numberGuests).appendPath(numberRooms).appendPath(fromDate).appendPath(toDate).build();
        }

        public static List<String> getSearchHistory(Uri uri) {
            return uri.getPathSegments();
        }
    }

    public static class Bookings implements BookingsColumns, BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_BOOKINGS).build();
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/int";

        public static Uri buildBookingsUri(String orderId, String reference, String city, String country, String hotelName, String hotelStars,
                                           String hotelImage, String arrival, String departure, String rooms, String rateName, String currency,
                                           String totalValue, String isCancelled) {
            return CONTENT_URI.buildUpon().appendPath(orderId).appendPath(reference).appendPath(city).appendPath(country).appendPath(hotelName).appendPath(hotelStars).appendPath(hotelImage)
                    .appendPath(arrival).appendPath(departure).appendPath(rooms).appendPath(rateName).appendPath(currency).appendPath(totalValue).appendPath(isCancelled).build();
        }

        public static List<String> getOrdersIds(Uri uri) {
            return uri.getPathSegments();
        }
    }

}
