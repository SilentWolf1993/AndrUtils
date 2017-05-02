package com.yhy.utils.core;

import android.content.Context;
import android.widget.Toast;

/**
 * Toast工具类
 */
public class ToastUtils {
    private static Context ctx;

    private ToastUtils() {
        throw new RuntimeException("Can not create instance for class ToastUtils.");
    }

    /**
     * 初始化，在Application中
     *
     * @param ctx 上下文对象
     */
    public static void init(Context ctx) {
        ToastUtils.ctx = ctx;
    }

    /**
     * 短时间显示
     *
     * @param text 提示的内容
     */
    public static void shortToast(CharSequence text) {
        Toast.makeText(ctx, text, Toast.LENGTH_SHORT).show();
    }

    /**
     * 长时间显示
     *
     * @param text 提示的内容
     */
    public static void longToast(CharSequence text) {
        Toast.makeText(ctx, text, Toast.LENGTH_LONG).show();
    }

    /**
     * 短时间显示
     *
     * @param resId 提示的内容资源id
     */
    public static void shortToast(int resId) {
        Toast.makeText(ctx, ctx.getResources().getString(resId), Toast.LENGTH_SHORT).show();
    }

    /**
     * 长时间显示
     *
     * @param resId 提示的内容资源id
     */
    public static void longToast(int resId) {
        Toast.makeText(ctx, ctx.getResources().getString(resId), Toast.LENGTH_LONG).show();
    }
}
