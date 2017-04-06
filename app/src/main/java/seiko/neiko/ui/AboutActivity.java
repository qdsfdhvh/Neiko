package seiko.neiko.ui;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import seiko.neiko.R;
import seiko.neiko.app.BaseSwipeLayout;

/**
 * Created by Seiko on 2016/9/24. YiKu
 */

public class AboutActivity extends BaseSwipeLayout {

    @Override
    public int getLayoutId() {return R.layout.activity_about;}

    @Override
    public void initViews(Bundle bundle) {
        shiftView2(new SettingsFragment());
    }

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);
        }
    }


}
