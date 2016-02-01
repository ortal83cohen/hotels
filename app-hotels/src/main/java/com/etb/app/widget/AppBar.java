package com.etb.app.widget;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.AttributeSet;

import com.easytobook.api.model.DateRange;
import com.easytobook.api.model.SearchRequest;
import com.easytobook.api.model.search.Type;
import com.etb.app.R;
import com.etb.app.model.CurrentLocation;
import com.etb.app.model.LocationWithTitle;

/**
 * @author alex
 * @date 2015-06-17
 */
public class AppBar extends Toolbar {

    public AppBar(Context context) {
        this(context, null);
    }

    public AppBar(Context context, AttributeSet attrs) {
        this(context, attrs, android.support.v7.appcompat.R.attr.toolbarStyle);
    }

    public AppBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setLocation(SearchRequest request) {
        Type loc = request.getType();
        if (loc instanceof CurrentLocation) {
            setTitle(R.string.current_location);
        } else if (loc instanceof LocationWithTitle) {
            setTitle(((LocationWithTitle) loc).getTitle());
        }
        DateRange dr = request.getDateRange();
        String subtitle = DateUtils.formatDateRange(getContext(), dr.from.getTimeInMillis(), dr.to.getTimeInMillis(), 0);
        setSubtitle(subtitle);
    }

    public void showLogo() {
        super.setTitle("");
        setLogo(R.drawable.actionbar_logo);
    }

    @Override
    public void setTitle(int resId) {
        super.setTitle(resId);
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
    }
}
