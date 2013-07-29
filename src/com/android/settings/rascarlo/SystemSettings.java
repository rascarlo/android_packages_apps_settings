
package com.android.settings.rascarlo;

import android.os.Bundle;
import android.preference.Preference;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class SystemSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {
    private static final String TAG = "SystemSettings";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.system_settings);
        }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        // TODO Auto-generated method stub
        return false;
    }
}
