package com.yhy.utils.core;

import android.content.Context;
import android.support.annotation.Nullable;

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
public class YamlUtils {
    private static Context ctx;
    private static final Properties PROPERTIES;
    private static final Map<String, Object> DOLLARS_MAP;

    static {
        PROPERTIES = new Properties() {
            @Override
            public String getProperty(String key) {
                Object value = get(key);
                return (value != null ? value.toString() : null);
            }
        };
        DOLLARS_MAP = new LinkedHashMap<>();
    }

    private YamlUtils() {
        throw new UnsupportedOperationException("Can not create instance for class PropUtils.");
    }

    public static void init(Context ctx) {
        YamlUtils.ctx = ctx;
    }

    public static void load(String assetName) {
        if (null == ctx) {
            throw new IllegalStateException("Must call YamlUtils.init(Context ctx) method in Application's onCreate() at first.");
        }
        try {
            ctx.getResources().getAssets().open(assetName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void load(InputStream input) throws IOException {
        Yaml yaml = createYaml();
        process(yaml, input);
        LogUtils.i(PROPERTIES.values());
    }

    public static Object get(String key) {
        return PROPERTIES.get(key);
    }

    private static void process(Yaml yaml, InputStream input) throws IOException {
        Reader reader = new UnicodeReader(input);
        for (Object obj : yaml.loadAll(reader)) {
            PROPERTIES.putAll(getFlattenedMap(asMap(obj)));
        }
        reader.close();
        Object value;
        String strValue;
        for (Map.Entry<Object, Object> et : PROPERTIES.entrySet()) {
            value = et.getValue();
            if (value instanceof String) {
                strValue = (String) value;
                if (RegexUtils.match(strValue, ".*?((\\$\\{.*?\\}).*?)+.*?")) {
                    Pattern compile = Pattern.compile(".*?((\\$\\{.*?\\}).*?)+.*?");
                    Matcher matcher = compile.matcher(strValue);
                    String group;
                    Object temp;
                    while (matcher.find()) {
                        group = matcher.group(1);
                        if (DOLLARS_MAP.containsKey(group)) {
                            temp = DOLLARS_MAP.get(group);
                        } else {
                            temp = get(group.substring(2, group.length() - 1));
                            DOLLARS_MAP.put(group, temp);
                        }
                        strValue = strValue.replace(group, temp.toString());
                        et.setValue(strValue);
                        LogUtils.i(group, temp, value, strValue);
                    }
                }
            }
        }
    }

    private static Yaml createYaml() {
        LoaderOptions options = new LoaderOptions();
        options.setAllowDuplicateKeys(false);
        return new Yaml(options);
    }

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

    private static Map<String, Object> getFlattenedMap(Map<String, Object> source) {
        Map<String, Object> result = new LinkedHashMap<>();
        buildFlattenedMap(result, source, null);
        return result;
    }

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

    private static boolean hasText(@Nullable String str) {
        return (str != null && !str.isEmpty() && containsText(str));
    }

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
