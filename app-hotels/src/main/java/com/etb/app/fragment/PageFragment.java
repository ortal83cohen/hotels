package com.etb.app.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.tech.freak.wizardpager.model.Page;
import com.tech.freak.wizardpager.ui.PageFragmentCallbacks;

import java.security.InvalidParameterException;

/**
 * @author alex
 * @date 2015-05-04
 */
public abstract class PageFragment extends BaseFragment implements PageFragmentInterface.FormData {

    protected Page mPage;
    protected PageFragmentInterface mCallbacks;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPage = mCallbacks.onGetPage(getPageKey());
    }

    @Override
    public void savePageData() {
        if (mPage != null) {
            onPageDataSave(mPage.getData());
        }
    }

    abstract void onPageDataSave(Bundle pageData);

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (!(context instanceof PageFragmentCallbacks)) {
            throw new ClassCastException("Activity must implement PageFragmentCallbacks");
        }

        mCallbacks = (PageFragmentInterface) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mCallbacks.addPageFormInterface(this);
        return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try{
        savePageData();
        } catch (InvalidParameterException invalidParameterException) {
//            Toast.makeText(getActivity(), invalidParameterException.getMessage(), Toast.LENGTH_LONG).show();
        }
        mCallbacks.removePageFormInterface(this);
    }

}
