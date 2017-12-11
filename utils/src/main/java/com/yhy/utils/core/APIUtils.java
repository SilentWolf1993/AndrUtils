package com.yhy.utils.core;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import java.io.IOException;

/**
 * author : 颜洪毅
 * e-mail : yhyzgn@gmail.com
 * time   : 2017-10-16 9:08
 * version: 1.0.0
 * desc   : 接口工具类
 */
public class APIUtils {
    @SuppressLint("StaticFieldLeak")
    private static Context ctx;
    private static StringBuffer sb;

    private APIUtils() {
        throw new UnsupportedOperationException("Can not instantiate utils class.");
    }

    /**
     * 初始化
     *
     * @param context 上下文对象
     * @throws IOException Assets文件异常
     */
    public static void init(Context context) throws IOException {
        ctx = context;
        load();
        sb = new StringBuffer();
    }

    /**
     * 按properties中的key获取值
     *
     * @param key key
     * @return 值
     */
    public static String get(String key) {
        return PropUtils.get(APIUtils.class, key);
    }

    /**
     * 根据接口子url对应properties中的key获取api地址
     *
     * @param apiKey 子url对应的key
     * @return 完整api
     */
    public static String getApiByKey(String apiKey) {
        return getApiByUrl(get(apiKey));
    }

    /**
     * 根据接口子url获取api地址
     *
     * @param subUrl 子url
     * @return 完整api
     */
    public static String getApiByUrl(String subUrl) {
        String apiHost = PropUtils.get(APIUtils.class, "api.host");
        if (TextUtils.isEmpty(apiHost)) {
            throw new IllegalStateException("Api host can not be null or empty.");
        }
        if (TextUtils.isEmpty(subUrl)) {
            return apiHost;
        }
        sb.delete(0, sb.length());
        return sb.append(apiHost).append(subUrl).toString();
    }

    /**
     * 加载属性文件
     *
     * @throws IOException Assets文件异常
     */
    private static void load() throws IOException {
        // 从meta中获取api配置文件的名称
        String apiAssets = null;
        try {
            apiAssets = MetaUtils.getString("API_ASSETS");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        // 默认文件名称
        if (TextUtils.isEmpty(apiAssets)) {
            apiAssets = "api_assets.properties";
        }
        PropUtils.load(APIUtils.class, apiAssets);
    }
}
