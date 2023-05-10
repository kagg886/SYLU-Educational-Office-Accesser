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
import com.qlstudio.lite_kagg886.util.HttpClientProxy;
import com.qlstudio.lite_kagg886.util.LogCatcher;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
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

    public boolean isInNightMode() {
//        深色模式的值为:0x21
//        浅色模式的值为:0x11
        return getResources().getConfiguration().uiMode == 0x21;
    }

    public void setSession(SyluSession session) {
        this.session = session;
    }

    public SharedPreferences getPreferences() {
        return preferences;
    }

    public File getLoggerBase() {
        return new File(getFilesDir(), "log");
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onCreate() {
        super.onCreate();


        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        session = new SyluSession(new HttpClientProxy());
        if (preferences.getString("user", null) != null) {
            session.setUser(preferences.getString("user", null));
        }

        //设置日志记录器
        File logRoot = getLoggerBase();
        logRoot.mkdirs();
        int i = 0;
        File log;
        do {
            LocalDate date = LocalDate.now();
            log = new File(logRoot, String.format("%d-%d-%d_%d.log", date.getYear(), date.getMonth().getValue(), date.getDayOfMonth(), i));
            i++;
        } while (log.exists());
        try {
            log.createNewFile();
            new LogCatcher(log).start();
        } catch (IOException e) {
            System.out.println(log.getAbsolutePath());
            throw new RuntimeException(e);
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
        String stu = GlobalApplication.getApplicationNoStatic().getSession().getStuCode();
        SyluSession session1 = new SyluSession(new HttpClientProxy());
        session1.setUser(stu);
//        GlobalApplication.getApplicationNoStatic().setSession(new SyluSession(new HttpClientProxy()));
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
        Log.e("WRONG", "App Crash and will exit App", th);
        new Thread(() -> {
            Intent i = new Intent(getApplicationContext(), ErrorActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //Calling startActivity() from outside of an Activity  context requires the FLAG_ACTIVITY_NEW_TASK flag. Is this really what you want?
            i.putExtra("ex", th);
            startActivity(i);
            android.os.Process.killProcess(android.os.Process.myPid());
        }).start();
    }
}
