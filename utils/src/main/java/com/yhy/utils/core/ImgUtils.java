package com.yhy.utils.core;

import android.content.Context;
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
    public interface ImgLoader {
        <T> void load(Context ctx, ImageView iv, T model);
    }
}
