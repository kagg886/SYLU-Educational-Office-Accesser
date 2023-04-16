package com.qlstudio.lite_kagg886.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.qlstudio.lite_kagg886.R;

import java.io.PrintWriter;
import java.io.StringWriter;

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

        ((ListView) findViewById(R.id.error_msg)).setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, writer.toString().split("\n")));
        findViewById(R.id.textView2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                intent.setData(Uri.parse("mqqapi://card/show_pslcard?src_type=internal&version=1&uin=798201505&card_type=group&source=qrcode"));
                startActivity(intent);
            }
        });
    }
}
