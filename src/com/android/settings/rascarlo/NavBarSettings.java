/*
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

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class NavBarSettings extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String KEY_NAV_BUTTONS_HEIGHT = "nav_buttons_height";
    private static final String NAVIGATION_BUTTON_COLOR = "navigation_button_color";

    private ListPreference mNavButtonsHeight;
    private Preference mNavigationButtonColor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.navigation_bar_settings);

        mNavButtonsHeight = (ListPreference) findPreference(KEY_NAV_BUTTONS_HEIGHT);
        mNavButtonsHeight.setOnPreferenceChangeListener(this);
        int statusNavButtonsHeight = Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                 Settings.System.NAV_BUTTONS_HEIGHT, 48);
        mNavButtonsHeight.setValue(String.valueOf(statusNavButtonsHeight));
        mNavButtonsHeight.setSummary(mNavButtonsHeight.getEntry());
        
        mNavigationButtonColor = (Preference) getPreferenceScreen().findPreference(NAVIGATION_BUTTON_COLOR);
        }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mNavButtonsHeight) {
            int statusNavButtonsHeight = Integer.valueOf((String) objValue);
            int index = mNavButtonsHeight.findIndexOfValue((String) objValue);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.NAV_BUTTONS_HEIGHT, statusNavButtonsHeight);
            mNavButtonsHeight.setSummary(mNavButtonsHeight.getEntries()[index]);
            return true;
        }
        return true;
    }
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mNavigationButtonColor) {
            ColorPickerDialog mColorPicker = new ColorPickerDialog(getActivity(),
                    mButtonColorListener, Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.NAVIGATION_BUTTON_COLOR,
                    getActivity().getApplicationContext().getResources().getColor(
                    com.android.internal.R.color.transparent)));
            mColorPicker.setDefaultColor(0x00000000);
            mColorPicker.show();
            return true;
        }
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }
    
    ColorPickerDialog.OnColorChangedListener mButtonColorListener =
            new ColorPickerDialog.OnColorChangedListener() {
                public void colorChanged(int color) {
                    Settings.System.putInt(getContentResolver(),
                            Settings.System.NAVIGATION_BUTTON_COLOR, color);
                }
                public void colorUpdate(int color) {
                }
        };
}