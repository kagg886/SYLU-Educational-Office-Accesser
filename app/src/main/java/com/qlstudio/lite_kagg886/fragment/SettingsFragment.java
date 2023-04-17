package com.qlstudio.lite_kagg886.fragment;

import android.os.Bundle;
import androidx.preference.PreferenceFragmentCompat;
import com.qlstudio.lite_kagg886.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }
}