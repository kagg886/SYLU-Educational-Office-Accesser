package com.qlstudio.lite_kagg886;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import com.kagg886.jxw_collector.exceptions.OfflineException;
import com.kagg886.jxw_collector.protocol.SyluSession;
import com.qlstudio.lite_kagg886.activity.ErrorActivity;
import com.qlstudio.lite_kagg886.activity.LoginActivity;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @projectName: 掌上沈理青春版
 * @package: com.qlstudio.lite_kagg886
 * @className: GlobalApplication
 * @author: kagg886
 * @description: 应用实例
 * @date: 2023/4/13 21:17
 * @version: 1.0
 */
public class GlobalApplication extends Application implements Thread.UncaughtExceptionHandler, Runnable {

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
        if (session == null) {
            throw new OfflineException("登录实例已丢失，请重新登录");
        }
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

        Thread.setDefaultUncaughtExceptionHandler(this);
        new Handler(Looper.getMainLooper()).post(this);
    }

    @SuppressLint({"DiscouragedPrivateApi", "PrivateApi"})
    public static Activity getCurrentActivity() {
        Activity current = null;
        try {
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(
                    null);
            Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
            activitiesField.setAccessible(true);
            Map<?, ?> activities = (Map<?, ?>) activitiesField.get(activityThread);
            for (Object activityRecord : activities.values()) {
                Class<?> activityRecordClass = activityRecord.getClass();
                Field pausedField = activityRecordClass.getDeclaredField("paused");
                pausedField.setAccessible(true);
                if (!pausedField.getBoolean(activityRecord)) {
                    Field activityField = activityRecordClass.getDeclaredField("activity");
                    activityField.setAccessible(true);
                    current = (Activity) activityField.get(activityRecord);
                }
            }
        } catch (ClassNotFoundException | InvocationTargetException | NoSuchMethodException | NoSuchFieldException |
                 IllegalAccessException e) {
            e.printStackTrace();
        }
        Log.d("SeikoApplication", "access getCurrentActivity:" + current);
        return current;
    }

    public void logout() {
        GlobalApplication.getApplicationNoStatic().setSession(new SyluSession(GlobalApplication.getApplicationNoStatic().getSession().getStuCode()));
        GlobalApplication.getApplicationNoStatic().getPreferences().edit().putString("pwd", "").apply();
        Intent p = new Intent(getApplicationContext(), LoginActivity.class);
        p.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //Calling startActivity() from outside of an Activity  context requires the FLAG_ACTIVITY_NEW_TASK flag. Is this really what you want?
        startActivity(p);
        getCurrentActivity().finish();
    }

    @Override
    public void uncaughtException(@NonNull @NotNull Thread t, @NonNull @NotNull Throwable e) {
        goCrash(e);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Looper.loop();
            } catch (Throwable th) {
                goCrash(th);
            }
        }
    }

    private void goCrash(Throwable th) {
        Log.e("WRONG", "CRASH!", th);
        new Thread(() -> {
            Intent i = new Intent(getApplicationContext(), ErrorActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //Calling startActivity() from outside of an Activity  context requires the FLAG_ACTIVITY_NEW_TASK flag. Is this really what you want?
            i.putExtra("ex", th);
            startActivity(i);
            android.os.Process.killProcess(android.os.Process.myPid());
        }).start();
    }
}
