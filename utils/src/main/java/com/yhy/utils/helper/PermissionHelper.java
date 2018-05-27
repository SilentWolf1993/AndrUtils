package com.yhy.utils.helper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StringDef;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * author  : 颜洪毅
 * e-mail  : yhyzgn@gmail.com
 * time    : 2018-05-26 22:31
 * version : 1.0.0
 * desc    : 运行时权限工具类
 */
public class PermissionHelper {
    private static final List<String> PERMISSIONS = new ArrayList<>();

    private volatile static PermissionHelper instance;

    private Application mApp;
    private Set<String> mPermissions;
    private List<String> mPermissionsRequest;
    private List<String> mPermissionsGranted;
    private List<String> mPermissionsDenied;
    private List<String> mPermissionsDeniedForever;
    private SimplePermissionCallback mSimpleCallback;
    private PermissionCallback mCallback;

    /**
     * 获取单例实例
     *
     * @return 单例实例
     */
    public static PermissionHelper getInstance() {
        if (null == instance) {
            synchronized (PermissionHelper.class) {
                if (null == instance) {
                    instance = new PermissionHelper();
                }
            }
        }
        return instance;
    }

    private PermissionHelper() {
        if (null != instance) {
            throw new UnsupportedOperationException("Can not instantiate singleton class.");
        }
    }

    /**
     * 初始化，放在Application中
     *
     * @param app 当前Application
     */
    public void init(Application app) {
        mApp = app;
        PERMISSIONS.clear();
        PERMISSIONS.addAll(getPermissions());
    }

    /**
     * 获取当前应用的所有权限
     *
     * @return 当前应用的所有权限
     */
    public List<String> getPermissions() {
        PackageManager pm = mApp.getPackageManager();
        try {
            return Arrays.asList(pm.getPackageInfo(mApp.getPackageName(), PackageManager.GET_PERMISSIONS).requestedPermissions);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * 判断某项权限是否已经授权
     *
     * @param permission 具体的某项权限
     * @return 是否已经授权
     */
    public boolean isGranted(String permission) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(mApp, permission);
    }

    /**
     * 判断某些权限是否已经授权
     *
     * @param permissions 某些权限
     * @return 是否已经授权
     */
    public boolean isGranted(String... permissions) {
        if (null != permissions) {
            for (String permission : permissions) {
                if (!isGranted(permission)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 准备请求授权的权限
     *
     * @param permissions 准备请求授权的权限
     * @return 当前对象
     */
    public PermissionHelper permissions(String... permissions) {
        mPermissions = new LinkedHashSet<>();
        if (null != permissions) {
            for (String permission : permissions) {
                for (String runtime : RuntimePermissions.getPermissions(permission)) {
                    if (PERMISSIONS.contains(runtime)) {
                        mPermissions.add(runtime);
                    }
                }
            }
        }
        return this;
    }

    /**
     * 请求授权
     *
     * @param callback 简约回调
     */
    public void request(SimplePermissionCallback callback) {
        request(callback, null);
    }

    /**
     * 请求授权
     *
     * @param callback 完整回调
     */
    public void request(PermissionCallback callback) {
        request(null, callback);
    }

    /**
     * 请求授权
     *
     * @param simpleCallback 简约回调
     * @param callback       完整回调
     */
    private void request(SimplePermissionCallback simpleCallback, PermissionCallback callback) {
        mSimpleCallback = simpleCallback;
        mCallback = callback;

        mPermissionsRequest = new ArrayList<>();
        mPermissionsGranted = new ArrayList<>();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mPermissionsGranted.addAll(mPermissions);
            callback();
        } else {
            for (String permission : mPermissions) {
                if (isGranted(permission)) {
                    mPermissionsGranted.add(permission);
                } else {
                    mPermissionsRequest.add(permission);
                }
            }
            if (mPermissionsRequest.isEmpty()) {
                callback();
            } else {
                toGrant();
            }
        }
    }

    /**
     * 去授权页面
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void toGrant() {
        mPermissionsDenied = new ArrayList<>();
        mPermissionsDeniedForever = new ArrayList<>();
        PermissionActivity.start(mApp);
    }

    /**
     * 统一回调处理
     */
    private void callback() {
        if (null != mPermissionsGranted && !mPermissionsGranted.isEmpty()) {
            if (null != mSimpleCallback) {
                mSimpleCallback.onGranted();
            }
            if (null != mCallback) {
                mCallback.onGranted(mPermissionsGranted);
            }
        }
        if (null != mPermissionsDenied && !mPermissionsDenied.isEmpty() || null != mPermissionsDeniedForever && !mPermissionsDeniedForever.isEmpty()) {
            if (null != mSimpleCallback) {
                mSimpleCallback.onDenied();
            }
            if (null != mCallback) {
                mCallback.onDenied(mPermissionsDenied, mPermissionsDeniedForever);
            }
        }
    }

    /**
     * 检查权限，分类
     *
     * @param activity 用来授权请求的Activity
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkStatus(Activity activity) {
        if (null == mPermissionsRequest) {
            return;
        }
        for (String permission : mPermissionsRequest) {
            // 无论是否包含，先移除（1、避免重复添加；2、某项权限从拒绝变为已授权时，需要从拒绝列表中移除）
            mPermissionsGranted.remove(permission);
            mPermissionsDenied.remove(permission);
            mPermissionsDeniedForever.remove(permission);

            if (isGranted(permission)) {
                mPermissionsGranted.add(permission);
            } else {
                mPermissionsDenied.add(permission);
                if (activity.shouldShowRequestPermissionRationale(permission)) {
                    mPermissionsDeniedForever.add(permission);
                }
            }
        }
    }

    /**
     * 授权回调处理
     *
     * @param activity 处理授权的Activity
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void onRequestPermissionsResult(final Activity activity) {
        checkStatus(activity);
        callback();
    }

    /**
     * 简约版回调
     */
    public interface SimplePermissionCallback {
        /**
         * 已经授权
         */
        void onGranted();

        /**
         * 已经拒绝
         */
        void onDenied();
    }

    /**
     * 完整版回调
     */
    public interface PermissionCallback {
        /**
         * 已经授权
         *
         * @param granted 具体被授权的权限
         */
        void onGranted(List<String> granted);

        /**
         * 已经被拒绝
         *
         * @param denied  被拒绝的权限
         * @param forever 被永久拒绝的权限
         */
        void onDenied(List<String> denied, List<String> forever);
    }

    /**
     * 用来处理授权请求的Activity
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static class PermissionActivity extends AppCompatActivity {

        /**
         * 开启授权窗口
         *
         * @param ctx 上下文
         */
        static void start(Context ctx) {
            Intent intent = new Intent(ctx, PermissionActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(intent);
        }

        /**
         * Activity被创建
         *
         * @param savedInstanceState 状态数据
         */
        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);

            super.onCreate(savedInstanceState);
            if (null == instance) {
                Log.e("PermissionHelper", "Request permission error.");
                finish();
                return;
            }

            if (null != instance.mPermissionsRequest) {
                int size = instance.mPermissionsRequest.size();
                if (size == 0) {
                    finish();
                    return;
                }
                // 请求权限
                requestPermissions(instance.mPermissionsRequest.toArray(new String[size]), 1000);
            }
        }

        /**
         * 授权回调
         *
         * @param requestCode  请求码
         * @param permissions  请求的权限
         * @param grantResults 被授权的权限在数组中的索引
         */
        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            if (requestCode == 1000) {
                instance.onRequestPermissionsResult(this);
            }
            finish();
        }

        /**
         * 处理触摸事件
         *
         * @param ev 当前事件
         * @return 返回true，表示消费了事件
         */
        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {
            finish();
            return true;
        }
    }

    /**
     * 权限常量
     */
    @SuppressLint("InlinedApi")
    private static class RuntimePermissions {
        static final String CALENDAR = Manifest.permission_group.CALENDAR;
        static final String CAMERA = Manifest.permission_group.CAMERA;
        static final String CONTACTS = Manifest.permission_group.CONTACTS;
        static final String LOCATION = Manifest.permission_group.LOCATION;
        static final String MICROPHONE = Manifest.permission_group.MICROPHONE;
        static final String PHONE = Manifest.permission_group.PHONE;
        static final String SENSORS = Manifest.permission_group.SENSORS;
        static final String SMS = Manifest.permission_group.SMS;
        static final String STORAGE = Manifest.permission_group.STORAGE;

        private static final String[] GROUP_CALENDAR = {
                Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR
        };
        private static final String[] GROUP_CAMERA = {
                Manifest.permission.CAMERA
        };
        private static final String[] GROUP_CONTACTS = {
                Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS, Manifest.permission.GET_ACCOUNTS
        };
        private static final String[] GROUP_LOCATION = {
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
        };
        private static final String[] GROUP_MICROPHONE = {
                Manifest.permission.RECORD_AUDIO
        };
        private static final String[] GROUP_PHONE = {
                Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_PHONE_NUMBERS, Manifest.permission.CALL_PHONE,
                Manifest.permission.ANSWER_PHONE_CALLS, Manifest.permission.READ_CALL_LOG, Manifest.permission.WRITE_CALL_LOG,
                Manifest.permission.ADD_VOICEMAIL, Manifest.permission.USE_SIP, Manifest.permission.PROCESS_OUTGOING_CALLS
        };
        private static final String[] GROUP_SENSORS = {
                Manifest.permission.BODY_SENSORS
        };
        private static final String[] GROUP_SMS = {
                Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS,
                Manifest.permission.RECEIVE_WAP_PUSH, Manifest.permission.RECEIVE_MMS,
        };
        private static final String[] GROUP_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        @StringDef({CALENDAR, CAMERA, CONTACTS, LOCATION, MICROPHONE, PHONE, SENSORS, SMS, STORAGE,})
        @Retention(RetentionPolicy.SOURCE)
        @interface Permission {
        }

        /**
         * 根据权限分组名称获取权限分组
         *
         * @param permission 名称
         * @return 权限
         */
        static String[] getPermissions(@Permission final String permission) {
            switch (permission) {
                case CALENDAR:
                    return GROUP_CALENDAR;
                case CAMERA:
                    return GROUP_CAMERA;
                case CONTACTS:
                    return GROUP_CONTACTS;
                case LOCATION:
                    return GROUP_LOCATION;
                case MICROPHONE:
                    return GROUP_MICROPHONE;
                case PHONE:
                    return GROUP_PHONE;
                case SENSORS:
                    return GROUP_SENSORS;
                case SMS:
                    return GROUP_SMS;
                case STORAGE:
                    return GROUP_STORAGE;
            }
            return new String[]{permission};
        }
    }
}
