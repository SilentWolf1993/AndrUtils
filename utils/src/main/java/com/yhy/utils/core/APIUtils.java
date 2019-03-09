package com.yhy.utils.core;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;

import java.io.IOException;
import java.util.Properties;

/**
 * author : 颜洪毅
 * e-mail : yhyzgn@gmail.com
 * time   : 2017-10-16 9:08
 * version: 1.0.0
 * desc   : 接口工具类
 */
public class APIUtils {
    private final static String DEF_API_ASSETS_NAME = "api-assets.properties";
    private static final Properties PROPERTIES;
    @SuppressLint("StaticFieldLeak")
    private static Context ctx;
    private static String apiHostKey;

    static {
        PROPERTIES = new Properties() {
            @Override
            public String getProperty(String key) {
                Object value = get(key);
                return (value != null ? value.toString() : null);
            }
        };
    }

    private APIUtils() {
        throw new UnsupportedOperationException("Can not instantiate utils class.");
    }

    /**
     * 初始化
     *
     * @param context 上下文对象
     * @throws IOException Assets文件异常
     */
    public static void init(Context context) throws Exception {
        ctx = context;
        load();
    }

    /**
     * 设置属性值
     *
     * @param key   属性名称
     * @param value 属性值
     */
    public static void set(String key, Object value) {
        PROPERTIES.setProperty(key, value.toString());
    }

    /**
     * 根据接口子url对应properties中的key获取api地址
     *
     * @param key 子url对应的key
     * @return 完整api
     */
    public static String get(String key) {
        return get(apiHostKey, key);
    }

    /**
     * 根据接口子url对应properties中的key获取api地址
     *
     * @param hostKey 根url对应的key
     * @param key     子url对应的key
     * @return 完整api
     */
    public static String get(String hostKey, String key) {
        return PROPERTIES.getProperty(hostKey, "") + PROPERTIES.getProperty(key, "");
    }

    /**
     * 加载属性文件
     *
     * @throws Exception Assets文件异常
     */
    private static void load() throws Exception {
        // 从meta中获取api配置文件的名称
        String assets = MetaUtils.getString("API_ASSETS");
        apiHostKey = MetaUtils.getString("API_HOST");
        // 默认文件名称
        if (TextUtils.isEmpty(assets)) {
            assets = DEF_API_ASSETS_NAME;
        }

        if (assets.endsWith(".yml") || assets.endsWith(".yaml")) {
            // yaml 格式
            YamlUtils.load(assets);
            PROPERTIES.putAll(YamlUtils.get());
        } else {
            // 其他格式（properties）
            PropUtils.load(assets);
            PROPERTIES.putAll(PropUtils.get());
        }
    }
}
