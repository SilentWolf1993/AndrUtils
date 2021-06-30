package com.yhy.utils.core;

import android.text.TextUtils;

import java.util.regex.Pattern;

/**
 * author : 颜洪毅
 * e-mail : yhyzgn@gmail.com
 * time   : 2018-02-02 15:29
 * version: 1.0.0
 * desc   : 正则工具类
 */
public abstract class RegexUtils {
    // 数字
    private final static String REG_NUMBER = "^\\d+$";
    // 手机号码
    private final static String REG_MOBILE = "^1[3,4,5,6,7,8,9]\\d{9}$";
    // 邮箱
    private final static String REG_EMAIL = "^(\\w+)(\\.\\w+)*@(\\w{2,8}\\.){1,3}\\w{2,8}$";
    // 网址
    private final static String REG_URL = "^https?://(\\w+)+(\\.\\w+)+(/\\w+)*/?$";

    private RegexUtils() {
        throw new UnsupportedOperationException("Can not instantiate utils class.");
    }

    /**
     * 检查是否是数字
     *
     * @param text 待检查文本
     * @return 是否是数字
     */
    public static boolean isNumber(String text) {
        return match(text, REG_NUMBER);
    }

    /**
     * 检查是否是手机号码
     *
     * @param text 待检查文本
     * @return 是否是手机号码
     */
    public static boolean isMobile(String text) {
        return match(text, REG_MOBILE);
    }

    /**
     * 检查是否是邮箱地址
     *
     * @param text 待检查文本
     * @return 是否是邮箱地址
     */
    public static boolean isEmail(String text) {
        return match(text, REG_EMAIL);
    }

    /**
     * 检查是否是url
     *
     * @param text 待检查文本
     * @return 是否是url
     */
    public static boolean isUrl(String text) {
        return match(text, REG_URL);
    }

    /**
     * 检查字符串是否匹配
     *
     * @param text 待检查字符串
     * @param reg  匹配正则表达式
     * @return 是否匹配
     */
    public static boolean match(String text, String reg) {
        return !TextUtils.isEmpty(text) && Pattern.compile(reg).matcher(text).matches();
    }

    /**
     * 隐藏手机号中间四位
     *
     * @param mobile 手机号码
     * @return 结果
     */
    public static String hideMobile(String mobile) {
        if (isMobile(mobile)) {
            return mobile.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
        }
        return mobile;
    }
}
