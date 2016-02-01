package com.etb.app.adapter;

import android.content.Context;
import android.support.annotation.ArrayRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.etb.app.R;

import java.util.Arrays;

/**
 * @author alex
 * @date 2015-05-04
 */
public class SpinnerAdapter extends ArrayAdapter<String> {

    public SpinnerAdapter(Context context, @ArrayRes int arrayRes) {
        super(context, R.layout.booking_spinner_item);
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        String[] items = context.getResources().getStringArray(arrayRes);
        addAll(Arrays.asList(items));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = super.getView(position, convertView, parent);
        TextView textView = ((TextView) v.findViewById(android.R.id.text1));
        if (position == getCount()) {
            textView.setText("");
            textView.setHint(getItem(position)); //"Hint to be displayed"
        }

        return v;
    }

    @Override
    public int getCount() {
        return super.getCount() - 1; // you don't display last item. It is used as hint.
    }
}
