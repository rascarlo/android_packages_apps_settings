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

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.view.IWindowManager;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

public class SystemSettings extends SettingsPreferenceFragment {
    private static final String TAG = "SystemSettings";

    private static final String KEY_NAVIGATION_BAR = "navigation_bar";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.system_settings);

        // Only show the navigation bar config on phones that has a navigation bar
        boolean removeKeys = false;
        boolean removeNavbar = false;
        IWindowManager windowManager = IWindowManager.Stub.asInterface(
                ServiceManager.getService(Context.WINDOW_SERVICE));
        try {
            if (windowManager.hasNavigationBar()) {
                removeKeys = true;
                if (Utils.isTablet(getActivity())) {
                    removeNavbar = true;
                }
            } else {
                removeNavbar = true;
            }
        } catch (RemoteException e) {
            // Do nothing
        }

        if (removeNavbar) {
            getPreferenceScreen().removePreference(findPreference(KEY_NAVIGATION_BAR));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
