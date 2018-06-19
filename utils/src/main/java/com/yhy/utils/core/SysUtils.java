package com.yhy.utils.core;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.os.Bundle;
import android.os.Process;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.yhy.utils.helper.PermissionHelper;
import com.yhy.utils.provider.AUFileProvider;

import java.io.File;
import java.util.Iterator;
import java.util.List;

/**
 * author : 颜洪毅
 * e-mail : yhyzgn@gmail.com
 * time   : 2017-12-11 10:31
 * version: 1.0.0
 * desc   : 系统工具类
 */
public class SysUtils {
    @SuppressLint("StaticFieldLeak")
    private static Context ctx;

    private SysUtils() {
        throw new UnsupportedOperationException("Can not instantiate class SysUtils.");
    }

    /**
     * 初始化
     *
     * @param context 上下文对象
     */
    public static void init(Context context) {
        ctx = context;
    }

    /**
     * 获取版本名称
     *
     * @return 版本名称
     */
    public static String getVersionName() {
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
     * @return 版本号
     */
    public static int getVersionCode() {
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
     * @param callback 回调
     */
    public static void getDeviceId(final Callback<String> callback) {
        PermissionHelper.getInstance().permissions(Manifest.permission.READ_PHONE_STATE).request(new PermissionHelper.SimplePermissionCallback() {
            @SuppressLint({"MissingPermission", "HardwareIds"})
            @Override
            public void onGranted() {
                TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
                String imei = null;
                if (null != tm) {
                    // 设备唯一标识 IMEI
                    imei = tm.getDeviceId();
                    if (TextUtils.isEmpty(imei)) {
                        // IESI
                        imei = tm.getSubscriberId();
                    }
                }
                if (TextUtils.isEmpty(imei)) {
                    // pad标识
                    imei = Secure.getString(ctx.getContentResolver(), Secure.ANDROID_ID);
                }
                if (TextUtils.isEmpty(imei) && null != tm) {
                    imei = tm.getLine1Number();
                }
                if (TextUtils.isEmpty(imei)) {
                    imei = "Unknow";
                }
                callback.onResult(imei);
            }

            @Override
            public void onDenied() {
                callback.onResult("");
            }
        });
    }

    /**
     * 获取当前APP的名称
     *
     * @return 当前APP的名称
     */
    public static String getAppName() {
        PackageManager pm = ctx.getPackageManager();
        ApplicationInfo ai = null;
        try {
            ai = pm.getApplicationInfo(ctx.getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return (String) pm.getApplicationLabel(ai);
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
     * @return 进程名称
     */
    public static String getProcessName() {
        int pid = getProcessId();
        String processName = null;
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        if (null == am) {
            return null;
        }
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
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 获取设备类型
     *
     * @return 设备类型
     */
    public static String getDeviceType() {
        return Build.MODEL;
    }

    /**
     * 安装apk
     *
     * @param apk apk文件
     */
    public static void installApk(final File apk) {
        if (!canInstall()) {
            // Android 8.0+ 不能直接安装，需要请求权限
            PermissionHelper.getInstance().permissions(Manifest.permission.REQUEST_INSTALL_PACKAGES).request(new PermissionHelper.SimplePermissionCallback() {
                @Override
                public void onGranted() {
                    // 授权后重新调用安装方法
                    installApk(apk);
                }

                @Override
                public void onDenied() {
                    // 请求权限
                    InstallSettingsActivity.start(ctx, apk);
                }
            });
            return;
        }

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        //为了兼容Android 7.0+，只能结合FileProvider来使用
        Uri uri = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = AUFileProvider.getUriForFile(ctx, getApplicationId() + ".provider.install.apk", apk);
            ctx.grantUriPermission(getApplicationId(), uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(uri, ctx.getContentResolver().getType(uri));
        } else {
            uri = Uri.fromFile(apk);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
        }

        //打开安装器
        try {
            ctx.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查是否可安装应用
     *
     * @return 是否可安装
     */
    public static boolean canInstall() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.O || ctx.getPackageManager().canRequestPackageInstalls();
    }

    /**
     * 拨打电话
     *
     * @param phone 电话号码
     */
    public static void callPhone(final String phone) {
        PermissionHelper.getInstance().permissions(Manifest.permission.CALL_PHONE).request(new PermissionHelper.SimplePermissionCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onGranted() {
                Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
                ctx.startActivity(callIntent);
            }

            @Override
            public void onDenied() {
                ToastUtils.shortT("请到应用设置中开启“拨打电话”权限");
            }
        });
    }

    /**
     * 判断是否注册过广播
     *
     * @param action 广播Action
     * @return 是否注册过
     */
    public static boolean isReceiverRegisted(String action) {
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
     * @param clazz Activity字节码对象
     * @return 是否在前台
     */
    public static boolean isForeground(Class<?> clazz) {
        if (null == clazz) {
            return false;
        }

        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        if (null == am) {
            return false;
        }
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (clazz.getName().equals(cpn.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取ApplicationId
     *
     * @return ApplicationId
     */
    public static String getApplicationId() {
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = ctx.getPackageManager().getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);

            String appId = null;
            if (applicationInfo != null) {
                appId = applicationInfo.packageName;
                if (TextUtils.isEmpty(appId)) {
                    appId = applicationInfo.processName;
                }
            }
            if (TextUtils.isEmpty(appId)) {
                appId = ctx.getPackageName();
            }
            return appId;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 返回当前的应用是否处于前台显示状态
     *
     * @param packageName 包名
     * @return 是否处于前台运行
     */
    public static boolean isTopProcess(String packageName) {
        ActivityManager am = (ActivityManager) ctx.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> list = am.getRunningAppProcesses();
        if (list.size() == 0) return false;
        for (ActivityManager.RunningAppProcessInfo process : list) {
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

    /**
     * 获取本机电话号码
     *
     * @param callback 回调
     */
    public static void getPhoneNo(final Callback<String> callback) {
        PermissionHelper.getInstance().permissions(Manifest.permission.READ_PHONE_STATE).request(new PermissionHelper.SimplePermissionCallback() {
            @SuppressLint({"MissingPermission", "HardwareIds"})
            @Override
            public void onGranted() {
                TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
                callback.onResult(null != tm ? tm.getLine1Number() : "");
            }

            @Override
            public void onDenied() {
                callback.onResult("");
            }
        });
    }

    /**
     * 回调
     *
     * @param <T> 传回的数据类型
     */
    public interface Callback<T> {

        /**
         * 回调
         *
         * @param result 传回的数据
         */
        void onResult(T result);
    }

    private static class InstallSettingsActivity extends AppCompatActivity {
        private File mApk;

        /**
         * 开启授权窗口
         *
         * @param ctx 上下文
         * @param apk 安装包
         */
        static void start(Context ctx, File apk) {
            Intent intent = new Intent(ctx, InstallSettingsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("apk", apk);
            ctx.startActivity(intent);
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
            super.onCreate(savedInstanceState);
            mApk = (File) getIntent().getSerializableExtra("apk");
            openInstallSettings();
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        private void openInstallSettings() {
            Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
            startActivityForResult(intent, 1000);
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {
            finish();
            return true;
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            switch (requestCode) {
                case 1000:
                    installApk(mApk);
                    break;
            }
            finish();
        }
    }
}
