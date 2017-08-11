package com.yhy.utils.core;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Process;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.util.Iterator;
import java.util.List;

/**
 * 系统工具类
 */
public class SystemUtils {
    private static final String TAG = "SystemUtils";

    private SystemUtils() {
        throw new IllegalStateException("Can not instantiate class SystemUtils.");
    }

    /**
     * 获取版本名称
     *
     * @param ctx 上下文对象
     * @return 版本名称
     */
    public static String getVersionName(Context ctx) {
        PackageManager packageManager = ctx.getPackageManager();
        try {
            // 得到apk的功能清单文件:为了防止出错直接使用getPackageName()方法获得包名
            PackageInfo packageInfo = packageManager.getPackageInfo(ctx.getPackageName(), 0);
            // 返回版本名称
            return packageInfo.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取版本号
     *
     * @param ctx 上下文对象
     * @return 版本号
     */
    public static int getVersionCode(Context ctx) {
        PackageManager packageManager = ctx.getPackageManager();
        try {
            // 得到apk的功能清单文件:为了防止出错直接使用getPackageName()方法获得包名
            PackageInfo packageInfo = packageManager.getPackageInfo(ctx.getPackageName(), 0);
            // 返回版本号
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取设备号
     *
     * @param ctx 上下文对象
     * @return 设备号
     */
    public static String getDeviceId(Context ctx) {
        // TelephonyManager tm = (TelephonyManager)
        // ctx.getSystemService(Context.TELEPHONY_SERVICE);
        // return tm.getDeviceId();
        // 以上方法有时候获取不到
        return Secure.getString(ctx.getContentResolver(), Secure.ANDROID_ID);
    }

    /**
     * 获取当前APP的名称
     *
     * @param ctx 上下文对象
     * @return 当前APP的名称
     */
    public static String getAppName(Context ctx) {
        PackageManager pm = ctx.getPackageManager();
        ApplicationInfo ai = null;
        try {
            ai = pm.getApplicationInfo(ctx.getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        String applicationName = (String) pm.getApplicationLabel(ai);
        return applicationName;
    }

    /**
     * 获取进程id
     *
     * @return 进程id
     */
    public static int getProcessId() {
        return Process.myPid();
    }

    /**
     * 获取进程名称
     *
     * @param ctx 上下文对象
     * @return 进程名称
     */
    public static String getProcessName(Context ctx) {
        int pid = getProcessId();
        String processName = null;
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> l = am.getRunningAppProcesses();
        Iterator<ActivityManager.RunningAppProcessInfo> i = l.iterator();
        PackageManager pm = ctx.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = i.next();
            try {
                if (info.pid == pid) {
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }

    /**
     * 获取设备类型
     *
     * @param ctx 上下文对象
     * @return 设备类型
     */
    public static String getDeviceType(Context ctx) {
        return Build.MODEL;
    }

    /**
     * 安装apk
     *
     * @param ctx 上下文对象
     * @param apk apk文件
     */
    public static void installApk(Context ctx, File apk) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(apk), "application/vnd.android.package-archive");
        ctx.startActivity(intent);
    }

    /**
     * 判断是否注册过广播
     *
     * @param ctx    上下文
     * @param action 广播Action
     * @return 是否注册过
     */
    public static boolean isReceiverRegisted(Context ctx, String action) {
        Intent intent = new Intent();
        intent.setAction(action);
        PackageManager pm = ctx.getPackageManager();
        List<ResolveInfo> resolveInfoList = pm.queryBroadcastReceivers(intent, 0);
        if (resolveInfoList != null && !resolveInfoList.isEmpty()) {
            //查询到相应的BroadcastReceiver
            return true;
        }
        return false;
    }

    /**
     * 判断某个Activity是否在前台
     *
     * @param context 上下文对象
     * @param clazz   Activity字节码对象
     * @return 是否在前台
     */
    public static boolean isForeground(Context context, Class<?> clazz) {
        if (context == null || null == clazz) {
            return false;
        }

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
//            Log.i(TAG, clazz.getName());
//            Log.i(TAG, cpn.getClassName());
            if (clazz.getName().equals(cpn.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取ApplicationId
     *
     * @param ctx 上下文对象
     * @return ApplicationId
     */
    public static String getApplicationId(Context ctx) {
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = ctx.getPackageManager().getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
            if (applicationInfo == null) {
                throw new IllegalArgumentException("get application info = null, has no meta data! ");
            }
            return applicationInfo.metaData.getString("APP_ID");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 返回当前的应用是否处于前台显示状态
     *
     * @param ctx         上下文对象
     * @param packageName 包名
     * @return 是否处于前台运行
     */
    public static boolean isTopProcess(Context ctx, String packageName) {
        ActivityManager am = (ActivityManager) ctx.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> list = am.getRunningAppProcesses();
        if (list.size() == 0) return false;
        for (ActivityManager.RunningAppProcessInfo process : list) {
//            Log.d("isTopActivity", Integer.toString(process.importance));
//            Log.d("isTopActivity", process.processName);
            /*
            在6.0/7.0等新版本中 可能还有另外几种状态:
            1.RunningAppProcessInfo.IMPORTANCE_TOP_SLEEPING(应用在前台时锁屏幕)，RunningAppProcessInfo.IMPORTANCE_FOREGROUND_SERVICE(应用开启了服务,然后锁屏幕,此时服务还是在前台运行)
            可以根据自己的实际情况决定上面列出的2个状态,是否算作前台状态;
             */
            if (process.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && process.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 模拟键盘点击事件
     *
     * @param keyCode keyCode
     */
    public static void sendKeyCode(final int keyCode) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 创建一个Instrumentation对象
                    Instrumentation inst = new Instrumentation();
                    // 调用inst对象的按键模拟方法
                    inst.sendKeyDownUpSync(keyCode);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
