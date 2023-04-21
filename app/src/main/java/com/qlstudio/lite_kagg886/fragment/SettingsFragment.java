package com.qlstudio.lite_kagg886.fragment;

import android.os.Bundle;
import android.text.InputType;
import android.widget.Toast;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import com.qlstudio.lite_kagg886.GlobalApplication;
import com.qlstudio.lite_kagg886.R;

import java.util.Objects;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        EditTextPreference cacheTime = Objects.requireNonNull(findPreference("setting_cache"));

        //只允许输入框输入数字
        cacheTime.setOnBindEditTextListener(editText -> {
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        });

        Preference update_button = findPreference("setting_update_now");
        //初始化
        update_button.setEnabled(Integer.parseInt(cacheTime.getText()) > 0);
        update_button.setOnPreferenceClickListener(v -> {
            GlobalApplication.getApplicationNoStatic().getPreferences().edit()
                    .putLong("cache_deadline_class", 0).apply();
            Toast.makeText(getContext(), "清空完毕!", Toast.LENGTH_SHORT).show();
            return false;
        });

        cacheTime.setOnPreferenceChangeListener((v, newValue) -> {
            update_button.setEnabled(Integer.parseInt(newValue.toString()) > 0);
            return true;
        });
    }
}