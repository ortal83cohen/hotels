package com.etb.app.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.DrawableRes;
import android.support.v4.util.LruCache;

import com.easytobook.api.model.Accommodation;
import com.etb.app.App;
import com.etb.app.R;
import com.etb.app.activity.BaseActivity;
import com.etb.app.preferences.UserPreferences;
import com.etb.app.utils.PriceRender;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * @author alex
 * @date 2015-04-20
 */
public class HotelMarker {
    public final static int STATUS_UNSEEN = 1;
    public final static int STATUS_SEEN = 2;
    public final static int STATUS_SELECTED = 3;
    private final LruCache<String, Bitmap> mMemoryCache;
    private final BaseActivity mContext;
    private final UserPreferences mUserPrefs;
    private final int mTextSize;
    private final int mStrokeSize;

    public HotelMarker(BaseActivity activity) {
        mContext = activity;
        mUserPrefs = App.provide(mContext).getUserPrefs();
        mTextSize = mContext.getResources().getDimensionPixelSize(R.dimen.size_text_secondary);
        mStrokeSize =  mContext.getResources().getDimensionPixelSize(R.dimen.maps_marker_padding);

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

    public MarkerOptions create(int pos, Accommodation acc, int status) {

        String currencyCode = mUserPrefs.getCurrencyCode();

        int price = (int) mContext.getPriceRender().price(acc, currencyCode);// show price on map without decimal


        Bitmap bitmap = drawTextToBitmap(mContext.getPriceRender().render(price), status);


        BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(bitmap);

        return new MarkerOptions()
                .position(new LatLng(acc.location.lat, acc.location.lon))
                .title(String.valueOf(pos))
                .icon(icon);
    }

    private Bitmap drawTextToBitmap(String text, int status) {
        Bitmap bitmap;
        int colorStroke;
        switch (status) {
            case STATUS_SELECTED:
                bitmap = decodeBitmapCached(R.drawable.map_pin_selected);
                return bitmap;

            case STATUS_UNSEEN:
                bitmap = decodeBitmapCached(R.drawable.map_pin_price);
                colorStroke = Color.WHITE;
                break;
            case STATUS_SEEN:
            default:
                bitmap = decodeBitmapCached(R.drawable.map_pin_price);
                colorStroke = Color.LTGRAY;
                break;
        }
        Bitmap.Config bitmapConfig =
                bitmap.getConfig();
        // set default bitmap config if none
        if (bitmapConfig == null) {
            bitmapConfig = Bitmap.Config.ARGB_8888;
        }

        // new antialiased Paint
        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        textPaint.setColor(colorStroke);
        // text size in pixels
        textPaint.setTextSize(mTextSize);
        // text shadow
        textPaint.setShadowLayer(1f, 0f, 1f, Color.WHITE);

        // draw text to the Canvas center
        Rect bounds = new Rect();
        textPaint.setTextAlign(Paint.Align.CENTER);
        float textWidth = textPaint.measureText(text, 0, text.length());

        bitmap = bitmap.copy(bitmapConfig, true);
        bitmap = Bitmap.createScaledBitmap(bitmap, (int) (textWidth + mStrokeSize), bitmap.getHeight(), false);
        Canvas canvas = new Canvas(bitmap);


        int xPos = (canvas.getWidth() / 2);
        int yPos = (int) (((canvas.getHeight() - canvas.getHeight() * 0.2) / 2) - ((textPaint.descent() + textPaint.ascent()) / 2));

        canvas.drawText(text, xPos, yPos, textPaint);

        return bitmap;
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
