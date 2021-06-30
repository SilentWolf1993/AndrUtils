package com.yhy.utils.core;

import android.content.Context;
import android.widget.ImageView;

/**
 * author : 颜洪毅
 * e-mail : yhyzgn@gmail.com
 * time   : 2017-10-16 10:01
 * version: 1.0.0
 * desc   : 图片工具类
 */
public abstract class ImgUtils {

    private static ImgLoader imgLoader;

    private ImgUtils() {
        throw new UnsupportedOperationException("Can not instantiate ImgUtils.");
    }

    /**
     * 初始化工具类
     *
     * @param loader 图片加载器
     */
    public static void init(ImgLoader loader) {
        imgLoader = loader;
    }

    /**
     * 加载图片
     *
     * @param iv    图片控件
     * @param model 图片地址
     * @param <T>   图片地址
     */
    public static <T> void load(ImageView iv, T model) {
        if (null == imgLoader) {
            throw new RuntimeException("You must call method init(ImgLoading loader) to initializing in Application.");
        }
        imgLoader.load(iv.getContext(), iv, model);
    }

    /**
     * 图片加载器
     */
    public interface ImgLoader {
        /**
         * 加载图片
         *
         * @param ctx   上下文对象
         * @param iv    图片控件
         * @param model 数据源
         * @param <T>   数据源类型
         */
        <T> void load(Context ctx, ImageView iv, T model);
    }
}
