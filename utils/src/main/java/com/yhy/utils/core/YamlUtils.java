package com.yhy.utils.core;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.Nullable;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.reader.UnicodeReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * author : 颜洪毅
 * e-mail : yhyzgn@gmail.com
 * time   : 2019-03-08 10:37
 * version: 1.0.0
 * desc   : Yaml 读取工具
 */
public abstract class YamlUtils {
    @SuppressLint("StaticFieldLeak")
    private static Context ctx;
    private static final Properties YAML_PROPERTIES;
    private static final Map<String, Object> YAML_DOLLARS_MAP;

    static {
        YAML_PROPERTIES = new Properties() {
            @Override
            public String getProperty(String key) {
                Object value = get(key);
                return (value != null ? value.toString() : null);
            }
        };
        YAML_DOLLARS_MAP = new LinkedHashMap<>();
    }

    private YamlUtils() {
        throw new UnsupportedOperationException("Can not create instance for class PropUtils.");
    }

    /**
     * 初始化
     *
     * @param ctx 上下文对象
     */
    public static void init(Context ctx) {
        YamlUtils.ctx = ctx;
    }

    /**
     * 加载配置文件
     *
     * @param assetName asset中文件名
     */
    public static void load(String assetName) {
        if (null == ctx) {
            throw new IllegalStateException("Must call YamlUtils.init(Context ctx) method in Application's onCreate() at first.");
        }
        try {
            load(ctx.getResources().getAssets().open(assetName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载配置文件
     *
     * @param input 文件输入流
     * @throws IOException IO异常
     */
    public static void load(InputStream input) throws IOException {
        Yaml yaml = createYaml();
        process(yaml, input);
    }

    /**
     * 获取 Integer 值
     *
     * @param key 名称
     * @return 值
     */
    public static Integer getInteger(String key) {
        return Integer.valueOf(getString(key, "0"));
    }

    /**
     * 获取 Boolean 值
     *
     * @param key 名称
     * @return 值
     */
    public static Boolean getBoolean(String key) {
        return Boolean.valueOf(getString(key, "false"));
    }

    /**
     * 获取 Long 值
     *
     * @param key 名称
     * @return 值
     */
    public static Long getLong(String key) {
        return Long.valueOf(getString(key, "0"));
    }

    /**
     * 获取 Float 值
     *
     * @param key 名称
     * @return 值
     */
    public static Float getFloat(String key) {
        return Float.valueOf(getString(key, "0"));
    }

    /**
     * 获取 Double 值
     *
     * @param key 名称
     * @return 值
     */
    public static Double getDouble(String key) {
        return Double.valueOf(getString(key, "0"));
    }

    /**
     * 获取 String 值
     *
     * @param key 名称
     * @return 值
     */
    public static String getString(String key) {
        return YAML_PROPERTIES.getProperty(key);
    }

    /**
     * 获取 String 值
     *
     * @param key 名称
     * @return 值
     */
    public static String getString(String key, String defValue) {
        return YAML_PROPERTIES.getProperty(key, defValue);
    }

    /**
     * 获取值
     *
     * @param key 名称
     * @return 值
     */
    public static Object get(String key) {
        return YAML_PROPERTIES.get(key);
    }

    /**
     * 获取所有配置信息
     *
     * @return 所有配置信息
     */
    public static Properties get() {
        return YAML_PROPERTIES;
    }

    /**
     * 设置属性值
     *
     * @param key   名称
     * @param value 值
     */
    public static void set(String key, Object value) {
        if (null == value || "null".equals(value)) {
            YAML_PROPERTIES.remove(key);
            return;
        }
        YAML_PROPERTIES.setProperty(key, value.toString());
    }

    /**
     * 解析配置文件
     *
     * @param yaml  yaml文件对象
     * @param input 输入流
     * @throws IOException IO异常
     */
    private static void process(Yaml yaml, InputStream input) throws IOException {
        Reader reader = new UnicodeReader(input);
        for (Object obj : yaml.loadAll(reader)) {
            YAML_PROPERTIES.putAll(getFlattenedMap(asMap(obj)));
        }
        reader.close();
        Object value;
        String strValue;
        for (Map.Entry<Object, Object> et : YAML_PROPERTIES.entrySet()) {
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
                        if (YAML_DOLLARS_MAP.containsKey(group)) {
                            temp = null != YAML_DOLLARS_MAP.get(group) ? YAML_DOLLARS_MAP.get(group).toString() : "null";
                        } else {
                            temp = getString(group.substring(2, group.length() - 1));
                            YAML_DOLLARS_MAP.put(group, temp);
                        }
                        strValue = strValue.replace(group, temp);
                        et.setValue(strValue);
                    }
                }
            }
        }
    }

    /**
     * 创建yaml对象
     *
     * @return yaml对象
     */
    private static Yaml createYaml() {
        LoaderOptions options = new LoaderOptions();
        options.setAllowDuplicateKeys(false);
        return new Yaml(options);
    }

    /**
     * 解析yaml文件为map类型
     *
     * @param object yaml节点对象
     * @return map结果集
     */
    private static Map<String, Object> asMap(Object object) {
        // YAML can have numbers as keys
        Map<String, Object> result = new LinkedHashMap<>();
        if (!(object instanceof Map)) {
            // A document can be a text literal
            result.put("document", object);
            return result;
        }

        Map<Object, Object> map = (Map<Object, Object>) object;

        Object key;
        Object value;
        for (Map.Entry<Object, Object> et : map.entrySet()) {
            key = et.getKey();
            value = et.getValue();
            if (value instanceof Map) {
                value = asMap(value);
                et.setValue(value);
            }
            if (key instanceof CharSequence) {
                result.put(key.toString(), value);
            } else {
                // It has to be a map key in this case
                result.put("[" + key.toString() + "]", value);
            }
        }
        return result;
    }

    /**
     * 将map结果集拼接成properties格式
     *
     * @param source map结果集
     * @return 拼接结果
     */
    private static Map<String, Object> getFlattenedMap(Map<String, Object> source) {
        Map<String, Object> result = new LinkedHashMap<>();
        buildFlattenedMap(result, source, null);
        return result;
    }

    /**
     * 将map结果集拼接成properties格式
     *
     * @param result 拼接的结果集
     * @param source map结果集
     * @param path   父节点
     */
    private static void buildFlattenedMap(Map<String, Object> result, Map<String, Object> source, @Nullable String path) {
        String key;
        Object value;
        for (Map.Entry<String, Object> et : source.entrySet()) {
            key = et.getKey();
            value = et.getValue();

            if (hasText(path)) {
                if (key.startsWith("[")) {
                    key = path + key;
                } else {
                    key = path + '.' + key;
                }
            }
            if (value instanceof String) {
                result.put(key, value);
            } else if (value instanceof Map) {
                // Need a compound key
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) value;
                buildFlattenedMap(result, map, key);
            } else if (value instanceof Collection) {
                // Need a compound key
                @SuppressWarnings("unchecked")
                Collection<Object> collection = (Collection<Object>) value;
                if (collection.isEmpty()) {
                    result.put(key, "");
                } else {
                    int count = 0;
                    for (Object object : collection) {
                        buildFlattenedMap(result, Collections.singletonMap("[" + (count++) + "]", object), key);
                    }
                }
            } else {
                result.put(key, (value != null ? value : ""));
            }
        }
    }

    /**
     * 判断是否有内容
     *
     * @param str 待测内容
     * @return 是否有内容
     */
    private static boolean hasText(@Nullable String str) {
        return (str != null && !str.isEmpty() && containsText(str));
    }

    /**
     * 判断是否包含内容
     *
     * @param str 待测内容
     * @return 是否有内容
     */
    private static boolean containsText(CharSequence str) {
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }
}
