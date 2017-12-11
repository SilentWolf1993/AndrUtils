package com.yhy.utils.core;

import android.annotation.SuppressLint;
import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * author : 颜洪毅
 * e-mail : yhyzgn@gmail.com
 * time   : 2017-10-11 17:05
 * version: 1.0.0
 * desc   : Properties工具类
 */
public class PropUtils {
    @SuppressLint("StaticFieldLeak")
    private static Context ctx;

    private static Map<Object, Properties> propMap;

    private PropUtils() {
        throw new UnsupportedOperationException("Can not create instance for class PropUtils.");
    }

    /**
     * 初始化
     *
     * @param context 上下文对象
     */
    public static void init(Context context) {
        ctx = context;
        propMap = new HashMap<>();
    }

    /**
     * 加载properties文件
     *
     * @param tag        标记，一般传this即可
     * @param assetsName assets目录中的properties文件
     * @throws IOException IOException
     */
    public static void load(Object tag, String assetsName) throws IOException {
        load(tag, ctx.getResources().getAssets().open(assetsName));
    }

    /**
     * 加载properties文件
     *
     * @param tag 标记，一般传this即可
     * @param is  properties文件输入流
     * @throws IOException IOException
     */
    public static void load(Object tag, InputStream is) throws IOException {
        if (null == tag || null == is) {
            throw new IllegalArgumentException("tag or is can not be null.");
        }

        Properties prop = new Properties();
        prop.load(is);
        propMap.put(tag, prop);
    }

    /**
     * 获取属性值
     *
     * @param tag 标记，一般传this即可
     * @param key 属性名称
     * @return 属性值
     */
    public static String get(Object tag, String key) {
        return get(tag, key, "");
    }

    /**
     * 获取属性值
     *
     * @param tag      标记，一般传this即可
     * @param key      属性名称
     * @param defValue 默认值
     * @return 属性值
     */
    public static String get(Object tag, String key, String defValue) {
        for (Map.Entry<Object, Properties> et : propMap.entrySet()) {
            if (et.getKey() == tag) {
                return et.getValue().getProperty(key, defValue);
            }
        }
        return null;
    }
}
