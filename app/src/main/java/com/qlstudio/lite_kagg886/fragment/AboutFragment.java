package com.qlstudio.lite_kagg886.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.qlstudio.lite_kagg886.BuildConfig;
import com.qlstudio.lite_kagg886.R;
import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;
import org.jetbrains.annotations.NotNull;

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
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return new AboutPage(getActivity())
                .isRTL(false)
                .setImage(R.mipmap.ic_launcher)//图片
                .setDescription("一个令人舒适的教务信息集成APP")//介绍
                .addItem(new Element().setTitle("Version：" + BuildConfig.VERSION_NAME))
                .addItem(new Element().setIconDrawable(R.drawable.ic_update).setTitle("检查更新").setOnClickListener(this))
                .addGroup("关注我")
                .addEmail("iveour@163.com", "个人邮箱")//邮箱
                .addWebsite("https://kagg886.top", "个人博客")//网站
                .addGitHub("kagg886/SYLU-Educational-Office-Accesser", "查看源代码")//github
                .create();
    }

    @Override
    public void onClick(View v) {
        //TODO 接入github信息检查
    }
}
