package com.yhy.utils.manager;

import android.annotation.SuppressLint;
import android.app.Application;

import com.yhy.utils.core.APIUtils;
import com.yhy.utils.core.ActivityUtils;
import com.yhy.utils.core.CacheUtils;
import com.yhy.utils.core.CleanUtils;
import com.yhy.utils.core.FileUtils;
import com.yhy.utils.core.ImgUtils;
import com.yhy.utils.core.LogUtils;
import com.yhy.utils.core.MetaUtils;
import com.yhy.utils.core.PropUtils;
import com.yhy.utils.core.ResUtils;
import com.yhy.utils.core.SPUtils;
import com.yhy.utils.core.SysUtils;
import com.yhy.utils.core.ToastUtils;
import com.yhy.utils.core.ViewUtils;
import com.yhy.utils.helper.PermissionHelper;
import com.yhy.utils.helper.SMSCodeAutoFillHelper;

import java.io.IOException;

/**
 * author : 颜洪毅
 * e-mail : yhyzgn@gmail.com
 * time   : 2017-10-16 11:17
 * version: 1.0.0
 * desc   : 工具类管理器
 */
public class UtilsManager {
    @SuppressLint("StaticFieldLeak")
    private volatile static UtilsManager instance;

    private Application mApp;

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
     * @param app 上下文对象
     * @return 当前对象
     */
    public UtilsManager init(Application app) {
        return init(app, false);
    }

    /**
     * 初始化
     *
     * @param app          上下文对象
     * @param debugEnabled 是否是debug模式
     * @return 当前对象
     */
    public UtilsManager init(Application app, boolean debugEnabled) {
        mApp = app;

        // 初始化各工具类
        FileUtils.init(mApp);
        ToastUtils.init(mApp);
        ActivityUtils.init(mApp);
        MetaUtils.init(mApp);
        PropUtils.init(mApp);
        ResUtils.init(mApp);
        SysUtils.init(mApp);
        SPUtils.init(mApp);
        CacheUtils.init(mApp);
        CleanUtils.init(mApp);
        ViewUtils.init(mApp);

        PermissionHelper.getInstance().init(mApp);

        SMSCodeAutoFillHelper.getInstance().init(mApp);

        // Log工具类
        LogUtils.getConfig().setApp(mApp).setLogSwitch(debugEnabled).setGlobalTag(getClass().getSimpleName());

        // 初始化api工具类
        try {
            APIUtils.init(mApp);
        } catch (Exception e) {
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
