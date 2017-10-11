package com.yhy.utils.core;

import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import java.io.ByteArrayOutputStream;

/**
 * author : 颜洪毅
 * e-mail : yhyzgn@gmail.com
 * time   : 2017-10-11 17:44
 * version: 1.0.0
 * desc   : View工具类
 */
public class ViewUtils {

    private ViewUtils() {
        throw new RuntimeException("Can not create instance for class ViewUtils.");
    }

    /**
     * 移除view的父控件
     *
     * @param view view
     */
    public static void removeParent(View view) {
        if (null != view && null != view.getParent() && view.getParent() instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) view.getParent();
            vg.removeView(view);
        }
    }

    /**
     * 对控件截图
     *
     * @param v       需要进行截图的控件
     * @param quality 图片的质量 0-100
     * @return 该控件截图的byte数组对象
     */
    public static byte[] printScreen(View v, int quality) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache(true);
        Bitmap bitmap = v.getDrawingCache();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        return baos.toByteArray();
    }

    /**
     * 截图
     *
     * @param v 需要进行截图的控件
     * @return 该控件截图的Bitmap对象
     */
    public static Bitmap printScreen(View v) {
        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache();
        return v.getDrawingCache();
    }
}
