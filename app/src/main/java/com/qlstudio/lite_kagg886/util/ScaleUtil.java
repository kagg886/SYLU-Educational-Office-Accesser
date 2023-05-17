package com.qlstudio.lite_kagg886.util;

/**
 * dp与px互转，保证布局一致
 *
 * @author kagg886
 * @date 2023/5/17 16:18
 **/

import com.qlstudio.lite_kagg886.GlobalApplication;

public class ScaleUtil {
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(float dpValue) {
        final float scale = GlobalApplication.getApplicationNoStatic().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(float pxValue) {
        final float scale = GlobalApplication.getApplicationNoStatic().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}

