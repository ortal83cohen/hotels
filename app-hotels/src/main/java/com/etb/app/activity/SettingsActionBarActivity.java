package com.etb.app.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.etb.app.R;

import java.util.ArrayList;
import java.util.List;

abstract public class SettingsActionBarActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    protected ListView mListView;

    private PreferenceAdapter mPreferenceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();

        ArrayList<Preference> preferences = initPreferenceItems();

        mPreferenceAdapter = new PreferenceAdapter(this, preferences);
        mListView = (ListView) findViewById(android.R.id.list);
        mListView.setEmptyView(findViewById(android.R.id.empty));
        mListView.setAdapter(mPreferenceAdapter);
        mListView.setOnItemClickListener(this);
    }

    public PreferenceAdapter getAdapter() {
        return mPreferenceAdapter;
    }

    protected abstract void init();

    protected abstract ArrayList<Preference> initPreferenceItems();

    protected abstract void onPreferenceItemClick(int action, Item pref);

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Preference pref = (Preference) mListView.getItemAtPosition(position);
        if (pref instanceof Item) {
            int action = ((Item) pref).action;
            onPreferenceItemClick(action, (Item) pref);
        }
    }

    protected void notifyDataSetChanged() {
        mPreferenceAdapter.notifyDataSetChanged();
    }

    public static class Preference {
        final String title;
        final int titleRes;
        final int layout;

        public Preference(@StringRes int titleRes, @LayoutRes int layout) {
            this.layout = layout;
            this.title = null;
            this.titleRes = titleRes;
        }

        public Preference(String title, @LayoutRes int layout) {
            this.layout = layout;
            this.title = title;
            this.titleRes = 0;
        }
    }

    public static class Category extends Preference {
        public Category(int title) {
            super(title, R.layout.preference_category);
        }

        public Category(String title) {
            super(title, R.layout.preference_category);
        }
    }

    public static class Item extends Preference {
        final int action;
        public int summaryRes;
        public String summary;
        public int widget;
        public boolean enabled = true;

        public Item(@StringRes int titleRes, @StringRes int summaryRes, int action) {
            super(titleRes, R.layout.preference_material);
            this.summaryRes = summaryRes;
            this.action = action;
        }

        public Item(@StringRes int titleRes, int action) {
            super(titleRes, R.layout.preference_material);
            this.summaryRes = 0;
            this.action = action;
        }

        public Item(String title, @StringRes int summaryRes, int action) {
            super(title, R.layout.preference_material);
            this.summaryRes = summaryRes;
            this.action = action;
        }

        public Item(String title, String summary, int action) {
            super(title, R.layout.preference_material);
            this.summary = summary;
            this.action = action;
        }

        public Item(@StringRes int titleRes, String summary, int action) {
            super(titleRes, R.layout.preference_material);
            this.summary = summary;
            this.action = action;
        }
    }

    public static class CheckboxItem extends Item {
        public boolean checked = false;

        public CheckboxItem(int title, int summaryRes, int action) {
            super(title, summaryRes, action);
            this.widget = R.layout.preference_widget_checkbox;
        }
    }

    public static class SwitchItem extends Item {
        public boolean checked;

        public SwitchItem(int title, boolean checked, int action) {
            super(title, action);
            this.widget = R.layout.preference_switcher;
            this.checked = checked;
        }
    }

    static class PreferenceAdapter extends ArrayAdapter<Preference> {

        private final SettingsActionBarActivity mActivity;
        private final LayoutInflater mInflater;

        public PreferenceAdapter(SettingsActionBarActivity activity, List<Preference> objects) {
            super(activity, 0, objects);
            mActivity = activity;
            mInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getItemViewType(int position) {
            Preference pref = getItem(position);
            if (pref instanceof Category) {
                return 0;
            }
            return 1;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Preference pref = getItem(position);

            View view;
            if (convertView == null) {
                view = mInflater.inflate(pref.layout, parent, false);
            } else {
                view = convertView;
            }

            TextView title = (TextView) view.findViewById(android.R.id.title);
            if (pref.titleRes > 0) {
                title.setText(pref.titleRes);
            } else {
                title.setText(pref.title);
            }

            if (pref instanceof Item) {
                Item item = (Item) pref;
                View icon = view.findViewById(android.R.id.icon);
                icon.setVisibility(View.GONE);

                TextView summary = (TextView) view.findViewById(android.R.id.summary);
                if (item.summaryRes > 0) {
                    summary.setText(item.summaryRes);
                    summary.setVisibility(View.VISIBLE);
                } else if (!TextUtils.isEmpty(item.summary)) {
                    summary.setText(item.summary);
                    summary.setVisibility(View.VISIBLE);
                } else {
                    summary.setVisibility(View.GONE);
                }

                final ViewGroup widgetFrame = (ViewGroup) view.findViewById(android.R.id.widget_frame);
                final View iconFrame = view.findViewById(R.id.icon_frame);
                iconFrame.setVisibility(View.GONE);
                if (item.widget > 0) {

                    if (item instanceof CheckboxItem) {
                        CheckBox checkBox = (CheckBox) widgetFrame.findViewById(android.R.id.checkbox);
                        if (checkBox == null) {
                            mInflater.inflate(item.widget, widgetFrame);
                            checkBox = (CheckBox) widgetFrame.findViewById(android.R.id.checkbox);
                        }
                        checkBox.setChecked(((CheckboxItem) item).checked);
                    }

                    if (item instanceof SwitchItem) {
                        Switch switcher = (Switch) widgetFrame.findViewById(R.id.switch1);
                        if (switcher == null) {
                            mInflater.inflate(item.widget, widgetFrame);
                            switcher = (Switch) widgetFrame.findViewById(R.id.switch1);
                        }
                        switcher.setChecked(((SwitchItem) item).checked);
                    }
                } else {
                    widgetFrame.setVisibility(View.GONE);
                }
            }

            return view;
        }

        @Override
        public boolean isEnabled(int position) {
            final Preference pref = getItem(position);
            return !(pref instanceof Category) && (!(pref instanceof Item) || ((Item) pref).enabled);
        }
    }

}
