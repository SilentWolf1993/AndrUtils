package com.yhy.utils.core;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * author : 颜洪毅
 * e-mail : yhyzgn@gmail.com
 * time   : 2017-10-16 9:08
 * version: 1.0.0
 * desc   : 接口工具类
 */
public class APIUtils {
    private final static String DEF_API_ASSETS_NAME = "api-assets.properties";
    private final static String DEF_API_HOST_KEY = "api.host";
    @SuppressLint("StaticFieldLeak")
    private static Context ctx;
    private static String apiHost;
    private static StringBuffer sb;
    private static Map<String, String> cacheMap = new HashMap<>();

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
        sb = new StringBuffer();
    }

    /**
     * 按properties中的key获取值
     *
     * @param key key
     * @return 值
     */
    public static String get(String key) {
        String url = PropUtils.get(APIUtils.class, key);
        if (RegexUtils.match(url, ".*?((\\$\\{.*?\\}).*?)+.*?")) {
            Pattern compile = Pattern.compile(".*?((\\$\\{.*?\\}).*?)+.*?");
            Matcher matcher = compile.matcher(url);

            String group, value;
            while (matcher.find()) {
                group = matcher.group(1);
                if (cacheMap.containsKey(group)) {
                    value = cacheMap.get(group);
                } else {
                    value = get(group.substring(2, group.length() - 1));
                    cacheMap.put(group, value);
                }
                url = url.replace(group, value);
            }
        }
        cacheMap.clear();
        return url;
    }

    /**
     * 设置属性值
     *
     * @param key   属性名称
     * @param value 属性值
     */
    public static void set(String key, String value) {
        PropUtils.set(APIUtils.class, key, value);
    }

    /**
     * 设置属性值
     *
     * @param key   属性名称
     * @param value 属性值
     */
    public static void set(String key, int value) {
        PropUtils.set(APIUtils.class, key, value);
    }

    /**
     * 设置属性值
     *
     * @param key   属性名称
     * @param value 属性值
     */
    public static void set(String key, long value) {
        PropUtils.set(APIUtils.class, key, value);
    }

    /**
     * 设置属性值
     *
     * @param key   属性名称
     * @param value 属性值
     */
    public static void set(String key, double value) {
        PropUtils.set(APIUtils.class, key, value);
    }

    /**
     * 设置属性值
     *
     * @param key   属性名称
     * @param value 属性值
     */
    public static void set(String key, Object value) {
        PropUtils.set(APIUtils.class, key, value);
    }

    /**
     * 根据接口子url对应properties中的key获取api地址
     *
     * @param apiKey 子url对应的key
     * @return 完整api
     * @see APIUtils#getByKey(String)
     */
    @Deprecated
    public static String getApiByKey(String apiKey) {
        return getApiByUrl(get(apiKey));
    }

    /**
     * 根据接口子url对应properties中的key获取api地址
     *
     * @param hostKey 根url对应的key
     * @param apiKey  子url对应的key
     * @return 完整api
     * @see APIUtils#getByKey(String, String)
     */
    @Deprecated
    public static String getApiByKey(String hostKey, String apiKey) {
        return getApiByUrl(get(hostKey), get(apiKey));
    }

    /**
     * 根据接口子url对应properties中的key获取api地址
     *
     * @param key 子url对应的key
     * @return 完整api
     */
    public static String getByKey(String key) {
        return getApiByKey(key);
    }

    /**
     * 根据接口子url对应properties中的key获取api地址
     *
     * @param hostKey 根url对应的key
     * @param key     子url对应的key
     * @return 完整api
     */
    public static String getByKey(String hostKey, String key) {
        return getApiByKey(hostKey, key);
    }

    /**
     * 根据接口子url获取api地址
     *
     * @param subUrl 子url
     * @return 完整api
     * @see APIUtils#getByUrl(String)
     */
    @Deprecated
    public static String getApiByUrl(String subUrl) {
        return getApiByUrl(apiHost, subUrl);
    }

    /**
     * 根据接口子url获取api地址
     *
     * @param host   host
     * @param subUrl 子url
     * @return 完整api
     * @see APIUtils#getByUrl(String, String)
     */
    @Deprecated
    public static String getApiByUrl(String host, String subUrl) {
        if (TextUtils.isEmpty(host)) {
            throw new IllegalStateException("Api host can not be null or empty.");
        }
        if (TextUtils.isEmpty(subUrl)) {
            return host;
        }
        sb.delete(0, sb.length());
        return sb.append(host).append(subUrl).toString();
    }

    /**
     * 根据接口子url获取api地址
     *
     * @param subUrl 子url
     * @return 完整api
     */
    public static String getByUrl(String subUrl) {
        return getApiByUrl(subUrl);
    }

    /**
     * 根据接口子url获取api地址
     *
     * @param host   host
     * @param subUrl 子url
     * @return 完整api
     */
    public static String getByUrl(String host, String subUrl) {
        return getApiByUrl(host, subUrl);
    }

    /**
     * 加载属性文件
     *
     * @throws Exception Assets文件异常
     */
    private static void load() throws Exception {
        // 从meta中获取api配置文件的名称
        String assets = MetaUtils.getString("API_ASSETS");
        String host = MetaUtils.getString("API_HOST");
        // 默认文件名称
        if (TextUtils.isEmpty(assets)) {
            assets = DEF_API_ASSETS_NAME;
        }
        // 设置api-host
        if (TextUtils.isEmpty(host)) {
            host = DEF_API_HOST_KEY;
        }
        PropUtils.load(APIUtils.class, assets);
        apiHost = get(host);
    }
}
