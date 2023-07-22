package com.qlstudio.lite_kagg886.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Toast;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import com.qlstudio.lite_kagg886.GlobalApplication;
import com.qlstudio.lite_kagg886.R;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.util.Objects;

public class SettingsFragment extends PreferenceFragmentCompat {

    private File[] wantToDel;

    private FileFilter filter = pathname -> !pathname.getAbsolutePath().equals(GlobalApplication.getApplicationNoStatic().getCatcher().getFile().getAbsolutePath());

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        wantToDel = new File[]{
                getContext().getCacheDir(),
                getContext().getFilesDir() //兼容旧版本
        };

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

        findPreference("setting_test_crash").setOnPreferenceClickListener(preference -> {
            throw new RuntimeException("这玩意是测试用的，不许截图，我不处理");
        });
        Preference log = findPreference("setting_remove_log");
        attachSpace(log);
        log.setOnPreferenceClickListener((preference -> {
            for (File file : wantToDel) {
                delFile(file);
            }
            attachSpace(log);
            Toast.makeText(getContext(), "删除完成~", Toast.LENGTH_SHORT).show();
            return true;
        }));
    }

    @SuppressLint("DefaultLocale")
    public void attachSpace(Preference log) {
        long space = 0;
        for (File f : wantToDel) {
            try {
                space += getFileSize(f);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        log.setSummary(String.format("共发现%.2fMB缓存", ((double) space) / 1024 / 1024));
    }

    public void delFile(File f) {
        if (f.exists()) {
            if (f.isDirectory()) {
                for (File file : f.listFiles(filter)) {
                    delFile(file);
                    file.delete(); //先删除子文件，再删除父文件夹
                }
            } else {
                f.delete();
            }
        }
    }

    public static long getFileSize(File f) throws FileNotFoundException {
        if (f.exists()) {
            if (f.isDirectory()) {
                //获取文件夹的文件的集合
                File[] files = f.listFiles();
                long count = 0;//用来保存文件的长度
                for (File file1 : files) {//遍历文件集合
                    if (file1.isFile()) {//如果是文件
                        count += file1.length();//计算文件的长度
                    } else {
                        count += getFileSize(file1);//递归调用
                    }
                }
                return count;
            } else {
                return f.length();
            }
        } else {
            throw new FileNotFoundException(f.getAbsolutePath());
        }
    }
}