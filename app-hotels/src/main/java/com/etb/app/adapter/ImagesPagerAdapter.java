package com.etb.app.adapter;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.etb.app.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * @author user
 * @date 2015-06-18
 */
public class ImagesPagerAdapter extends PagerAdapter {

    Context mContext;
    LayoutInflater mLayoutInflater;
    private ArrayList<String> mResources = new ArrayList<>();

    public ImagesPagerAdapter(Context context) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addItem(String url) {
        mResources.add(url);
    }

    public void addItems(ArrayList<String> urls) {
        mResources = urls;
    }


    @Override
    public int getCount() {
        if (mResources == null) {
            return 0;
        }
        return mResources.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView itemView = (ImageView) mLayoutInflater.inflate(R.layout.view_image, container, false);

        if (mResources.get(position) != null) {

            if (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT == mContext.getResources().getConfiguration().orientation) {
                Picasso.with(mContext)
                        .load(mResources.get(position))
                        .placeholder( R.drawable.progress_animation )
                        .resize(container.getMeasuredWidth(), (int) mContext.getResources().getDimension(R.dimen.hotel_summary_image_size))
                        .into(itemView);
            } else {
                Picasso.with(mContext)
                        .load(mResources.get(position))
                        .placeholder( R.drawable.progress_animation )
                        .into(itemView);
            }
        }
        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ImageView) object);
    }
}