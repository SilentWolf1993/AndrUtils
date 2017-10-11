package com.yhy.utils.core;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

/**
 * author : 颜洪毅
 * e-mail : yhyzgn@gmail.com
 * time   : 2017-10-11 17:28
 * version: 1.0.0
 * desc   : meta参数工具类
 */
public class MetaUtils {
    private static Context ctx;

    private MetaUtils() {
        throw new IllegalStateException("Can not instantiate class MetaUtils.");
    }

    /**
     * 初始化
     *
     * @param context 上下文对象
     */
    public static void init(Context context) {
        ctx = context;
    }

    /**
     * 获取meta参数
     *
     * @return meta参数
     */
    public static Bundle getMeta() throws PackageManager.NameNotFoundException {
        ApplicationInfo ai = ctx.getPackageManager().getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
        return ai.metaData;
    }

    /**
     * 获取字符串参数
     *
     * @param name 名称
     * @return 值
     */
    public static String getString(String name) {
        return getString(name, "");
    }

    /**
     * 获取字符串参数
     *
     * @param name     名称
     * @param defValue 默认值
     * @return 值
     */
    public static String getString(String name, String defValue) {
        try {
            return getMeta().getString(name, defValue);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return defValue;
    }

    /**
     * 获取整型参数
     *
     * @param name 名称
     * @return 值
     */
    public static int getInt(String name) {
        return getInt(name, -1);
    }

    /**
     * 获取整型参数
     *
     * @param name     名称
     * @param defValue 默认值
     * @return 值
     */
    public static int getInt(String name, int defValue) {
        try {
            return getMeta().getInt(name, defValue);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return defValue;
    }


    /**
     * 获取参数
     *
     * @param name 名称
     * @return 值
     */
    public static Object get(String name) {
        try {
            return getMeta().get(name);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
