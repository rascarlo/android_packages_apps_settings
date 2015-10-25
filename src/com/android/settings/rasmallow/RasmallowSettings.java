
package com.android.settings.rasmallow;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.SwitchPreference;
import android.provider.Settings;

import com.android.internal.logging.MetricsLogger;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class RasmallowSettings extends SettingsPreferenceFragment
        implements OnPreferenceChangeListener {

    private static final String RAS_KILL_APP_LONG_PRESS_BACK_KEY = "ras_kill_app_long_press_back_key";
    private static final String RAS_VOLUME_KEYS_CURSOR_CONTROL = "ras_volume_keys_cursor_control";
    private SwitchPreference killAppLongPressBackKeySwitch;
    private ListPreference volumeKeysCursorControlListPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.rasmallow_settings);

        // kill app long press back key
        killAppLongPressBackKeySwitch = (SwitchPreference) findPreference(
                RAS_KILL_APP_LONG_PRESS_BACK_KEY);
        killAppLongPressBackKeySwitch.setOnPreferenceChangeListener(this);
        killAppLongPressBackKeySwitch.setChecked(Settings.Secure.getInt(getContentResolver(),
                RAS_KILL_APP_LONG_PRESS_BACK_KEY, 0) != 0);

        // volume keys cursor control
        volumeKeysCursorControlListPref = (ListPreference) findPreference(
                RAS_VOLUME_KEYS_CURSOR_CONTROL);
        if (volumeKeysCursorControlListPref != null) {
            volumeKeysCursorControlListPref.setOnPreferenceChangeListener(this);
            volumeKeysCursorControlListPref
                    .setValue(Integer.toString(Settings.System.getInt(getContentResolver(),
                            RAS_VOLUME_KEYS_CURSOR_CONTROL,
                            0)));
            volumeKeysCursorControlListPref.setSummary(volumeKeysCursorControlListPref.getEntry());
        }
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsLogger.APPLICATION;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        // kill app long press back key
        if (preference == killAppLongPressBackKeySwitch) {
            Settings.Secure.putInt(getContentResolver(), RAS_KILL_APP_LONG_PRESS_BACK_KEY,
                    (boolean) newValue ? 1 : 0);
            return true;

            // volume keys cursor control
        } else if (preference == volumeKeysCursorControlListPref) {
            int volumeKeyCursorControlValue = Integer.parseInt((String) newValue);
            Settings.System.putInt(getContentResolver(), RAS_VOLUME_KEYS_CURSOR_CONTROL,
                    volumeKeyCursorControlValue);
            int volumeKeyCursorControlIndex = volumeKeysCursorControlListPref
                    .findIndexOfValue((String) newValue);
            volumeKeysCursorControlListPref.setSummary(
                    volumeKeysCursorControlListPref.getEntries()[volumeKeyCursorControlIndex]);
            return true;
        }
        return false;
    }
}
