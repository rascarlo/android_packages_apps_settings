package com.android.settings.rascarlo;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

public class StatusBarSettings extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String STATUS_BAR_GENERAL_CATEGORY = "status_bar_general_category";
    private static final String STATUS_BAR_BATTERY = "status_bar_battery";
    private static final String QUICK_SETTINGS_CATEGORY = "status_bar_quick_settings_category";
    private static final String QUICK_PULLDOWN = "status_bar_quick_pulldown";
    private static final String STATUS_BAR_BRIGHTNESS_CONTROL = "status_bar_brightness_control";

    private PreferenceCategory mStatusBarGeneralCategory;
    private ListPreference mStatusBarBattery;
    private PreferenceCategory mQuickSettingsCategory;
    private ListPreference mQuickPulldown;
    private CheckBoxPreference mStatusBarBrightnessControl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.status_bar_settings);

        // Battery
        mStatusBarGeneralCategory = (PreferenceCategory) findPreference(STATUS_BAR_GENERAL_CATEGORY);
        mStatusBarBattery = (ListPreference) getPreferenceScreen().findPreference(STATUS_BAR_BATTERY);
        int statusBarBattery = Settings.System.getInt(getActivity().
                getApplicationContext().getContentResolver(),
                Settings.System.STATUS_BAR_BATTERY, 0);
        mStatusBarBattery.setValue(String.valueOf(statusBarBattery));
        mStatusBarBattery.setSummary(mStatusBarBattery.getEntry());
        mStatusBarBattery.setOnPreferenceChangeListener(this);

        // Brightness control
        mStatusBarBrightnessControl = (CheckBoxPreference) getPreferenceScreen().findPreference(STATUS_BAR_BRIGHTNESS_CONTROL);
        if (!Utils.isPhone(getActivity())) {
                mStatusBarGeneralCategory.removePreference(mStatusBarBrightnessControl);
        } else {
            mStatusBarBrightnessControl.setChecked((Settings.System.getInt(getActivity().
                    getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_BRIGHTNESS_CONTROL, 0) == 1));
            try {
                if (Settings.System.getInt(getActivity().
                        getApplicationContext().getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                    mStatusBarBrightnessControl.setEnabled(false);
                    mStatusBarBrightnessControl
                            .setSummary(R.string.status_bar_toggle_info);
                }
            } catch (SettingNotFoundException e) {
            }
        }

        // Quick Settings category and pull down
        mQuickSettingsCategory = (PreferenceCategory) findPreference(QUICK_SETTINGS_CATEGORY);
        mQuickPulldown = (ListPreference) getPreferenceScreen().findPreference(
                QUICK_PULLDOWN);
        if (!Utils.isPhone(getActivity())) {
            if (mQuickPulldown != null)
                getPreferenceScreen().removePreference(mQuickPulldown);
            getPreferenceScreen().removePreference((PreferenceCategory) findPreference(QUICK_SETTINGS_CATEGORY));
        } else {
            mQuickPulldown.setOnPreferenceChangeListener(this);
            int quickPulldownValue = Settings.System.getInt(getActivity()
                    .getApplicationContext().getContentResolver(),
                    Settings.System.QS_QUICK_PULLDOWN, 0);
            mQuickPulldown.setValue(String.valueOf(quickPulldownValue));
            mQuickPulldown.setSummary(mQuickPulldown.getEntry());
        }
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {

        if (preference == mStatusBarBattery) {
            int statusBarBattery = Integer.valueOf((String) objValue);
            int indexBattery = mStatusBarBattery.findIndexOfValue((String) objValue);
            Settings.System.putInt(getActivity().getApplicationContext()
                    .getContentResolver(), Settings.System.STATUS_BAR_BATTERY, statusBarBattery);
            mStatusBarBattery.setSummary(mStatusBarBattery.getEntries()[indexBattery]);
            return true;
        } else if (preference == mQuickPulldown) {
            int quickPulldownValue = Integer.valueOf((String) objValue);
            int quickPulldownIndex = mQuickPulldown.findIndexOfValue((String) objValue);
            Settings.System.putInt(getActivity().getApplicationContext()
                    .getContentResolver(), Settings.System.QS_QUICK_PULLDOWN, quickPulldownValue);
            mQuickPulldown.setSummary(mQuickPulldown.getEntries()[quickPulldownIndex]);
            return true;
        }
        return false;
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            Preference preference) {
        boolean value;
        if (preference == mStatusBarBrightnessControl) {
            value = mStatusBarBrightnessControl.isChecked();
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_BRIGHTNESS_CONTROL, value ? 1 : 0);
            return true;
        }
        return false;
    }
}
