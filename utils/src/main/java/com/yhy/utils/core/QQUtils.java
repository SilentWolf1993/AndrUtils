package com.yhy.utils.core;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import java.util.List;

/**
 * author : 颜洪毅
 * e-mail : yhyzgn@gmail.com
 * time   : 2017-11-11 14:34
 * version: 1.0.0
 * desc   : QQ工具类
 */
public class QQUtils {

    private QQUtils() {
        throw new UnsupportedOperationException("Can not instantiate utils class.");
    }

    /**
     * 判断是否安装了QQ
     *
     * @param context 上下文对象
     * @return 是否安装了QQ
     */
    public static boolean isInstalled(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 跳往聊天页面
     *
     * @param context 上下文对象
     * @param qqNo    对方QQ号
     */
    public static void toChat(Context context, String qqNo) {
        String url = "mqqwpa://im/chat?chat_type=wpa&uin=" + qqNo;
        open(context, url);
    }

    /**
     * 跳往QQ联系人简介页面
     *
     * @param context 上下文对象
     * @param qqNo    对方QQ号
     */
    public static void toIntro(Context context, String qqNo) {
        String url = "mqqapi://card/show_pslcard?src_type=internal&version=1&uin=" + qqNo + "&card_type=person&source=qrcode";
    }

    /**
     * 跳往QQ群简介页面
     *
     * @param context 上下文对象
     * @param qqNo    QQ群号
     */
    public static void toGroupIntro(Context context, String qqNo) {
        String url = "mqqapi://card/show_pslcard?src_type=internal&version=1&uin=" + qqNo + "&card_type=group&source=qrcode";
    }

    /**
     * 打开页面
     *
     * @param context 上下文对象
     * @param url     url
     */
    private static void open(Context context, String url) {
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }
}
