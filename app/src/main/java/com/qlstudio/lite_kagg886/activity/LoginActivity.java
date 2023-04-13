package com.qlstudio.lite_kagg886.activity;

import android.animation.*;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.qlstudio.lite_kagg886.R;

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

    private float mWidth, mHeight;

    private LinearLayout mName, mPsw;

    private boolean isLogin = false;

    private Handler animController = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 0:
                    if (isLogin) {
                        return;
                    }
                    isLogin = true;
                    // 计算出控件的高与宽
                    mWidth = mBtnLogin.getMeasuredWidth();
                    mHeight = mBtnLogin.getMeasuredHeight();
                    // 隐藏输入框
                    mName.setVisibility(View.INVISIBLE);
                    mPsw.setVisibility(View.INVISIBLE);
                    inputAnimator(mInputLayout, mWidth, mHeight);
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
                    animator2.start();
                    isLogin = false;
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        initView();
    }

    private void initView() {
        mBtnLogin = findViewById(R.id.main_btn_login);
        progress = findViewById(R.id.layout_progress);
        mInputLayout = findViewById(R.id.input_layout);
        mName = findViewById(R.id.input_layout_name);
        mPsw = findViewById(R.id.input_layout_psw);

        mBtnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                animController.sendEmptyMessage(0);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                animController.sendEmptyMessage(1);
            }
        }).start();
    }

    private void inputAnimator(final View view, float w, float h) {

        AnimatorSet set = new AnimatorSet();

        ValueAnimator animator = ValueAnimator.ofFloat(0, w);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view
                        .getLayoutParams();
                params.leftMargin = (int) value;
                params.rightMargin = (int) value;
                view.setLayoutParams(params);
            }
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
                /**
                 * 动画结束后，先显示加载的动画，然后再隐藏输入框
                 */
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