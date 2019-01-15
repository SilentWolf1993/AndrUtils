package com.yhy.utils.core;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import java.io.File;

/**
 * author : 颜洪毅
 * e-mail : yhyzgn@gmail.com
 * time   : 2019-01-15 14:17
 * version: 1.0.0
 * desc   : 文件工具类
 */
public class FileUtils {
    @SuppressLint("StaticFieldLeak")
    private static Context mCtx;

    private FileUtils() {
        throw new UnsupportedOperationException("Can not instantiate ImgUtils.");
    }

    /**
     * 初始化，在Application入口调用
     *
     * @param ctx 上下文对象
     */
    public static void init(Context ctx) {
        mCtx = ctx;
    }

    /**
     * 获取文件后缀名
     *
     * @param filename 文件名
     * @return 文件后缀
     */
    public static String getExt(String filename) {
        if (!TextUtils.isEmpty(filename)) {
            if (filename.contains(".")) {
                return filename.substring(filename.lastIndexOf(".") + 1);
            }
            return "";
        }
        return null;
    }

    /**
     * 获取文件后缀名
     *
     * @param file 文件
     * @return 后缀
     */
    public static String getExt(File file) {
        return null == file ? null : getExt(file.getName());
    }

    /**
     * 获取文件MimeType
     *
     * @param filename 文件名
     * @return MimeType
     */
    public static String getMimeType(String filename) {
        String ext = getExt(filename);
        return TextUtils.isEmpty(ext) ? null : MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
    }

    /**
     * 获取文件MimeType
     *
     * @param file 文件
     * @return MimeType
     */
    public static String getMimeType(File file) {
        return null == file ? null : getMimeType(file.getName());
    }
}
