/*
 * Copyright (C) 2012 The CyanogenMod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.rascarlo;

import android.content.res.Resources;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class UserInterface extends SettingsPreferenceFragment implements OnPreferenceChangeListener {
    
    private static final String USER_INTERFACE_CATEGORY_GENERAL = "user_interface_category_general";
    private static final String USER_INTERFACE_CATEGORY_DISPLAY = "user_interface_category_display";

    private static final String DUAL_PANE_PREFS = "dual_pane_prefs";
    private static final String KEY_WAKEUP_WHEN_PLUGGED_UNPLUGGED = "wakeup_when_plugged_unplugged";

    private PreferenceCategory mUserInterfaceGeneral;
    private PreferenceCategory mUserInterfaceDisplay;
    private ListPreference mDualPanePrefs;
    private CheckBoxPreference mWakeUpWhenPluggedOrUnplugged;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.user_interface);

        PreferenceScreen prefSet = getPreferenceScreen();

        // General
        // Dual pane, only show on selected devices
        mUserInterfaceGeneral = (PreferenceCategory) prefSet.findPreference(USER_INTERFACE_CATEGORY_GENERAL);
        mDualPanePrefs = (ListPreference) prefSet.findPreference(DUAL_PANE_PREFS);
        if (mUserInterfaceGeneral != null) {
            if (!getResources().getBoolean(R.bool.config_show_user_interface_dual_pane)) {
                getPreferenceScreen().removePreference((PreferenceCategory) findPreference(USER_INTERFACE_CATEGORY_GENERAL));
                getPreferenceScreen().removePreference(mDualPanePrefs);
                mUserInterfaceGeneral = null;
            } else {
                mDualPanePrefs.setOnPreferenceChangeListener(this);
                int dualPanePrefsValue = Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                        Settings.System.DUAL_PANE_PREFS, 0);
                mDualPanePrefs.setValue(String.valueOf(dualPanePrefsValue));
                updateDualPanePrefs(dualPanePrefsValue);
            }
        }

        // Display
        // Wake up plugged/unplugged
        mUserInterfaceDisplay = (PreferenceCategory) prefSet.findPreference(USER_INTERFACE_CATEGORY_DISPLAY);
        mWakeUpWhenPluggedOrUnplugged = (CheckBoxPreference) findPreference(KEY_WAKEUP_WHEN_PLUGGED_UNPLUGGED);
        mWakeUpWhenPluggedOrUnplugged.setChecked(Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.WAKEUP_WHEN_PLUGGED_UNPLUGGED, 1) == 1);
        }

    private void updateDualPanePrefs(int value) {
        Resources res = getResources();
        if (value == 0) {
            /* dual pane deactivated */
            mDualPanePrefs.setSummary(res.getString(R.string.dual_pane_prefs_off));
        } else {
            String direction = res.getString(value == 2
                    ? R.string.dual_pane_prefs_landscape
                    : R.string.dual_pane_prefs_on);
            mDualPanePrefs.setSummary(res.getString(R.string.dual_pane_prefs_summary, direction));
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mWakeUpWhenPluggedOrUnplugged) {
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.WAKEUP_WHEN_PLUGGED_UNPLUGGED,
                    mWakeUpWhenPluggedOrUnplugged.isChecked() ? 1 : 0);
            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mDualPanePrefs) {
            int dualPanePrefsValue = Integer.valueOf((String) objValue);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.DUAL_PANE_PREFS, dualPanePrefsValue);
            updateDualPanePrefs(dualPanePrefsValue);
            getActivity().recreate();
            return true;
            }
        return false;
        }
}