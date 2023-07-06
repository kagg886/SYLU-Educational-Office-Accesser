package com.qlstudio.lite_kagg886.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by lum on 2019/1/15.
 */

@SuppressLint("AppCompatCustomView")
public class MarqueeTextView extends TextView {

    public MarqueeTextView(Context context) {
        super(context);
        TextView textView = this;
        textView.setMarqueeRepeatLimit(Integer.MAX_VALUE);
        textView.setFocusable(true);
        textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        textView.setSingleLine();
        textView.setFocusableInTouchMode(true);
        textView.setHorizontallyScrolling(true);
    }

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TextView textView = this;
        textView.setMarqueeRepeatLimit(Integer.MAX_VALUE);
        textView.setFocusable(true);
        textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        textView.setSingleLine();
        textView.setFocusableInTouchMode(true);
        textView.setHorizontallyScrolling(true);
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TextView textView = this;
        textView.setMarqueeRepeatLimit(Integer.MAX_VALUE);
        textView.setFocusable(true);
        textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        textView.setSingleLine();
        textView.setFocusableInTouchMode(true);
        textView.setHorizontallyScrolling(true);
    }

    @Override
    public boolean isFocused() {
        // TODO Auto-generated method stub
        return true;
    }

}


