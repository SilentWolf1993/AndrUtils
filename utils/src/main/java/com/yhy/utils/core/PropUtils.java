package com.yhy.utils.core;

import android.annotation.SuppressLint;
import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * author : 颜洪毅
 * e-mail : yhyzgn@gmail.com
 * time   : 2017-10-11 17:05
 * version: 1.0.0
 * desc   : Properties工具类
 */
public abstract class PropUtils {

    @SuppressLint("StaticFieldLeak")
    private static Context ctx;
    private static final Properties PROP_PROPERTIES;
    private static final Map<String, String> PROP_DOLLARS_MAP;

    static {
        PROP_PROPERTIES = new Properties() {
            @Override
            public String getProperty(String key) {
                Object value = get(key);
                return (value != null ? value.toString() : null);
            }
        };
        PROP_DOLLARS_MAP = new LinkedHashMap<>();
    }

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
    }

    /**
     * 加载properties文件
     *
     * @param assetsName assets目录中的properties文件
     * @throws IOException IOException
     */
    public static void load(String assetsName) throws IOException {
        load(ctx.getResources().getAssets().open(assetsName));
    }

    /**
     * 加载properties文件
     *
     * @param is properties文件输入流
     * @throws IOException IOException
     */
    public static void load(InputStream is) throws IOException {
        if (null == is) {
            throw new IllegalArgumentException("Can not found the properties file.");
        }
        Properties prop = new Properties();
        prop.load(is);
        PROP_PROPERTIES.putAll(prop);
        process();
    }

    /**
     * 解析配置文件
     */
    private static void process() {
        Object value;
        String strValue;
        for (Map.Entry<Object, Object> et : PROP_PROPERTIES.entrySet()) {
            value = et.getValue();
            if (value instanceof String) {
                strValue = (String) value;
                if (RegexUtils.match(strValue, ".*?((\\$\\{.*?\\}).*?)+.*?")) {
                    Pattern compile = Pattern.compile(".*?((\\$\\{.*?\\}).*?)+.*?");
                    Matcher matcher = compile.matcher(strValue);
                    String group;
                    String temp;
                    while (matcher.find()) {
                        group = matcher.group(1);
                        if (PROP_DOLLARS_MAP.containsKey(group)) {
                            temp = null != PROP_DOLLARS_MAP.get(group) ? PROP_DOLLARS_MAP.get(group) : "null";
                        } else {
                            temp = get(group.substring(2, group.length() - 1));
                            PROP_DOLLARS_MAP.put(group, temp);
                        }
                        if (null == temp) {
                            temp = "null";
                        }
                        strValue = strValue.replace(group, temp);
                        et.setValue(strValue);
                    }
                }
            }
        }
    }

    /**
     * 获取 Integer 类型
     *
     * @param key 名称
     * @return 值
     */
    public static Integer getInteger(String key) {
        return Integer.valueOf(getString(key, "0"));
    }

    /**
     * 获取 Boolean 类型
     *
     * @param key 名称
     * @return 值
     */
    public static Boolean getBoolean(String key) {
        return Boolean.valueOf(getString(key, "false"));
    }

    /**
     * 获取 Long 类型
     *
     * @param key 名称
     * @return 值
     */
    public static Long getLong(String key) {
        return Long.valueOf(getString(key, "0"));
    }

    /**
     * 获取 Float 类型
     *
     * @param key 名称
     * @return 值
     */
    public static Float getFloat(String key) {
        return Float.valueOf(getString(key, "0"));
    }

    /**
     * 获取 Double 类型
     *
     * @param key 名称
     * @return 值
     */
    public static Double getDouble(String key) {
        return Double.valueOf(getString(key, "0"));
    }

    /**
     * 获取 String 类型
     *
     * @param key 名称
     * @return 值
     */
    public static String getString(String key) {
        return PROP_PROPERTIES.getProperty(key);
    }

    /**
     * 获取 String 类型
     *
     * @param key 名称
     * @return 值
     */
    public static String getString(String key, String defValue) {
        return PROP_PROPERTIES.getProperty(key, defValue);
    }

    /**
     * 获取属性值
     *
     * @param key 属性名称
     * @return 属性值
     */
    public static String get(String key) {
        return get(key, "");
    }

    /**
     * 获取属性值
     *
     * @param key      属性名称
     * @param defValue 默认值
     * @return 属性值
     */
    public static String get(String key, String defValue) {
        return PROP_PROPERTIES.getProperty(key, defValue);
    }

    /**
     * 获取所有配置信息
     *
     * @return 所有配置信息
     */
    public static Properties get() {
        return PROP_PROPERTIES;
    }

    /**
     * 设置属性值
     *
     * @param key   属性名称
     * @param value 属性值
     */
    public static void set(String key, Object value) {
        if (null == value || "null".equals(value)) {
            PROP_PROPERTIES.remove(key);
            return;
        }
        PROP_PROPERTIES.setProperty(key, value.toString());
    }
}
