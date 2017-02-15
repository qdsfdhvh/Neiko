package seiko.neiko.ui;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import seiko.neiko.R;
import seiko.neiko.app.SwipeLayoutBase;

/**
 * Created by Seiko on 2016/9/24. YiKu
 */

public class AboutActivity extends SwipeLayoutBase {

    @Override
    public int getLayoutId() {return R.layout.activity_about;}

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);
        }
    }

}
