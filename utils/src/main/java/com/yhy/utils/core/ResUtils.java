package com.yhy.utils.core;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ArrayRes;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import java.util.ArrayList;
import java.util.List;

/**
 * author : 颜洪毅
 * e-mail : yhyzgn@gmail.com
 * time   : 2017-10-16 10:01
 * version: 1.0.0
 * desc   : 资源工具类
 */
public class ResUtils {
    @SuppressLint("StaticFieldLeak")
    private static Context mCtx;

    private ResUtils() {
        throw new UnsupportedOperationException("Can not instantiate class ResUtils.");
    }

    /**
     * 初始化，在Application入口调用
     *
     * @param ctx 上下文对象
     */
    public static void init(Context ctx) {
        mCtx = ctx;
    }

    /**
     * 获取字符串，可格式化匹配参数
     *
     * @param resId      资源id
     * @param formatArgs 匹配参数列表
     * @return 获取到的字符串
     */
    public static String getString(@StringRes int resId, Object... formatArgs) {
        return mCtx.getString(resId, formatArgs);
    }

    /**
     * 获取字符串，可格式化匹配参数
     *
     * @param str        原始字符串
     * @param formatArgs 匹配参数列表
     * @return 获取到的字符串
     */
    public static String getString(String str, Object... formatArgs) {
        return String.format(str, formatArgs);
    }

    /**
     * 获取颜色值
     *
     * @param resId 资源id
     * @return 获取到的颜色值
     */
    @ColorInt
    public static int getColor(@ColorRes int resId) {
        return mCtx.getResources().getColor(resId);
    }

    /**
     * 获取字符串列表
     *
     * @param resId 资源id
     * @return 获取到的字符串列表
     */
    public static List<String> getStringList(@ArrayRes int resId) {
        //Arrays.asList() 转换后的List不可改变大小，有些地方不适用，所以使用下列方法转换为List
        List<String> resultList = new ArrayList<>();
        for (String s : getStringArr(resId)) {
            resultList.add(s);
        }
        return resultList;
    }

    /**
     * 获取字符串数组
     *
     * @param resId 资源id
     * @return 获取到的字符串数组
     */
    public static String[] getStringArr(@ArrayRes int resId) {
        return mCtx.getResources().getStringArray(resId);
    }

    /**
     * 获取图片
     *
     * @param resId 资源id
     * @return 获取到的图片
     */
    public static Drawable getDrawable(@DrawableRes int resId) {
        return mCtx.getResources().getDrawable(resId);
    }
}
