package com.qlstudio.lite_kagg886;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import com.kagg886.jxw_collector.protocol.SyluSession;

import java.lang.reflect.Method;

/**
 * @projectName: 掌上沈理青春版
 * @package: com.qlstudio.lite_kagg886
 * @className: GlobalApplication
 * @author: kagg886
 * @description: 应用实例
 * @date: 2023/4/13 21:17
 * @version: 1.0
 */
public class GlobalApplication extends Application {

    private SyluSession session; //API接口

    private SharedPreferences preferences; //本地管理器

    @SuppressLint("PrivateApi")
    public static GlobalApplication getApplicationNoStatic() {
        Application application = null;
        try {
            Class<?> atClass = Class.forName("android.app.ActivityThread");
            Method currentApplicationMethod = atClass.getDeclaredMethod("currentApplication");
            currentApplicationMethod.setAccessible(true);
            application = (Application) currentApplicationMethod.invoke(null);
        } catch (Exception ignored) {
        }
        if (application != null) return (GlobalApplication) application;
        try {
            Class<?> atClass = Class.forName("android.app.AppGlobals");
            Method currentApplicationMethod = atClass.getDeclaredMethod("getInitialApplication");
            currentApplicationMethod.setAccessible(true);
            application = (Application) currentApplicationMethod.invoke(null);
        } catch (Exception ignored) {
        }
        return (GlobalApplication) application;
    }

    public SyluSession getSession() {
        return session;
    }

    public void setSession(SyluSession session) {
        this.session = session;
    }

    public SharedPreferences getPreferences() {
        return preferences;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        session = new SyluSession();
        if (preferences.getString("user", null) != null) {
            session.setUser(preferences.getString("user", null));
        }
    }
}
