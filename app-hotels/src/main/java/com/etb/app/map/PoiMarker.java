package com.etb.app.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.annotation.DrawableRes;
import android.support.v4.util.LruCache;

import com.easytobook.api.model.search.Poi;
import com.etb.app.R;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * @author alex
 * @date 2015-04-20
 */
public class PoiMarker {
    public final static int STATUS_UNSEEN = 1;
    public final static int STATUS_SELECTED = 3;
    public static final int TYPE_AIRPORTS = 1;
    public static final int TYPE_TRAIN_STATION = 2;
    public static final int TYPE_EVENT_CENTERS = 3;
    public static final int TYPE_SHOPPING = 4;
    public static final int TYPE_ATTRACTIONS = 5;
    public static final int TYPE_MUSEUMS = 6;
    public static final int TYPE_MONUMENTS = 7;
    public static final int TYPE_LANDMARKS = 8;
    public static final int TYPE_CHURCHES = 9;
    public static final int TYPE_AMUSEMENT_PARK = 10;
    public static final int TYPE_BEACHES = 11;
    public static final int TYPE_HOSPITALS = 12;
    public static final int TYPE_UNIVERSITIES = 13;
    public static final int TYPE_MONUMENTAL_SITES = 14;
    public static final int TYPE_RELIGIOUS_SITES = 15;
    public static final int TYPE_STADIUM = 16;
    public static final int TYPE_EVENTS = 17;
    public static final int TYPE_DISTRICT = 18;
    public static final int TYPE_AREA = 19;
    public static final int[] TYPES = new int[]{
            PoiMarker.TYPE_AIRPORTS,
            PoiMarker.TYPE_TRAIN_STATION,
            PoiMarker.TYPE_EVENT_CENTERS,
            PoiMarker.TYPE_SHOPPING,
            PoiMarker.TYPE_ATTRACTIONS,
            PoiMarker.TYPE_MUSEUMS,
            PoiMarker.TYPE_MONUMENTS,
            PoiMarker.TYPE_LANDMARKS,
            PoiMarker.TYPE_CHURCHES,
            PoiMarker.TYPE_AMUSEMENT_PARK,
            PoiMarker.TYPE_BEACHES,
            PoiMarker.TYPE_HOSPITALS,
            PoiMarker.TYPE_UNIVERSITIES,
            PoiMarker.TYPE_MONUMENTAL_SITES,
            PoiMarker.TYPE_RELIGIOUS_SITES,
            PoiMarker.TYPE_STADIUM,
            PoiMarker.TYPE_EVENTS,
            PoiMarker.TYPE_DISTRICT,
            PoiMarker.TYPE_AREA
    };

    private final LruCache<String, Bitmap> mMemoryCache;
    private final Context mContext;

    private final int mTextSize;
    private final int mStrokeSize;

    public PoiMarker(Context context) {
        mContext = context;
        mTextSize = context.getResources().getDimensionPixelSize(R.dimen.size_text_small);
        mStrokeSize = context.getResources().getDimensionPixelSize(R.dimen.maps_marker_padding);

        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public static int getImageByType(int typeId) {
        switch (typeId) {
            case TYPE_AIRPORTS:
                return R.drawable.icon_poi_category_airports;  // "Airports"
            case TYPE_TRAIN_STATION:
                return R.drawable.icon_poi_category_transportation;  //"Train Stations"
            case TYPE_EVENT_CENTERS:
                return R.drawable.icon_poi_event;  //"Conference centers / Event Centers"
            case TYPE_SHOPPING:
                return R.drawable.icon_poi_category_shopping;  //"Shopping"
            case TYPE_ATTRACTIONS:
                return R.drawable.icon_poi_category_attractions;  //"Attractions"
            case TYPE_MUSEUMS:
                return R.drawable.icon_poi_category_museums;  //"Museums"
            case TYPE_MONUMENTS:
                return R.drawable.icon_poi_monuments;  //"Monuments"
            case TYPE_LANDMARKS:
                return R.drawable.icon_poi_category_landmarks;  //"Landmarks"
            case TYPE_CHURCHES:
                return R.drawable.icon_poi_church;  //"Churches"
            case TYPE_AMUSEMENT_PARK:
                return R.drawable.icon_poi_event;  //"Amusement Parks"
            case TYPE_BEACHES:
                return R.drawable.icon_poi_beach;   // "Beaches"
            case TYPE_HOSPITALS:
                return R.drawable.icon_poi_hospital;   //"Hospitals"
            case TYPE_UNIVERSITIES:
                return R.drawable.icon_poi_university;  //"Universities"
            case TYPE_MONUMENTAL_SITES:
                return R.drawable.icon_poi_monuments;  //"Monumental Sites"
            case TYPE_RELIGIOUS_SITES:
                return R.drawable.icon_poi_church;  //"Religious Sites"
            case TYPE_STADIUM:
                return R.drawable.icon_poi_stadium;  // "Stadium"
            case TYPE_EVENTS:
                return R.drawable.icon_poi_event;  // "Events"
//            case 18:
//                return R.drawable.icon_poi_category;  //"Districts"
//            case 19:
//                return R.drawable.icon_poi_category;  //"Area"
            default:
                return 0;
        }
    }

    public static int getSelectedImageByType(int typeId) {
        switch (typeId) {
            case TYPE_AIRPORTS:
                return R.drawable.icon_poi_category_airports_active;  // "Airports"
            case TYPE_TRAIN_STATION:
                return R.drawable.icon_poi_category_transportation_active;  //"Train Stations"
            case TYPE_EVENT_CENTERS:
                return R.drawable.icon_poi_event_active;  //"Conference centers / Event Centers"
            case TYPE_SHOPPING:
                return R.drawable.icon_poi_category_shopping_active;  //"Shopping"
            case TYPE_ATTRACTIONS:
                return R.drawable.icon_poi_category_attractions_active;  //"Attractions"
            case TYPE_MUSEUMS:
                return R.drawable.icon_poi_category_museums_active;  //"Museums"
            case TYPE_MONUMENTS:
                return R.drawable.icon_poi_monuments_active;  //"Monuments"
            case TYPE_LANDMARKS:
                return R.drawable.icon_poi_category_landmarks_active;  //"Landmarks"
            case TYPE_CHURCHES:
                return R.drawable.icon_poi_church_active;  //"Churches"
            case TYPE_AMUSEMENT_PARK:
                return R.drawable.icon_poi_event_active;  //"Amusement Parks"
            case TYPE_BEACHES:
                return R.drawable.icon_poi_beach_active;   // "Beaches"
            case TYPE_HOSPITALS:
                return R.drawable.icon_poi_hospital_active;   //"Hospitals"
            case TYPE_UNIVERSITIES:
                return R.drawable.icon_poi_university_active;  //"Universities"
            case TYPE_MONUMENTAL_SITES:
                return R.drawable.icon_poi_monuments_active;  //"Monumental Sites"
            case TYPE_RELIGIOUS_SITES:
                return R.drawable.icon_poi_church_active;  //"Religious Sites"
            case TYPE_STADIUM:
                return R.drawable.icon_poi_stadium_active;  // "Stadium"
            case TYPE_EVENTS:
                return R.drawable.icon_poi_event_active;  // "Events"
//            case 18:
//                return R.drawable.icon_poi_category;  //"Districts"
//            case 19:
//                return R.drawable.icon_poi_category;  //"Area"
            default:
                return 0;
        }
    }

    public static String getNameByType(int typeId) {
        switch (typeId) {
            case TYPE_AIRPORTS:
                return "Airports";
            case TYPE_TRAIN_STATION:
                return "Train Stations";
            case TYPE_EVENT_CENTERS:
                return "Conference centers / Event Centers";
            case TYPE_SHOPPING:
                return "Shopping";
            case TYPE_ATTRACTIONS:
                return "Attractions";
            case TYPE_MUSEUMS:
                return "Museums";
            case TYPE_MONUMENTS:
                return "Monuments";
            case TYPE_LANDMARKS:
                return "Landmarks";
            case TYPE_CHURCHES:
                return "Churches";
            case TYPE_AMUSEMENT_PARK:
                return "Amusement Parks";
            case TYPE_BEACHES:
                return "Beaches";
            case TYPE_HOSPITALS:
                return "Hospitals";
            case TYPE_UNIVERSITIES:
                return "Universities";
            case TYPE_MONUMENTAL_SITES:
                return "Monumental Sites";
            case TYPE_RELIGIOUS_SITES:
                return "Religious Sites";
            case TYPE_STADIUM:
                return "Stadium";
            case TYPE_EVENTS:
                return "Events";
            case 18:
                return "Districts";
            case 19:
                return "Area";
            default:
                return "";
        }
    }

    public MarkerOptions create(int pos, Poi poi, int status) {

        Bitmap bitmap = drawTextToBitmap(poi.getTitle(), poi.getType_id(), status);


        BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(bitmap);

        return new MarkerOptions()
                .position(new LatLng(poi.lat, poi.lon))
                .snippet(getNameByType(poi.type_id))
                .title(poi.getTitle())
                .icon(icon);
    }

    private Bitmap drawTextToBitmap(String text, int typeId, int status) {
        Bitmap bitmap;
        switch (status) {
            case STATUS_UNSEEN:
                bitmap = decodeBitmapCached(R.drawable.map_pin_white);
                if (getImageByType(typeId) != 0) {
                    return overlay(bitmap, decodeBitmapCached(getImageByType(typeId)), true);
                }
                return bitmap;
            case STATUS_SELECTED:
            default:
                bitmap = decodeBitmapCached(R.drawable.map_pin_long_white);

                Bitmap.Config bitmapConfig =
                        bitmap.getConfig();
                // set default bitmap config if none
                if (bitmapConfig == null) {
                    bitmapConfig = Bitmap.Config.ARGB_8888;
                }

                // new antialiased Paint
                Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

                textPaint.setColor(Color.BLACK);
                // text size in pixels
                textPaint.setTextSize(mTextSize);
                // text shadow
                textPaint.setShadowLayer(1f, 0f, 1f, Color.BLACK);

                bitmap = bitmap.copy(bitmapConfig, true);
                textPaint.setTextAlign(Paint.Align.CENTER);
                float textWidth = textPaint.measureText(text, 0, text.length());
                int xPos;
                Canvas canvas;
                if (getImageByType(typeId) == 0) {
                    bitmap = Bitmap.createScaledBitmap(bitmap, (int) (textWidth + mStrokeSize), bitmap.getHeight(), false);
                    canvas = new Canvas(bitmap);
                    xPos = (canvas.getWidth() / 2);

                } else {
                    bitmap = overlay(Bitmap.createScaledBitmap(bitmap, (int) (textWidth + mStrokeSize + 50), bitmap.getHeight(), false), decodeBitmapCached(getImageByType(typeId)), false);
                    canvas = new Canvas(bitmap);
                    xPos = (canvas.getWidth() / 2) + 40;
                }
                int yPos = (int) (((canvas.getHeight() - canvas.getHeight() * 0.2) / 2) - ((textPaint.descent() + textPaint.ascent()) / 2));

                canvas.drawText(text, xPos, yPos, textPaint);

                return bitmap;
        }
    }

    private Bitmap overlay(Bitmap bitmap1, Bitmap bitmap2, boolean center) {
        int bitmap1Width = bitmap1.getWidth();
        int bitmap1Height = bitmap1.getHeight();
        int bitmap2Width = bitmap2.getWidth();
        int bitmap2Height = bitmap2.getHeight();
        float marginLeft = 16;
        float marginTop = (float) (bitmap1Height * 0.4 - bitmap2Height * 0.5);
        if (center) {
            marginLeft = (float) (bitmap1Width * 0.5 - bitmap2Width * 0.5);
        }
        Bitmap overlayBitmap = Bitmap.createBitmap(bitmap1Width, bitmap1Height, bitmap1.getConfig());
        Canvas canvas = new Canvas(overlayBitmap);
        canvas.drawBitmap(bitmap1, new Matrix(), null);
        canvas.drawBitmap(bitmap2, marginLeft, marginTop, null);
        return overlayBitmap;
    }

    private Bitmap decodeBitmapCached(@DrawableRes int drawableRes) {
        final String imageKey = String.valueOf(drawableRes);

        Bitmap bitmap = getBitmapFromMemCache(imageKey);
        if (bitmap == null) {
            bitmap = BitmapFactory.decodeResource(mContext.getResources(), drawableRes);
            addBitmapToMemoryCache(String.valueOf(imageKey), bitmap);
            return bitmap;
        }
        return bitmap;
    }

    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    private Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }
}
