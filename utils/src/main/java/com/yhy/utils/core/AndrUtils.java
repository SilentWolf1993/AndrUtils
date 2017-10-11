package com.yhy.utils.core;

import android.content.Context;
import android.widget.ImageView;

/**
 * author : 颜洪毅
 * e-mail : yhyzgn@gmail.com
 * time   : 2017-10-11 16:40
 * version: 1.0.0
 * desc   : 所有utils上下文初始化
 */
public class AndrUtils {

    /**
     * 初始化工具类
     *
     * @param ctx 上下文对象
     */
    public static void init(Context ctx) {
        MetaUtils.init(ctx);
        PropUtils.init(ctx);
        ResUtils.init(ctx);
        SystemUtils.init(ctx);
        ToastUtils.init(ctx);
    }

    /**
     * 初始化图片加载器
     *
     * @param loader 图片加载器
     */
    public static void initImgLoader(ImgLoader loader) {
        ImgUtils.init(loader);
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
    }
}
