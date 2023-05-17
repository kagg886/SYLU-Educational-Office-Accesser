package com.qlstudio.lite_kagg886.fragment.tools.impl;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.*;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import com.kagg886.jxw_collector.protocol.SyluSession;
import com.qlstudio.lite_kagg886.GlobalApplication;
import com.qlstudio.lite_kagg886.R;
import com.qlstudio.lite_kagg886.activity.MainActivity;
import com.qlstudio.lite_kagg886.fragment.tools.AbstractDialogFragments;
import com.qlstudio.lite_kagg886.util.ScaleUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class PhotoDeSigner extends AbstractDialogFragments {

    private final List<Uri> uris = new ArrayList<>();

    public PhotoDeSigner(Context ctx) {
        super(ctx);
        setText("图片添加姓名学号").setId(R.drawable.ic_tool_image);
    }

    public static Bitmap cloneAndDrawText(Bitmap src, String text) {
        //准备画笔类
        Paint textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(ScaleUtil.dip2px(ScaleUtil.px2dip(src.getWidth() * 0.1f)));

        Paint bgPaint = new Paint();
        bgPaint.setColor(Color.WHITE);

        Bitmap dst = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(dst);
        canvas.drawBitmap(src, 0, 0, null);
        String[] lines = text.split("\n");
        float y = 10;
        for (String line : lines) {
            float len = textPaint.measureText(line);
            y += textPaint.getTextSize();

            float left = (src.getWidth() - len) / 2;
            canvas.drawRect(left, y - textPaint.getTextSize(), left + len, y, bgPaint);
            //居中
            canvas.drawText(line, (src.getWidth() - len) / 2.0f, y, textPaint);
        }
        src.recycle();
        return dst;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public AlertDialog initDialog(Context c) {
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("批量选择图片!");

        View v = LayoutInflater.from(c).inflate(R.layout.dialog_photodesigner, null);
        Button button = v.findViewById(R.id.dialog_photodesigner_choose);
        TextView tx = v.findViewById(R.id.dialog_photodesigner_state);
        MainActivity activity = (MainActivity) GlobalApplication.getCurrentActivity();

        button.setOnClickListener((v0) -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);//关键！多选参数
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            activity.getCaller().launch(intent);

            new Thread(() -> {
                clearUriFromAR(activity.getResultBlocked());
                activity.runOnUiThread(() -> tx.setText(String.format("已选择%d张图片", uris.size())));
            }).start();
        });

        Button button1 = v.findViewById(R.id.dialog_photodesigner_do);
        button1.setOnClickListener((v0) -> {
            builder.setCancelable(false);
            button.setEnabled(false);
            button1.setEnabled(false);
            new Thread(() -> {
                SyluSession userInfo = GlobalApplication.getApplicationNoStatic().getSession();
                String name = userInfo.getUserInfo().getName().split(" ")[0];
                String zip = UUID.randomUUID().toString().replace("-", "") + ".zip";
                File file = new File(activity.getCacheDir().toPath().resolve("share").toFile(), zip);
                file.getParentFile().mkdirs();
                ZipOutputStream zipStream;
                try {
                    file.createNewFile();
                    zipStream = new ZipOutputStream(Files.newOutputStream(file.toPath()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                for (int i = 0; i < uris.size(); i++) {
                    try {
                        Bitmap src = BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uris.get(i)));
                        src = cloneAndDrawText(src, String.format("%s\n%s", userInfo.getStuCode(), name));

                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        src.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        out.flush();
                        out.close();

                        zipStream.putNextEntry(new ZipEntry(i + ".png"));
                        zipStream.write(out.toByteArray());
                        zipStream.flush();

                        int finalI = i;
                        activity.runOnUiThread(() -> {
                            button1.setText(String.format("%s/%s", finalI, uris.size()));
                        });
                    } catch (Exception e) {
                        activity.runOnUiThread(() -> {
                            builder.setCancelable(true);
                            button.setEnabled(true);
                            button1.setEnabled(true);
                            button1.setText("确定");
                            Log.e("PhotoDesigner", "图片生成过程中出错", e);
                            Toast.makeText(c, "发生了一点小错误!", Toast.LENGTH_SHORT).show();
                        });
                        return;
                    }
                }
                try {
                    activity.runOnUiThread(() -> {
                        builder.setCancelable(true);
                        button.setEnabled(true);
                        button1.setEnabled(true);
                        button1.setText("确定");
                        Toast.makeText(c, "生成完毕!", Toast.LENGTH_SHORT).show();
                    });
                    zipStream.close();
                    Intent intent = new Intent("android.intent.action.SEND");
                    intent.putExtra("android.intent.extra.STREAM", FileProvider.getUriForFile(c, "com.qlstudio.lite_kagg886.fileprovider", file));
                    intent.setType("*/*");
                    activity.startActivity(intent);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        });


        builder.setView(v);
        return builder.create();
    }

    private void clearUriFromAR(ActivityResult result) {
        uris.clear();
        if (Objects.requireNonNull(result.getData()).getData() != null) {
            try {
                uris.add(result.getData().getData());
            } catch (Exception ignored) {
            }
        } else {
            ClipData clipData = result.getData().getClipData();
            if (clipData != null) {
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    ClipData.Item item = clipData.getItemAt(i);
                    uris.add(item.getUri());
                }
            }
        }
    }
}
