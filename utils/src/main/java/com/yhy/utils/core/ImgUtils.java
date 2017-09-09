package com.yhy.utils.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.IdRes;
import android.widget.ImageView;

/**
 * Created by HongYi Yan on 2017/4/6 16:48.
 */
public class ImgUtils {

    private static ImgLoader mLoader;

    private ImgUtils() {
        throw new RuntimeException("Can not instantiate ImgUtils.");
    }

    /**
     * 初始化工具类
     *
     * @param loader 图片加载器
     */
    public static void init(ImgLoader loader) {
        mLoader = loader;
    }

    /**
     * 加载图片
     *
     * @param iv    图片控件
     * @param model 图片地址
     * @param <T>   图片地址
     */
    public static <T> void load(ImageView iv, T model) {
        if (null == mLoader) {
            throw new RuntimeException("You must call method init(ImgLoading loader) to initializing in Application.");
        }
        mLoader.load(iv.getContext(), iv, model);
    }

    /**
     * 图片加载器
     */
    public static abstract class ImgLoader {
        /**
         * 加载图片
         *
         * @param ctx   上下文对象
         * @param iv    图片控件
         * @param model 数据源
         * @param <T>   数据源类型
         */
        public abstract <T> void load(Context ctx, ImageView iv, T model);

        /**
         * 根据数据源获取到bitmap对象
         *
         * @param ctx   上下文对象
         * @param model 数据源
         * @param <T>   数据源类型
         * @return 获取到的图片
         */
        public <T> Bitmap get(Context ctx, T model) {
            return null;
        }
    }
}
