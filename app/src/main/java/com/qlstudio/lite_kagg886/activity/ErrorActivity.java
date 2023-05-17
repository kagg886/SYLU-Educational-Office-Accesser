package com.qlstudio.lite_kagg886.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import com.qlstudio.lite_kagg886.GlobalApplication;
import com.qlstudio.lite_kagg886.R;

import java.io.*;
import java.nio.file.Files;
import java.util.Objects;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @projectName: 掌上沈理青春版
 * @package: com.qlstudio.lite_kagg886.activity
 * @className: ErrorActivity
 * @author: kagg886
 * @description: 错误的Activity
 * @date: 2023/4/16 22:10
 * @version: 1.0
 */
public class ErrorActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);
        Throwable a = (Throwable) getIntent().getSerializableExtra("ex");

        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        a.printStackTrace(printWriter);

        ((TextView) findViewById(R.id.activity_error_tip)).setText("单击左侧按钮加群\n单击右侧按钮分享错误报告\n错误报告可能会含有隐私信息\n强烈建议私发给开发者\n防止账户被盗\n(下面为技术信息)");

        ((ListView) findViewById(R.id.error_msg)).setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, writer.toString().split("\n")));
        findViewById(R.id.activity_error_joingroup).setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            intent.setData(Uri.parse("mqqapi://card/show_pslcard?src_type=internal&version=1&uin=798201505&card_type=group&source=qrcode"));
            startActivity(intent);
        });


        findViewById(R.id.activity_error_sendlog).setOnClickListener((v) -> {
            ProgressBar bar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("请稍等").setView(bar).create();
            Handler handler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    switch (msg.what) {
                        case 0:
                            dialog.show();
                            break;
                        case 1:
                            dialog.dismiss();
                            dialog.cancel();
                            Intent intent = new Intent("android.intent.action.SEND");
                            intent.putExtra("android.intent.extra.STREAM",
                                    FileProvider.getUriForFile(
                                            ErrorActivity.this,
                                            "com.qlstudio.lite_kagg886.fileprovider",
                                            new File(msg.getData().getString("file"))
                                    )
                            );
                            intent.setType("*/*");
                            startActivity(intent);
                            break;
                        case 2:
                            bar.setMax(msg.getData().getInt("max"));
                            break;
                        case 3:
                            bar.setProgress(bar.getProgress() + 1);
                            break;
                    }
                }
            };
            new Thread(() -> {
                handler.sendEmptyMessage(0);
                File base = GlobalApplication.getApplicationNoStatic().getLoggerBase();

                File target = new File(getCacheDir(), UUID.randomUUID().toString() + ".zip");
                try {
                    Message message = new Message();
                    message.what = 2;
                    message.getData().putInt("max", Objects.requireNonNull(base.listFiles()).length);
                    handler.sendMessage(message);
                    target.createNewFile();
                    try (ZipOutputStream out = new ZipOutputStream(Files.newOutputStream(target.toPath()))) {
                        for (File log : Objects.requireNonNull(base.listFiles())) {
                            ZipEntry entry = new ZipEntry(log.getName());
                            out.putNextEntry(entry);

                            try (FileInputStream stream = new FileInputStream(log)) {
                                byte[] buffer = new byte[1024];
                                int len;
                                while ((len = stream.read(buffer)) != -1) {
                                    out.write(buffer, 0, len);
                                }
//                                byte[] buffer = new byte[1024];
//                                int readOnly = stream.read(buffer);
//                                //使用缓冲区，效率杠杠的
//                                out.write(buffer, 0, readOnly);
                            }
                            handler.sendEmptyMessage(3);
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Message message = new Message();
                message.what = 1;
                message.getData().putString("file", target.getAbsolutePath());
                handler.sendMessage(message);
            }).start();
        });
    }
}
