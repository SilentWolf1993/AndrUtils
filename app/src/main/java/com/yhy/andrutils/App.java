package com.yhy.andrutils;

import android.app.Application;
import android.content.Context;
import android.widget.ImageView;

import com.yhy.utils.core.ImgUtils;
import com.yhy.utils.manager.UtilsManager;

/**
 * author : 颜洪毅
 * e-mail : yhyzgn@gmail.com
 * time   : 2017-10-11 16:45
 * version: 1.0.0
 * desc   :
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化工具类
        UtilsManager.getInstance()
                .init(this)
                .initImgLoader(new ImgUtils.ImgLoader() {
                    @Override
                    public <T> void load(Context ctx, ImageView iv, T model) {
                        // ... resolve image loader
                    }
                });
    }
}
