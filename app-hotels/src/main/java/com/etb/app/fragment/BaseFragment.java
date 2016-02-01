package com.etb.app.fragment;

import android.support.v4.app.Fragment;

import com.etb.app.App;
import com.etb.app.activity.BaseActivity;
import com.etb.app.model.HotelListRequest;
import com.etb.app.preferences.UserPreferences;
import com.etb.app.utils.PriceRender;

/**
 * @author alex
 * @date 2015-07-01
 */
public abstract class BaseFragment extends Fragment {

    public UserPreferences getUserPrefs() {
        return App.provide(getActivity()).getUserPrefs();
    }

    public String getCurrencyCode() {
        return getUserPrefs().getCurrencyCode();
    }

    public PriceRender getPriceRender() {
        return ((BaseActivity)getActivity()).getPriceRender();
    }

    public HotelListRequest getHotelsRequest() {
        return ((BaseActivity)getActivity()).getHotelsRequest();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        RefWatcher refWatcher = HotelsApplication.get(getActivity()).getRefWatcher();
//        refWatcher.watch(this);
    }
}
