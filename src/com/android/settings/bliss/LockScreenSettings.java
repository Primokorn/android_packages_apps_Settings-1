/*
 * Copyright (C) 2013 SlimRoms
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

package com.android.settings.bliss;

import android.app.admin.DevicePolicyManager;
import android.os.Bundle;
import android.os.UserHandle;
import android.content.Context;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.UserHandle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.android.settings.util.Helpers;
import com.android.settings.Utils;

public class LockScreenSettings extends SettingsPreferenceFragment
        implements OnSharedPreferenceChangeListener,
        Preference.OnPreferenceChangeListener {

    private static final String TAG = "LockScreenSettings";

    private static final String KEY_LOCKSCREEN_CAMERA_WIDGET_HIDE = "camera_widget_hide";
    private static final String KEY_LOCKSCREEN_DIALER_WIDGET_HIDE = "dialer_widget_hide";
    private static final String KEY_LOCKSCREEN_WEATHER = "lockscreen_weather";

    private PreferenceScreen mLockScreen;
    private SwitchPreference mCameraWidgetHide;
    private SwitchPreference mDialerWidgetHide;
    private SwitchPreference mLockscreenWeather;

    private Context mContext;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.bliss_lockscreen_settings);

        PreferenceScreen prefSet = getPreferenceScreen();
        ContentResolver resolver = getActivity().getContentResolver();
        PackageManager pm = getPackageManager();
        Resources res = getResources();
        mContext = getActivity();

        mLockScreen = (PreferenceScreen) findPreference("lock_screen");

        // Camera widget hide
        mCameraWidgetHide = (SwitchPreference) findPreference("camera_widget_hide");
        boolean mCameraDisabled = false;
        DevicePolicyManager dpm =
            (DevicePolicyManager) getActivity().getSystemService(Context.DEVICE_POLICY_SERVICE);
        if (dpm != null) {
            mCameraDisabled = dpm.getCameraDisabled(null);
        }
        if (mCameraDisabled){
            mLockScreen.removePreference(mCameraWidgetHide);
        }

        // Dialer widget hide
        mDialerWidgetHide = (SwitchPreference) prefSet.findPreference(KEY_LOCKSCREEN_DIALER_WIDGET_HIDE);
        mDialerWidgetHide.setChecked(Settings.System.getIntForUser(resolver,
            Settings.System.DIALER_WIDGET_HIDE, 0, UserHandle.USER_CURRENT) == 1);
        mDialerWidgetHide.setOnPreferenceChangeListener(this);

        mDialerWidgetHide = (SwitchPreference) findPreference("dialer_widget_hide");
        if ((!Utils.isVoiceCapable(mContext) || Utils.isWifiOnly(mContext))) {
            mLockScreen.removePreference(mDialerWidgetHide);
        }
        
        // Lockscreen weather
        mLockscreenWeather = (SwitchPreference) findPreference(KEY_LOCKSCREEN_WEATHER);
        mLockscreenWeather.setChecked(Settings.System.getIntForUser(resolver,
                Settings.System.LOCKSCREEN_WEATHER, 1, UserHandle.USER_CURRENT) == 1);
        mLockscreenWeather.setOnPreferenceChangeListener(this);
        
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mDialerWidgetHide) {
            boolean value = (Boolean) objValue;
            Settings.System.putIntForUser(getActivity().getContentResolver(),
                    Settings.System.DIALER_WIDGET_HIDE, value ? 1 : 0, UserHandle.USER_CURRENT);
            Helpers.restartSystemUI();
        } else if (preference == mLockscreenWeather) {
            boolean value = (Boolean) objValue;
            Settings.System.putIntForUser(getActivity().getContentResolver(),
                    Settings.System.LOCKSCREEN_WEATHER, value ? 1 : 0, UserHandle.USER_CURRENT);
            Helpers.restartSystemUI();
        }            
        return false;
    }
}
