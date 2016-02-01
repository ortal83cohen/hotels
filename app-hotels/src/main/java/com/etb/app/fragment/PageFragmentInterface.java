package com.etb.app.fragment;

import com.tech.freak.wizardpager.ui.PageFragmentCallbacks;

/**
 * @author alex
 * @date 2015-05-04
 */
public interface PageFragmentInterface extends PageFragmentCallbacks {

    void addPageFormInterface(FormData form);

    void removePageFormInterface(FormData form);

    interface FormData {
        String getPageKey();

        void savePageData();
    }
}
