package com.yhy.utils.core;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * author : 颜洪毅
 * e-mail : yhyzgn@gmail.com
 * time   : 2017-10-16 11:11
 * version: 1.0.0
 * desc   : PS 工具类
 */
public class SPUtils {

    private static SharedPreferences sp;

    private SPUtils() {
        throw new UnsupportedOperationException("Can not instantiate utils class.");
    }

    /**
     * 初始化
     *
     * @param context 上下文对象
     */
    public static void init(Context context) {
        init(context, "sp_utils");
    }

    /**
     * 初始化
     *
     * @param context 上下文对象
     * @param spName  sp文件名称
     */
    public static void init(Context context, String spName) {
        sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
    }

    /**
     * 移除一条记录
     *
     * @param key 名称
     */
    public static void remove(String key) {
        edit().remove(key).apply();
    }

    /**
     * 添加整型记录
     *
     * @param key   名称
     * @param value 值
     */
    public static void putInt(String key, int value) {
        edit().putInt(key, value).apply();
    }

    /**
     * 添加字符串记录
     *
     * @param key   名称
     * @param value 值
     */
    public static void putString(String key, String value) {
        if (null == value) {
            remove(key);
            return;
        }
        edit().putString(key, value).apply();
    }

    /**
     * 添加布尔记录
     *
     * @param key   名称
     * @param value 值
     */
    public static void putBoolean(String key, boolean value) {
        edit().putBoolean(key, value).apply();
    }

    /**
     * 添加浮点型记录
     *
     * @param key   名称
     * @param value 值
     */
    public static void putFloat(String key, float value) {
        edit().putFloat(key, value).apply();
    }

    /**
     * 添加长整型记录
     *
     * @param key   名称
     * @param value 值
     */
    public static void putLong(String key, Long value) {
        edit().putLong(key, value).apply();
    }

    /**
     * 添加双精度记录
     *
     * @param key   名称
     * @param value 值
     */
    public static void putDouble(String key, double value) {
        /* 默认没有保存 double 的功能，将double转为字符串后保存 */
        edit().putString(key, String.valueOf(value)).apply();
    }

    /**
     * 获取双精度型记录
     *
     * @param key      名称
     * @param defValue 默认值
     * @return 值
     */
    public static double getDouble(String key, double defValue) {
        /* 默认不能取double，将字符串解析为doueble */
        String result = getString(key, String.valueOf(defValue));
        return Double.valueOf(result);
    }

    /**
     * 获取整型记录
     *
     * @param key      名称
     * @param defValue 默认值
     * @return 值
     */
    public static int getInt(String key, int defValue) {
        return sp.getInt(key, defValue);
    }

    /**
     * 获取字符串记录
     *
     * @param key      名称
     * @param defValue 默认值
     * @return 值
     */
    public static String getString(String key, String defValue) {
        return sp.getString(key, defValue);
    }

    /**
     * 获取浮点型记录
     *
     * @param key      名称
     * @param defValue 默认值
     * @return 值
     */
    public static float getFloat(String key, float defValue) {
        return sp.getFloat(key, defValue);
    }

    /**
     * 获取布尔型记录
     *
     * @param key      名称
     * @param defValue 默认值
     * @return 值
     */
    public static boolean getBoolean(String key, boolean defValue) {
        return sp.getBoolean(key, defValue);
    }

    /**
     * 获取长整型记录
     *
     * @param key      名称
     * @param defValue 默认值
     * @return 值
     */
    public static long getLong(String key, long defValue) {
        return sp.getLong(key, defValue);
    }

    /**
     * 获取sp编辑器
     *
     * @return sp编辑器
     */
    private static SharedPreferences.Editor edit() {
        return sp.edit();
    }
}
