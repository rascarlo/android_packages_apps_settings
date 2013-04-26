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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.view.VolumePanel;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class UserInterface extends SettingsPreferenceFragment implements OnPreferenceChangeListener {
    
    private static final String USER_INTERFACE_CATEGORY_GENERAL = "user_interface_category_general";

    private static final String DUAL_PANE_PREFS = "dual_pane_prefs";
    private static final String KEY_VOLUME_ADJUST_SOUNDS = "volume_adjust_sounds";
    private static final String KEY_VOLUME_OVERLAY = "volume_overlay";
    private static final String KEY_SAFE_HEADSET_VOLUME = "safe_headset_volume";
    private static final String KEY_WAKEUP_WHEN_PLUGGED_UNPLUGGED = "wakeup_when_plugged_unplugged";
    private static final String KEY_POWER_NOTIFICATIONS = "power_notifications";
    private static final String KEY_POWER_NOTIFICATIONS_VIBRATE = "power_notifications_vibrate";
    private static final String KEY_POWER_NOTIFICATIONS_RINGTONE = "power_notifications_ringtone";
    
    // Request code for power notification ringtone picker
    private static final int REQUEST_CODE_POWER_NOTIFICATIONS_RINGTONE = 1;

    // Used for power notification uri string if set to silent
    private static final String POWER_NOTIFICATIONS_SILENT_URI = "silent";

    private PreferenceCategory mUserInterfaceGeneral;
    private ListPreference mDualPanePrefs;
    private CheckBoxPreference mVolumeAdjustSounds;
    private ListPreference mVolumeOverlay;
    private CheckBoxPreference mSafeHeadsetVolume;
    private CheckBoxPreference mWakeUpWhenPluggedOrUnplugged;
    private CheckBoxPreference mPowerSounds;
    private CheckBoxPreference mPowerSoundsVibrate;
    private Preference mPowerSoundsRingtone;

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

        // Volume and Sound
        // Volume adjust sound
        mVolumeAdjustSounds = (CheckBoxPreference) findPreference(KEY_VOLUME_ADJUST_SOUNDS);
        mVolumeAdjustSounds.setPersistent(false);
        mVolumeAdjustSounds.setChecked(Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.VOLUME_ADJUST_SOUNDS_ENABLED, 1) != 0);
        
        // Volume panel
        mVolumeOverlay = (ListPreference) findPreference(KEY_VOLUME_OVERLAY);
        mVolumeOverlay.setOnPreferenceChangeListener(this);
        int volumeOverlay = Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.MODE_VOLUME_OVERLAY,
                VolumePanel.VOLUME_OVERLAY_EXPANDABLE);
        mVolumeOverlay.setValue(Integer.toString(volumeOverlay));
        mVolumeOverlay.setSummary(mVolumeOverlay.getEntry());
        
        // Safe headset volume
        mSafeHeadsetVolume = (CheckBoxPreference) findPreference(KEY_SAFE_HEADSET_VOLUME);
        mSafeHeadsetVolume.setPersistent(false);
        boolean safeMediaVolumeEnabled = getResources().getBoolean(
                com.android.internal.R.bool.config_safe_media_volume_enabled);
        mSafeHeadsetVolume.setChecked(Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.SAFE_HEADSET_VOLUME, safeMediaVolumeEnabled ? 1 : 0) != 0);
        
        // Plug-in prompt
        // Wake up plugged/unplugged
        mWakeUpWhenPluggedOrUnplugged = (CheckBoxPreference) findPreference(KEY_WAKEUP_WHEN_PLUGGED_UNPLUGGED);
        mWakeUpWhenPluggedOrUnplugged.setChecked(Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.WAKEUP_WHEN_PLUGGED_UNPLUGGED, 1) == 1);

        // power state change notification sounds
        mPowerSounds = (CheckBoxPreference) findPreference(KEY_POWER_NOTIFICATIONS);
        mPowerSounds.setChecked(Settings.Global.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.Global.POWER_NOTIFICATIONS_ENABLED, 0) != 0);
        mPowerSoundsVibrate = (CheckBoxPreference) findPreference(KEY_POWER_NOTIFICATIONS_VIBRATE);
        mPowerSoundsVibrate.setChecked(Settings.Global.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.Global.POWER_NOTIFICATIONS_VIBRATE, 0) != 0);
        
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator == null || !vibrator.hasVibrator()) {
            removePreference(KEY_POWER_NOTIFICATIONS_VIBRATE);
        }

        mPowerSoundsRingtone = findPreference(KEY_POWER_NOTIFICATIONS_RINGTONE);
        String currentPowerRingtonePath =
                Settings.Global.getString(getActivity().getApplicationContext().getContentResolver(),
                        Settings.Global.POWER_NOTIFICATIONS_RINGTONE);

        // set to default notification if we don't yet have one
        if (currentPowerRingtonePath == null) {
                currentPowerRingtonePath = Settings.System.DEFAULT_NOTIFICATION_URI.toString();
                Settings.Global.putString(getContentResolver(),
                        Settings.Global.POWER_NOTIFICATIONS_RINGTONE, currentPowerRingtonePath);
        }
        // is it silent ?
        if (currentPowerRingtonePath.equals(POWER_NOTIFICATIONS_SILENT_URI)) {
            mPowerSoundsRingtone.setSummary(
                    getString(R.string.power_notifications_ringtone_silent));
        } else {
            final Ringtone ringtone =
                    RingtoneManager.getRingtone(getActivity(), Uri.parse(currentPowerRingtonePath));
            if (ringtone != null) {
                mPowerSoundsRingtone.setSummary(ringtone.getTitle(getActivity()));
            }
        }
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
        if (preference == mVolumeAdjustSounds) {
            Settings.System.putInt(getContentResolver(), Settings.System.VOLUME_ADJUST_SOUNDS_ENABLED,
                    mVolumeAdjustSounds.isChecked() ? 1 : 0);
        } else if (preference == mSafeHeadsetVolume) {
            Settings.System.putInt(getContentResolver(), Settings.System.SAFE_HEADSET_VOLUME,
                    mSafeHeadsetVolume.isChecked() ? 1 : 0);
        } else if (preference == mWakeUpWhenPluggedOrUnplugged) {
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.WAKEUP_WHEN_PLUGGED_UNPLUGGED,
                        mWakeUpWhenPluggedOrUnplugged.isChecked() ? 1 : 0);
        } else if (preference == mPowerSounds) {
            Settings.Global.putInt(getContentResolver(),
                    Settings.Global.POWER_NOTIFICATIONS_ENABLED,
                    mPowerSounds.isChecked() ? 1 : 0);
        } else if (preference == mPowerSoundsVibrate) {
            Settings.Global.putInt(getContentResolver(),
                    Settings.Global.POWER_NOTIFICATIONS_VIBRATE,
                    mPowerSoundsVibrate.isChecked() ? 1 : 0);
        } else if (preference == mPowerSoundsRingtone) {
            launchNotificationSoundPicker(REQUEST_CODE_POWER_NOTIFICATIONS_RINGTONE,
                    Settings.Global.getString(getContentResolver(),
                            Settings.Global.POWER_NOTIFICATIONS_RINGTONE));
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
        } else if (preference == mVolumeOverlay) {
            final int value = Integer.valueOf((String) objValue);
            final int index = mVolumeOverlay.findIndexOfValue((String) objValue);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.MODE_VOLUME_OVERLAY, value);
            mVolumeOverlay.setSummary(mVolumeOverlay.getEntries()[index]);
            return true;
            }
        return false;
        }


    private void launchNotificationSoundPicker(int code, String currentPowerRingtonePath) {
        final Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);

        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,
                RingtoneManager.TYPE_NOTIFICATION);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI,
                Settings.System.DEFAULT_NOTIFICATION_URI);
        if (currentPowerRingtonePath != null &&
                !currentPowerRingtonePath.equals(POWER_NOTIFICATIONS_SILENT_URI)) {
            Uri uri = Uri.parse(currentPowerRingtonePath);
            if (uri != null) {
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, uri);
            }
        }
        startActivityForResult(intent, code);
    }

    private void setPowerNotificationRingtone(Intent intent) {
        final Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

        final String toneName;
        final String toneUriPath;

        if ( uri != null ) {
            final Ringtone ringtone = RingtoneManager.getRingtone(getActivity(), uri);
            toneName = ringtone.getTitle(getActivity());
            toneUriPath = uri.toString();
        } else {
            // silent
            toneName = getString(R.string.power_notifications_ringtone_silent);
            toneUriPath = POWER_NOTIFICATIONS_SILENT_URI;
        }

        mPowerSoundsRingtone.setSummary(toneName);
        Settings.Global.putString(getContentResolver(),
                Settings.Global.POWER_NOTIFICATIONS_RINGTONE, toneUriPath);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_POWER_NOTIFICATIONS_RINGTONE:
                if (resultCode == Activity.RESULT_OK) {
                    setPowerNotificationRingtone(data);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }
}