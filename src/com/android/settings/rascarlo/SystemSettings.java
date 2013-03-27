/*
 * Copyright (C) 2012 The CyanogenMod project
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
import android.os.UserHandle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

public class SystemSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {
    private static final String TAG = "SystemSettings";

    private static final String KEY_PIE_CONTROL = "pie_control";
    private static final String KEY_STATUS_BAR = "status_bar";
    private static final String KEY_NAVIGATION_BAR = "navigation_bar";
    private static final String KEY_LOCKSCREEN_TARGETS = "lockscreen_targets";
    private static final String KEY_VOLUME_ROCKER_SETTINGS = "volume_rocker_settings";
    private static final String KEY_NOTIFICATION_PULSE_CATEGORY = "category_notification_pulse";
    private static final String KEY_NOTIFICATION_PULSE = "notification_pulse";
    private static final String QUICK_SETTINGS_CATEGORY = "quick_settings_category";
    private static final String QUICK_PULLDOWN = "quick_pulldown";

    private PreferenceScreen mPieControl;
    private PreferenceScreen mNotificationPulse;
    private PreferenceCategory mQuickSettingsCategory;
    private ListPreference mQuickPulldown;
    private boolean mPrimaryUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.system_settings);
        
        // USER_OWNER is logged in
        mPrimaryUser = UserHandle.myUserId() == UserHandle.USER_OWNER;
        if (mPrimaryUser) {
            // do nothing, show all settings
        } else {
            // NON USER_OWNER is logged in
            // remove non multi-user compatible settings
            getPreferenceScreen().removePreference(findPreference(KEY_STATUS_BAR));
            getPreferenceScreen().removePreference(findPreference(KEY_NAVIGATION_BAR));
        }

        // Pie controls
        mPieControl = (PreferenceScreen) findPreference(KEY_PIE_CONTROL);

        // Notification lights
        mNotificationPulse = (PreferenceScreen) findPreference(KEY_NOTIFICATION_PULSE);
        if (mNotificationPulse != null) {
            if (!getResources().getBoolean(com.android.internal.R.bool.config_intrusiveNotificationLed)) {
                getPreferenceScreen().removePreference(mNotificationPulse);
                getPreferenceScreen().removePreference((PreferenceCategory) findPreference(KEY_NOTIFICATION_PULSE_CATEGORY));
                mNotificationPulse = null;
            }
        }

        // Quick Settings category and pull down. Only show on phones
        mQuickSettingsCategory = (PreferenceCategory) getPreferenceScreen().findPreference(QUICK_SETTINGS_CATEGORY);
        mQuickPulldown = (ListPreference) getPreferenceScreen().findPreference(QUICK_PULLDOWN);
        if (!Utils.isPhone(getActivity())) {
            if(mQuickPulldown != null)
                getPreferenceScreen().removePreference(mQuickPulldown);
                getPreferenceScreen().removePreference((PreferenceCategory) findPreference(QUICK_SETTINGS_CATEGORY));
            } else {
                mQuickPulldown.setOnPreferenceChangeListener(this);
                int quickPulldownValue = Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                        Settings.System.QS_QUICK_PULLDOWN, 0);
                mQuickPulldown.setValue(String.valueOf(quickPulldownValue));
                updatePulldownSummary(quickPulldownValue);
            }
        }

    private void updatePieControlDescription() {
        if (Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.PIE_CONTROLS, 0) == 1) {
            mPieControl.setSummary(getString(R.string.pie_control_enabled));
        } else {
            mPieControl.setSummary(getString(R.string.pie_control_disabled));
        }
    }

    private void updateLightPulseDescription() {
        if (Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.NOTIFICATION_LIGHT_PULSE, 0) == 1) {
            mNotificationPulse.setSummary(getString(R.string.notification_light_enabled));
        } else {
            mNotificationPulse.setSummary(getString(R.string.notification_light_disabled));
        }
    }

    private void updatePulldownSummary(int value) {
        Resources res = getResources();
        if (value == 0) {
            /* quick pulldown deactivated */
            mQuickPulldown.setSummary(res.getString(R.string.quick_pulldown_off));
        } else {
            String direction = res.getString(value == 2
                    ? R.string.quick_pulldown_summary_left
                    : R.string.quick_pulldown_summary_right);
            mQuickPulldown.setSummary(res.getString(R.string.summary_quick_pulldown, direction));
        }
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mQuickPulldown) {
                int quickPulldownValue = Integer.valueOf((String) objValue);
                Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                        Settings.System.QS_QUICK_PULLDOWN, quickPulldownValue);
                updatePulldownSummary(quickPulldownValue);
                return true;
                }
        return false;
        }

    @Override
    public void onResume() {
        super.onResume();
        if (mNotificationPulse != null) {
            updateLightPulseDescription();
        }
        if (mPieControl != null) {
            updatePieControlDescription();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
