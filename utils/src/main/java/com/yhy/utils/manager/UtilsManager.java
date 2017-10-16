package com.yhy.utils.manager;

import android.content.Context;

import com.yhy.utils.core.APIUtils;
import com.yhy.utils.core.ImgUtils;
import com.yhy.utils.core.MetaUtils;
import com.yhy.utils.core.PropUtils;
import com.yhy.utils.core.ResUtils;
import com.yhy.utils.core.SPUtils;
import com.yhy.utils.core.SysUtils;
import com.yhy.utils.core.ToastUtils;

import java.io.IOException;

/**
 * author : 颜洪毅
 * e-mail : yhyzgn@gmail.com
 * time   : 2017-10-16 11:17
 * version: 1.0.0
 * desc   : 工具类管理器
 */
public class UtilsManager {
    private static UtilsManager instance;

    private Context mCtx;

    private UtilsManager() {
        if (null != instance) {
            throw new UnsupportedOperationException("Can not instantiate singleton class.");
        }
    }

    /**
     * 单例模式
     *
     * @return 单例实例
     */
    public static UtilsManager getInstance() {
        if (null == instance) {
            synchronized (UtilsManager.class) {
                if (null == instance) {
                    instance = new UtilsManager();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化
     *
     * @param ctx 上下文对象
     * @return 当前对象
     */
    public UtilsManager init(Context ctx) {
        mCtx = ctx;

        // 初始化各工具类
        MetaUtils.init(mCtx);
        PropUtils.init(mCtx);
        ResUtils.init(mCtx);
        SysUtils.init(mCtx);
        ToastUtils.init(mCtx);
        SPUtils.init(mCtx);

        // 初始化api工具类
        try {
            APIUtils.init(mCtx);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return this;
    }

    /**
     * 初始化图片工具类中的图片加载器
     *
     * @param loader 图片加载器
     * @return 当前对象
     */
    public UtilsManager initImgLoader(ImgUtils.ImgLoader loader) {
        ImgUtils.init(loader);
        return this;
    }
}
