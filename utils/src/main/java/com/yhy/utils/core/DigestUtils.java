package com.yhy.utils.core;

import android.text.TextUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 加密工具类
 */
public class DigestUtils {

    private DigestUtils() {
        throw new RuntimeException("Can not create instance for class DigestUtils.");
    }

    /**
     * md5加密
     *
     * @param str 要加密的字符串
     * @return 加密后的字符串
     */
    public static String md5(String str) {
        StringBuffer sb = new StringBuffer();
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] result = digest.digest(str.getBytes());
            for (byte b : result) {
                int i = b & 0xff;
                String hexString = Integer.toHexString(i);
                if (hexString.length() < 2)
                    hexString = "0" + hexString;
                sb.append(hexString);
            }
        } catch (NoSuchAlgorithmException e) {
            // 如果找不到MD5加密方法，就报异常
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * 异或加密字符串
     *
     * @param original 原始字符串
     * @param key      异或key
     * @return 异或后的字符串
     */
    public static String xor(String original, int key) {
        if (!TextUtils.isEmpty(original)) {
            char[] chs = original.toCharArray();
            StringBuffer sb = new StringBuffer();
            for (char c : chs) {
                char ch = (char) (c ^ key);
                sb.append(ch);
            }
            return sb.toString();
        }
        return null;
    }
}
