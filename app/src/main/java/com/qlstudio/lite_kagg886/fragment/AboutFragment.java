package com.qlstudio.lite_kagg886.fragment;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.qlstudio.lite_kagg886.BuildConfig;
import com.qlstudio.lite_kagg886.GlobalApplication;
import com.qlstudio.lite_kagg886.R;
import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.IOException;

/**
 * @projectName: 掌上沈理青春版
 * @package: com.qlstudio.lite_kagg886.fragment
 * @className: AboutFragment
 * @author: kagg886
 * @description: 关于
 * @date: 2023/4/14 17:40
 * @version: 1.0
 */
public class AboutFragment extends Fragment implements View.OnClickListener {
    private final Handler checkHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 0:
                    JSONObject data;
                    try {
                        data = new JSONObject(msg.getData().getString("update"));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    String body = data.optString("body");
                    String title = data.optString("tag_name");
                    builder.setTitle(("发现更新:V" + BuildConfig.VERSION_NAME) + "->V" + title);
                    builder.setMessage(body.substring(0, Math.min(body.length() - 1, 500)) + (body.length() - 1 > 500 ? "..." : ""));
                    builder.setPositiveButton("下载", (dialog, which) -> {
                        JSONArray a = data.optJSONArray("assets");
                        for (int i = 0; i < a.length(); i++) {
                            if (a.optJSONObject(i).optString("name").equals("app-release.apk")) {
                                openUrlByBrowser(a.optJSONObject(i).optString("browser_download_url"));
                                return;
                            }
                        }
                    });
                    builder.create().show();
                    break;
                case 1:
                    Toast.makeText(getActivity(), "当前为最新版本!", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(getActivity(), "更新检测失败...", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return new AboutPage(getActivity())
                .isRTL(false)
                .setImage(R.mipmap.ic_launcher)//图片
                .setDescription("一个令人舒适的教务信息集成APP")//介绍
                .addGroup("版本信息")
                .addItem(new Element().setTitle("Version：" + BuildConfig.VERSION_NAME))
                .addItem(new Element().setIconDrawable(R.drawable.ic_update).setTitle("检查更新").setOnClickListener(this))
                .addGroup("关注我")
                .addEmail("iveour@163.com", "个人邮箱")//邮箱
                .addWebsite("https://kagg886.top", "个人博客")//网站
                .addGitHub("kagg886/SYLU-Educational-Office-Accesser", "查看源代码")//github
                .addItem(new Element()
                        .setIconDrawable(R.drawable.ic_add_qq_group)
                        .setTitle("加入QQ群")
                        .setIntent(getIntent())
                )
                .addItem(new Element().setIconDrawable(R.drawable.ic_donate).setTitle("捐赠我!").setOnClickListener((v) -> {
                    ImageView view = new ImageView(getActivity());
                    try {
                        view.setImageBitmap(BitmapFactory.decodeStream(getActivity().getAssets().open("pay.png")));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    new AlertDialog.Builder(getActivity())
                            .setTitle("截图保存二维码以捐赠")
                            .setView(view).create().show();
                }))
                .create();
    }

    private Intent getIntent() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.setData(Uri.parse("mqqapi://card/show_pslcard?src_type=internal&version=1&uin=798201505&card_type=group&source=qrcode"));
        return intent;
    }

    @Override
    public void onClick(View v) {
        //TODO 接入gitee信息检查
        checkUpdate();
    }

    private void checkUpdate() {
        new Thread(() -> {
            try {
                JSONObject object = new JSONObject(
                        Jsoup.connect("https://gitee.com/api/v5/repos/kagg886/sylu-educational-office-accesser/releases/latest")
                                .ignoreContentType(true)
                                .timeout(10000)
                                .execute().body());
                String newVer = object.optString("tag_name");
                if (!BuildConfig.VERSION_NAME.equals(newVer)) {
                    Message message = new Message();
                    message.what = 0;
                    message.getData().putString("update", object.toString());
                    checkHandler.sendMessage(message);
                    return;
                }
                checkHandler.sendEmptyMessage(1);
            } catch (Exception e) {
                checkHandler.sendEmptyMessage(2);
            }
        }).start();
    }

    public static void openUrlByBrowser(String url) {
        Uri uri = Uri.parse(url);
        GlobalApplication.getCurrentActivity().startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }
}
