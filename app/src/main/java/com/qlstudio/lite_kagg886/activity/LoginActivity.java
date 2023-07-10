package com.qlstudio.lite_kagg886.activity;

import android.animation.*;
import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import com.kagg886.jxw_collector.exceptions.OfflineException;
import com.kagg886.jxw_collector.protocol.SyluSession;
import com.qlstudio.lite_kagg886.GlobalApplication;
import com.qlstudio.lite_kagg886.R;
import com.qlstudio.lite_kagg886.fragment.AboutFragment;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;

/**
 * @projectName: 掌上沈理青春版
 * @package: com.qlstudio.lite_kagg886.activity
 * @className: LoginActivity
 * @author: kagg886
 * @description: 登录页面
 * @date: 2023/4/13 21:37
 * @version: 1.0
 */
public class LoginActivity extends Activity implements View.OnClickListener {

    private static final int TIME = 200;
    private TextView mBtnLogin;

    private View progress;

    private View mInputLayout;

    private LinearLayout mName, mPsw;

    private EditText userName, pwd;

    private GlobalApplication application;

    private volatile boolean isLogin = false;

    private final Handler animController = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 0:
                    if (isLogin) {
                        return;
                    }
                    isLogin = true;
                    // 计算出控件的高与宽
                    float mWidth = mBtnLogin.getMeasuredWidth();
                    // 隐藏输入框
                    mName.setVisibility(View.INVISIBLE);
                    mPsw.setVisibility(View.INVISIBLE);
                    inputAnimator(mInputLayout, mWidth);
                    break;
                case 1:
                    progress.setVisibility(View.GONE);
                    mInputLayout.setVisibility(View.VISIBLE);
                    mName.setVisibility(View.VISIBLE);
                    mPsw.setVisibility(View.VISIBLE);

                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mInputLayout.getLayoutParams();
                    params.leftMargin = 0;
                    params.rightMargin = 0;
                    mInputLayout.setLayoutParams(params);

                    ObjectAnimator animator2 = ObjectAnimator.ofFloat(mInputLayout, "scaleX", 0.5f, 1f);
                    animator2.setDuration(TIME);
                    animator2.setInterpolator(new AccelerateDecelerateInterpolator());
                    animator2.addListener(new Animator.AnimatorListener() {

                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            isLogin = false;
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }
                    });
                    animator2.start();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        application = GlobalApplication.getApplicationNoStatic();

        initView();
    }

    private void initView() {
        mBtnLogin = findViewById(R.id.main_btn_login);
        progress = findViewById(R.id.layout_progress);
        mInputLayout = findViewById(R.id.input_layout);
        mName = findViewById(R.id.input_layout_name);
        mPsw = findViewById(R.id.input_layout_psw);

        userName = findViewById(R.id.login_user);
        pwd = findViewById(R.id.login_pwd);

        String user = GlobalApplication.getApplicationNoStatic().getSession().getStuCode();
        if (!TextUtils.isEmpty(user)) {
            userName.setText(user);
        }

        String pwd = GlobalApplication.getApplicationNoStatic().getPreferences().getString("pwd", "");
        if (!TextUtils.isEmpty(pwd)) {
            this.pwd.setText(pwd);
            onClick(null);
        }

        mBtnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (isLogin) { //不能放进线程中判断，不然会重复启用关闭动画
            return;
        }
        application.getPreferences().edit()
                .putString("user", userName.getEditableText().toString())
                .putString("pwd", pwd.getEditableText().toString()).apply();
        if (!userName.getEditableText().toString().equals(application.getSession().getStuCode())) {
            application.setSession(new SyluSession(userName.getEditableText().toString()));
        }
        new Thread(() -> {
            CountDownLatch latch = new CountDownLatch(1);
            animController.sendEmptyMessage(0);
            try {
                if (application.getSession().needVerifyCode()) {
                    SyluSession session = application.getSession();
                    View captchaView = LayoutInflater.from(LoginActivity.this).inflate(R.layout.dialog_captcha, null);
                    EditText result = captchaView.findViewById(R.id.captcha_res);
                    ImageView view = captchaView.findViewById(R.id.captcha_img);
                    view.setImageBitmap(BitmapFactory.decodeStream(session.getClient().url(session.getVerifyLink()).get().bodyStream()));

                    AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this)
                            .setTitle("输入验证码")
                            .setView(captchaView)
                            .setOnCancelListener(dialog12 -> {
                                throw new RuntimeException("验证码被用户取消");
                                //TODO 待测试
                            })
                            .setPositiveButton("确定", (dialog1, which) -> {
                                latch.countDown();
                            });
                    runOnUiThread(() -> {
                        dialog.create().show();
                    });
                    latch.await();
                    session.loginByPwd(pwd.getText().toString(), result.getText().toString());
                } else {
                    application.getSession().loginByPwd(pwd.getText().toString());
                }
                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
                finish();
                isLogin = false;
            } catch (Exception e) {
                Log.e(getClass().getName(), "login failed", e);
                runOnUiThread(() -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("登陆失败");
                    if ((e instanceof OfflineException)) {
                        builder.setMessage(e.getMessage());
                        builder.setPositiveButton("找回密码", (dialog, which) -> AboutFragment.openUrlByBrowser("https://jxw.sylu.edu.cn/pwdmgr/retake/index.zf"));
                    } else if (Objects.requireNonNull(e.getMessage()).contains("Read timeout")) {
                        builder.setMessage("连接超时!请检查网络状态后重试");
                    } else {
                        builder.setMessage(e.getMessage());
                    }
                    builder.create().show();
                });
            }
            animController.sendEmptyMessage(1);
        }).start();
    }

    private void inputAnimator(final View view, float w) {
        AnimatorSet set = new AnimatorSet();
        ValueAnimator animator = ValueAnimator.ofFloat(0, w);
        animator.addUpdateListener(animation -> {
            float value = (Float) animation.getAnimatedValue();
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view
                    .getLayoutParams();
            params.leftMargin = (int) value;
            params.rightMargin = (int) value;
            view.setLayoutParams(params);
        });

        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mInputLayout,
                "scaleX", 1f, 0.5f);
        set.setDuration(TIME);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.playTogether(animator, animator2);
        set.start();
        set.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                progress.setVisibility(View.VISIBLE);
                PropertyValuesHolder animator = PropertyValuesHolder.ofFloat("scaleX",
                        0.5f, 1f);
                PropertyValuesHolder animator2 = PropertyValuesHolder.ofFloat("scaleY",
                        0.5f, 1f);
                ObjectAnimator animator3 = ObjectAnimator.ofPropertyValuesHolder(progress,
                        animator, animator2);
                animator3.setDuration(TIME * 5);
                animator3.setInterpolator(new JellyInterpolator());
                animator3.start();
                mInputLayout.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }
        });

    }

    public static class JellyInterpolator extends LinearInterpolator {
        private float factor;

        public JellyInterpolator() {
            this.factor = 0.15f;
        }

        @Override
        public float getInterpolation(float input) {
            return (float) (Math.pow(2, -10 * input)
                    * Math.sin((input - factor / 4) * (2 * Math.PI) / factor) + 1);
        }
    }
}