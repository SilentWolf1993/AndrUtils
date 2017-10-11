package com.yhy.andrutils;

import android.app.Application;
import android.content.Context;
import android.widget.ImageView;

import com.yhy.utils.core.AndrUtils;

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
        AndrUtils.init(this);

        // 初始化图片加载工具类中的加载器
        AndrUtils.initImgLoader(new AndrUtils.ImgLoader() {
            @Override
            public <T> void load(Context ctx, ImageView iv, T model) {
                // 图片加载方案实现
            }
        });
    }
}
