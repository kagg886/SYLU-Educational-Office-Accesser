package com.qlstudio.lite_kagg886.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.alibaba.fastjson.JSON;
import com.kagg886.jxw_collector.protocol.beans.SecondClassData;
import com.qlstudio.lite_kagg886.GlobalApplication;
import com.qlstudio.lite_kagg886.R;
import com.qlstudio.lite_kagg886.widget.SpiderWebPropertyDiagram;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Objects;

/**
 * @projectName: 掌上沈理青春版
 * @package: com.qlstudio.lite_kagg886.fragment
 * @className: ClassSecondFragment
 * @author: kagg886
 * @description: 第二课堂的Fragment
 * @date: 2023/5/4 14:30
 * @version: 1.0
 */
public class ClassSecondFragment extends Fragment {
    private static String[] data = {
            "A. 思想成长",
            "B. 实践学习",
            "C. 创新创业",
            "D. 志愿公益",
            "E. 文体+技能"
    };

    private AlertDialog dialog;

    private View root;

    private View dialogRoot;
    Handler twLoginResult = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            Button login = dialogRoot.findViewById(R.id.dialog_classsecedit_login);
            switch (msg.what) {
                case -1:
                    if (!dialog.isShowing()) {
                        dialog.show();
                    }
                    Toast.makeText(getContext(), msg.getData().getString("cause"), Toast.LENGTH_SHORT).show();
                    login.setEnabled(true);
                    break;
                case 0:
                    SecondClassData data1 = JSON.parseObject(msg.getData().getString("data"), SecondClassData.class);
                    loadFromData(data1);
                    login.setEnabled(true);
                    dialog.cancel();
                    dialog.dismiss();
                    break;
            }
        }
    };

    ;

    @SuppressLint("DefaultLocale")
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        this.root = LayoutInflater.from(getContext()).inflate(R.layout.fragment_class2, null);
        this.dialogRoot = LayoutInflater.from(getContext()).inflate(R.layout.dialog_classsecset, null);
        initDialog();

        SharedPreferences sp = GlobalApplication.getApplicationNoStatic().getPreferences();
        String pass = sp.getString("pwd_TW", null);
        if (TextUtils.isEmpty(pass)) {
            dialog.show();
        } else {
            new Thread(new LoginAction().setPass(pass)).start();
        }
        return root;
    }

    private void initDialog() {
        SharedPreferences sp = GlobalApplication.getApplicationNoStatic().getPreferences();
        dialogRoot.findViewById(R.id.dialog_classsecedit_goTW).setOnClickListener(v1 -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://xg.sylu.edu.cn/SyluTW/Sys/SystemForm/main.htm"));
            startActivity(browserIntent);
        });

        EditText editText = dialogRoot.findViewById(R.id.dialog_classsecedit_pass);

        String pass0 = sp.getString("pwd_TW", null);
        if (!TextUtils.isEmpty(pass0)) {
            editText.setText(pass0);
        }
        Button login = dialogRoot.findViewById(R.id.dialog_classsecedit_login);

        login.setOnClickListener((v) -> {
            login.setEnabled(false);
            String pass = editText.getText().toString();
            if (TextUtils.isEmpty(pass)) {
                pass = sp.getString("pwd", null);
            }
            //启动登录
            new Thread(new LoginAction().setPass(pass)).start();
        });

        Button exit = dialogRoot.findViewById(R.id.dialog_classsecedit_exit);

        exit.setOnClickListener((v) -> {
            dialog.cancel();
            dialog.dismiss();
            Objects.requireNonNull(getActivity()).onBackPressed();
        });


        dialog = new AlertDialog.Builder(Objects.requireNonNull(getContext()))
                .setView(dialogRoot)
                .setCancelable(false)
                .create();
    }

    @SuppressLint("DefaultLocale")
    private void loadFromData(SecondClassData data) {
        SpiderWebPropertyDiagram diagram = root.findViewById(R.id.class2_diagram);

        HashMap<String, SpiderWebPropertyDiagram.DiagramUnit> map = new HashMap<>();
        for (char i = 'A'; i <= 'E'; i++) {
            double exp; //期望值
            double act; //实际值
            try {
                exp = (double) SecondClassData.class.getMethod("get" + i).invoke(data);
                act = (double) SecondClassData.class.getMethod("get" + i + "1").invoke(data);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
            map.put(ClassSecondFragment.data[i - 'A'] + "\n" + "最低达标:" + exp, new SpiderWebPropertyDiagram.DiagramUnit(exp, act));
        }
        diagram.setLabel(map);

        TextView tx = root.findViewById(R.id.class2_text);
        tx.setText(String.format("总分:%.2f/%.2f", data.getSum1(), data.getSum()));

    }

    private class LoginAction implements Runnable {
        private String pass;

        public LoginAction setPass(String pass) {
            this.pass = pass;
            return this;
        }

        @Override
        public void run() {
            try {
                SecondClassData data1 = GlobalApplication.getApplicationNoStatic().getSession().getSecondClassData(pass);
                Message msg = new Message();
                msg.what = 0;
                msg.getData().putString("data", JSON.toJSONString(data1));
                twLoginResult.sendMessage(msg);
                GlobalApplication.getApplicationNoStatic().getPreferences().edit()
                        .putString("pwd_TW", pass).apply();
            } catch (Exception e) {
                Message msg = new Message();
                msg.what = -1;
                msg.getData().putString("cause", e.getMessage());
                twLoginResult.sendMessage(msg);
            }
        }
    }
}
