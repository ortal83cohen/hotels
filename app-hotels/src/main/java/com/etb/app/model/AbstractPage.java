package com.etb.app.model;

import com.tech.freak.wizardpager.model.ModelCallbacks;
import com.tech.freak.wizardpager.model.Page;

/**
 * @author alex
 * @date 2015-11-19
 */
public abstract class AbstractPage extends Page {

    protected AbstractPage(ModelCallbacks callbacks, String title) {
        super(callbacks, title);
    }

    public abstract void onPageSelected();
}
