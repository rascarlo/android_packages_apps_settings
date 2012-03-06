
package com.android.settings.rascarlo;

import android.os.Bundle;
import android.preference.Preference;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class NavigationBarSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.navigation_bar_settings);
        }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        // TODO Auto-generated method stub
        return false;
    }
}
