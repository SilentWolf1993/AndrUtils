package com.yhy.utils.core;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.AnimRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.view.View;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * author : 颜洪毅
 * e-mail : yhyzgn@gmail.com
 * time   : 2017-12-11 11:17
 * version: 1.0.0
 * desc   : Activity工具类
 */
public class ActivityUtils {
    // 当前Application
    @SuppressLint("StaticFieldLeak")
    private static Application app;
    // 栈顶Activity
    private static WeakReference<Activity> topActivityWeakRef;
    // Activity栈
    private static List<Activity> activityList;

    private ActivityUtils() {
        throw new UnsupportedOperationException("Can not instantiate utils class.");
    }

    /**
     * 初始化，在Application入口调用
     *
     * @param app 上下文对象
     */
    public static void init(Application app) {
        ActivityUtils.app = app;
        activityList = new ArrayList<>();

        // 当前Application注册声明周期回调
        ActivityUtils.app.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                activityList.add(activity);
                setTopActivityWeakRef(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {
                setTopActivityWeakRef(activity);
            }

            @Override
            public void onActivityResumed(Activity activity) {
                setTopActivityWeakRef(activity);
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                activityList.remove(activity);
            }
        });
    }

    /**
     * 设置栈顶Activity
     *
     * @param activity 当前Activity
     */
    private static void setTopActivityWeakRef(Activity activity) {
        if (topActivityWeakRef == null || !activity.equals(topActivityWeakRef.get())) {
            topActivityWeakRef = new WeakReference<>(activity);
        }
    }

    /**
     * 判断 Activity 是否存在
     *
     * @param packageName 包名
     * @param className   activity 全路径类名
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isActivityExists(@NonNull final String packageName,
                                           @NonNull final String className) {
        Intent intent = new Intent();
        intent.setClassName(packageName, className);
        return !(app.getPackageManager().resolveActivity(intent, 0) == null ||
                intent.resolveActivity(app.getPackageManager()) == null ||
                app.getPackageManager().queryIntentActivities(intent, 0).size() == 0);
    }

    /**
     * 启动 Activity
     *
     * @param clz Activity 类
     */
    public static void startActivity(@NonNull final Class<?> clz) {
        Context context = getActivityOrApp();
        startActivity(context, null, context.getPackageName(), clz.getName(), null);
    }

    /**
     * 启动 Activity
     *
     * @param clz     Activity 类
     * @param options 跳转动画
     */
    public static void startActivity(@NonNull final Class<?> clz,
                                     @NonNull final Bundle options) {
        Context context = getActivityOrApp();
        startActivity(context, null, context.getPackageName(), clz.getName(), options);
    }

    /**
     * 启动 Activity
     *
     * @param clz       Activity 类
     * @param enterAnim 入场动画
     * @param exitAnim  出场动画
     */
    public static void startActivity(@NonNull final Class<?> clz,
                                     @AnimRes final int enterAnim,
                                     @AnimRes final int exitAnim) {
        Context context = getActivityOrApp();
        startActivity(context, null, context.getPackageName(), clz.getName(),
                getOptionsBundle(context, enterAnim, exitAnim));
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN && context instanceof Activity) {
            ((Activity) context).overridePendingTransition(enterAnim, exitAnim);
        }
    }

    /**
     * 启动 Activity
     *
     * @param activity activity
     * @param clz      Activity 类
     */
    public static void startActivity(@NonNull final Activity activity,
                                     @NonNull final Class<?> clz) {
        startActivity(activity, null, activity.getPackageName(), clz.getName(), null);
    }

    /**
     * 启动 Activity
     *
     * @param activity activity
     * @param clz      Activity 类
     * @param options  跳转动画
     */
    public static void startActivity(@NonNull final Activity activity,
                                     @NonNull final Class<?> clz,
                                     @NonNull final Bundle options) {
        startActivity(activity, null, activity.getPackageName(), clz.getName(), options);
    }

    /**
     * 启动 Activity
     *
     * @param activity       activity
     * @param clz            Activity 类
     * @param sharedElements 共享元素
     */
    public static void startActivity(@NonNull final Activity activity,
                                     @NonNull final Class<?> clz,
                                     @NonNull final View... sharedElements) {
        startActivity(activity, null, activity.getPackageName(), clz.getName(),
                getOptionsBundle(activity, sharedElements));
    }

    /**
     * 启动 Activity
     *
     * @param activity  activity
     * @param clz       Activity 类
     * @param enterAnim 入场动画
     * @param exitAnim  出场动画
     */
    public static void startActivity(@NonNull final Activity activity,
                                     @NonNull final Class<?> clz,
                                     @AnimRes final int enterAnim,
                                     @AnimRes final int exitAnim) {

        startActivity(activity, null, activity.getPackageName(), clz.getName(),
                getOptionsBundle(activity, enterAnim, exitAnim));
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            activity.overridePendingTransition(enterAnim, exitAnim);
        }
    }

    /**
     * 启动 Activity
     *
     * @param extras extras
     * @param clz    Activity 类
     */
    public static void startActivity(@NonNull final Bundle extras,
                                     @NonNull final Class<?> clz) {
        Context context = getActivityOrApp();
        startActivity(context, extras, context.getPackageName(), clz.getName(), null);
    }

    /**
     * 启动 Activity
     *
     * @param extras  extras
     * @param clz     Activity 类
     * @param options 跳转动画
     */
    public static void startActivity(@NonNull final Bundle extras,
                                     @NonNull final Class<?> clz,
                                     @NonNull final Bundle options) {
        Context context = getActivityOrApp();
        startActivity(context, extras, context.getPackageName(), clz.getName(), options);
    }

    /**
     * 启动 Activity
     *
     * @param extras    extras
     * @param clz       Activity 类
     * @param enterAnim 入场动画
     * @param exitAnim  出场动画
     */
    public static void startActivity(@NonNull final Bundle extras,
                                     @NonNull final Class<?> clz,
                                     @AnimRes final int enterAnim,
                                     @AnimRes final int exitAnim) {
        Context context = getActivityOrApp();
        startActivity(context, extras, context.getPackageName(), clz.getName(),
                getOptionsBundle(context, enterAnim, exitAnim));
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN && context instanceof Activity) {
            ((Activity) context).overridePendingTransition(enterAnim, exitAnim);
        }
    }

    /**
     * 启动 Activity
     *
     * @param extras   extras
     * @param activity activity
     * @param clz      Activity 类
     */
    public static void startActivity(@NonNull final Bundle extras,
                                     @NonNull final Activity activity,
                                     @NonNull final Class<?> clz) {
        startActivity(activity, extras, activity.getPackageName(), clz.getName(), null);
    }

    /**
     * 启动 Activity
     *
     * @param extras   extras
     * @param activity activity
     * @param clz      Activity 类
     * @param options  跳转动画
     */
    public static void startActivity(@NonNull final Bundle extras,
                                     @NonNull final Activity activity,
                                     @NonNull final Class<?> clz,
                                     @NonNull final Bundle options) {
        startActivity(activity, extras, activity.getPackageName(), clz.getName(), options);
    }

    /**
     * 启动 Activity
     *
     * @param extras         extras
     * @param activity       activity
     * @param clz            Activity 类
     * @param sharedElements 共享元素
     */
    public static void startActivity(@NonNull final Bundle extras,
                                     @NonNull final Activity activity,
                                     @NonNull final Class<?> clz,
                                     @NonNull final View... sharedElements) {
        startActivity(activity, extras, activity.getPackageName(), clz.getName(),
                getOptionsBundle(activity, sharedElements));
    }

    /**
     * 启动 Activity
     *
     * @param extras    extras
     * @param activity  activity
     * @param clz       Activity 类
     * @param enterAnim 入场动画
     * @param exitAnim  出场动画
     */
    public static void startActivity(@NonNull final Bundle extras,
                                     @NonNull final Activity activity,
                                     @NonNull final Class<?> clz,
                                     @AnimRes final int enterAnim,
                                     @AnimRes final int exitAnim) {
        startActivity(activity, extras, activity.getPackageName(), clz.getName(),
                getOptionsBundle(activity, enterAnim, exitAnim));
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            activity.overridePendingTransition(enterAnim, exitAnim);
        }
    }

    /**
     * 启动 Activity
     *
     * @param pkg 包名
     * @param cls 全类名
     */
    public static void startActivity(@NonNull final String pkg,
                                     @NonNull final String cls) {
        startActivity(getActivityOrApp(), null, pkg, cls, null);
    }

    /**
     * 启动 Activity
     *
     * @param pkg     包名
     * @param cls     全类名
     * @param options 动画
     */
    public static void startActivity(@NonNull final String pkg,
                                     @NonNull final String cls,
                                     @NonNull final Bundle options) {
        startActivity(getActivityOrApp(), null, pkg, cls, options);
    }

    /**
     * 启动 Activity
     *
     * @param pkg       包名
     * @param cls       全类名
     * @param enterAnim 入场动画
     * @param exitAnim  出场动画
     */
    public static void startActivity(@NonNull final String pkg,
                                     @NonNull final String cls,
                                     @AnimRes final int enterAnim,
                                     @AnimRes final int exitAnim) {
        Context context = getActivityOrApp();
        startActivity(context, null, pkg, cls, getOptionsBundle(context, enterAnim, exitAnim));
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN && context instanceof Activity) {
            ((Activity) context).overridePendingTransition(enterAnim, exitAnim);
        }
    }

    /**
     * 启动 Activity
     *
     * @param activity activity
     * @param pkg      包名
     * @param cls      全类名
     */
    public static void startActivity(@NonNull final Activity activity,
                                     @NonNull final String pkg,
                                     @NonNull final String cls) {
        startActivity(activity, null, pkg, cls, null);
    }

    /**
     * 启动 Activity
     *
     * @param activity activity
     * @param pkg      包名
     * @param cls      全类名
     * @param options  动画
     */
    public static void startActivity(@NonNull final Activity activity,
                                     @NonNull final String pkg,
                                     @NonNull final String cls,
                                     @NonNull final Bundle options) {
        startActivity(activity, null, pkg, cls, options);
    }

    /**
     * 启动 Activity
     *
     * @param activity       activity
     * @param pkg            包名
     * @param cls            全类名
     * @param sharedElements 共享元素
     */
    public static void startActivity(@NonNull final Activity activity,
                                     @NonNull final String pkg,
                                     @NonNull final String cls,
                                     @NonNull final View... sharedElements) {
        startActivity(activity, null, pkg, cls, getOptionsBundle(activity, sharedElements));
    }

    /**
     * 启动 Activity
     *
     * @param activity  activity
     * @param pkg       包名
     * @param cls       全类名
     * @param enterAnim 入场动画
     * @param exitAnim  出场动画
     */
    public static void startActivity(@NonNull final Activity activity,
                                     @NonNull final String pkg,
                                     @NonNull final String cls,
                                     @AnimRes final int enterAnim,
                                     @AnimRes final int exitAnim) {
        startActivity(activity, null, pkg, cls, getOptionsBundle(activity, enterAnim, exitAnim));
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            activity.overridePendingTransition(enterAnim, exitAnim);
        }
    }

    /**
     * 启动 Activity
     *
     * @param extras extras
     * @param pkg    包名
     * @param cls    全类名
     */
    public static void startActivity(@NonNull final Bundle extras,
                                     @NonNull final String pkg,
                                     @NonNull final String cls) {
        startActivity(getActivityOrApp(), extras, pkg, cls, null);
    }

    /**
     * 启动 Activity
     *
     * @param extras  extras
     * @param pkg     包名
     * @param cls     全类名
     * @param options 动画
     */
    public static void startActivity(@NonNull final Bundle extras,
                                     @NonNull final String pkg,
                                     @NonNull final String cls,
                                     @NonNull final Bundle options) {
        startActivity(getActivityOrApp(), extras, pkg, cls, options);
    }

    /**
     * 启动 Activity
     *
     * @param extras    extras
     * @param pkg       包名
     * @param cls       全类名
     * @param enterAnim 入场动画
     * @param exitAnim  出场动画
     */
    public static void startActivity(@NonNull final Bundle extras,
                                     @NonNull final String pkg,
                                     @NonNull final String cls,
                                     @AnimRes final int enterAnim,
                                     @AnimRes final int exitAnim) {
        Context context = getActivityOrApp();
        startActivity(context, extras, pkg, cls, getOptionsBundle(context, enterAnim, exitAnim));
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN && context instanceof Activity) {
            ((Activity) context).overridePendingTransition(enterAnim, exitAnim);
        }
    }

    /**
     * 启动 Activity
     *
     * @param activity activity
     * @param extras   extras
     * @param pkg      包名
     * @param cls      全类名
     */
    public static void startActivity(@NonNull final Bundle extras,
                                     @NonNull final Activity activity,
                                     @NonNull final String pkg,
                                     @NonNull final String cls) {
        startActivity(activity, extras, pkg, cls, null);
    }

    /**
     * 启动 Activity
     *
     * @param extras   extras
     * @param activity activity
     * @param pkg      包名
     * @param cls      全类名
     * @param options  动画
     */
    public static void startActivity(@NonNull final Bundle extras,
                                     @NonNull final Activity activity,
                                     @NonNull final String pkg,
                                     @NonNull final String cls,
                                     @NonNull final Bundle options) {
        startActivity(activity, extras, pkg, cls, options);
    }

    /**
     * 启动 Activity
     *
     * @param extras         extras
     * @param activity       activity
     * @param pkg            包名
     * @param cls            全类名
     * @param sharedElements 共享元素
     */
    public static void startActivity(@NonNull final Bundle extras,
                                     @NonNull final Activity activity,
                                     @NonNull final String pkg,
                                     @NonNull final String cls,
                                     @NonNull final View... sharedElements) {
        startActivity(activity, extras, pkg, cls, getOptionsBundle(activity, sharedElements));
    }

    /**
     * 启动 Activity
     *
     * @param extras    extras
     * @param pkg       包名
     * @param cls       全类名
     * @param enterAnim 入场动画
     * @param exitAnim  出场动画
     */
    public static void startActivity(@NonNull final Bundle extras,
                                     @NonNull final Activity activity,
                                     @NonNull final String pkg,
                                     @NonNull final String cls,
                                     @AnimRes final int enterAnim,
                                     @AnimRes final int exitAnim) {
        startActivity(activity, extras, pkg, cls, getOptionsBundle(activity, enterAnim, exitAnim));
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            activity.overridePendingTransition(enterAnim, exitAnim);
        }
    }

    /**
     * 启动 Activity
     *
     * @param intent 意图
     */
    public static void startActivity(@NonNull final Intent intent) {
        startActivity(intent, getActivityOrApp(), null);
    }

    /**
     * 启动 Activity
     *
     * @param intent  意图
     * @param options 跳转动画
     */
    public static void startActivity(@NonNull final Intent intent,
                                     @NonNull final Bundle options) {
        startActivity(intent, getActivityOrApp(), options);
    }

    /**
     * 启动 Activity
     *
     * @param intent    意图
     * @param enterAnim 入场动画
     * @param exitAnim  出场动画
     */
    public static void startActivity(@NonNull final Intent intent,
                                     @AnimRes final int enterAnim,
                                     @AnimRes final int exitAnim) {
        Context context = getActivityOrApp();
        startActivity(intent, context, getOptionsBundle(context, enterAnim, exitAnim));
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN && context instanceof Activity) {
            ((Activity) context).overridePendingTransition(enterAnim, exitAnim);
        }
    }

    /**
     * 启动 Activity
     *
     * @param activity activity
     * @param intent   意图
     */
    public static void startActivity(@NonNull final Activity activity,
                                     @NonNull final Intent intent) {
        startActivity(intent, activity, null);
    }

    /**
     * 启动 Activity
     *
     * @param activity activity
     * @param intent   意图
     * @param options  跳转动画
     */
    public static void startActivity(@NonNull final Activity activity,
                                     @NonNull final Intent intent,
                                     @NonNull final Bundle options) {
        startActivity(intent, activity, options);
    }

    /**
     * 启动 Activity
     *
     * @param activity       activity
     * @param intent         意图
     * @param sharedElements 共享元素
     */
    public static void startActivity(@NonNull final Activity activity,
                                     @NonNull final Intent intent,
                                     @NonNull final View... sharedElements) {
        startActivity(intent, activity, getOptionsBundle(activity, sharedElements));
    }

    /**
     * 启动 Activity
     *
     * @param activity  activity
     * @param intent    意图
     * @param enterAnim 入场动画
     * @param exitAnim  出场动画
     */
    public static void startActivity(@NonNull final Activity activity,
                                     @NonNull final Intent intent,
                                     @AnimRes final int enterAnim,
                                     @AnimRes final int exitAnim) {
        startActivity(intent, activity, getOptionsBundle(activity, enterAnim, exitAnim));
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            activity.overridePendingTransition(enterAnim, exitAnim);
        }
    }

    /**
     * 启动多个 Activity
     *
     * @param intents 意图
     */
    public static void startActivities(@NonNull final Intent[] intents) {
        startActivities(intents, getActivityOrApp(), null);
    }

    /**
     * 启动多个 Activity
     *
     * @param intents 意图
     * @param options 跳转动画
     */
    public static void startActivities(@NonNull final Intent[] intents,
                                       @NonNull final Bundle options) {
        startActivities(intents, getActivityOrApp(), options);
    }

    /**
     * 启动多个 Activity
     *
     * @param intents   意图
     * @param enterAnim 入场动画
     * @param exitAnim  出场动画
     */
    public static void startActivities(@NonNull final Intent[] intents,
                                       @AnimRes final int enterAnim,
                                       @AnimRes final int exitAnim) {
        Context context = getActivityOrApp();
        startActivities(intents, context, getOptionsBundle(context, enterAnim, exitAnim));
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN && context instanceof Activity) {
            ((Activity) context).overridePendingTransition(enterAnim, exitAnim);
        }
    }

    /**
     * 启动多个 Activity
     *
     * @param activity activity
     * @param intents  意图
     */
    public static void startActivities(@NonNull final Activity activity,
                                       @NonNull final Intent[] intents) {
        startActivities(intents, activity, null);
    }

    /**
     * 启动多个 Activity
     *
     * @param activity activity
     * @param intents  意图
     * @param options  跳转动画
     */
    public static void startActivities(@NonNull final Activity activity,
                                       @NonNull final Intent[] intents,
                                       @NonNull final Bundle options) {
        startActivities(intents, activity, options);
    }

    /**
     * 启动多个 Activity
     *
     * @param activity  activity
     * @param intents   意图
     * @param enterAnim 入场动画
     * @param exitAnim  出场动画
     */
    public static void startActivities(@NonNull final Activity activity,
                                       @NonNull final Intent[] intents,
                                       @AnimRes final int enterAnim,
                                       @AnimRes final int exitAnim) {
        startActivities(intents, activity, getOptionsBundle(activity, enterAnim, exitAnim));
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            activity.overridePendingTransition(enterAnim, exitAnim);
        }
    }

    /**
     * 回到桌面
     */
    public static void startHomeActivity() {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        startActivity(homeIntent);
    }

    /**
     * 获取 Activity 栈链表
     *
     * @return Activity 栈链表
     */
    public static List<Activity> getActivityList() {
        return activityList;
    }

    /**
     * 获取启动项 Activity
     *
     * @return 启动项 Activity
     */
    public static String getLauncherActivity() {
        return getLauncherActivity(app.getPackageName());
    }

    /**
     * 获取启动项 Activity
     *
     * @param packageName 包名
     * @return 启动项 Activity
     */
    public static String getLauncherActivity(@NonNull final String packageName) {
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PackageManager pm = app.getPackageManager();
        List<ResolveInfo> info = pm.queryIntentActivities(intent, 0);
        for (ResolveInfo aInfo : info) {
            if (aInfo.activityInfo.packageName.equals(packageName)) {
                return aInfo.activityInfo.name;
            }
        }
        return "no " + packageName;
    }

    /**
     * 获取栈顶 Activity
     *
     * @return 栈顶 Activity
     */
    public static Activity getTopActivity() {
        if (topActivityWeakRef != null) {
            Activity activity = topActivityWeakRef.get();
            if (activity != null) {
                return activity;
            }
        }
        List<Activity> activities = activityList;
        int size = activities.size();
        return size > 0 ? activities.get(size - 1) : null;
    }

    /**
     * 判断 Activity 是否存在栈中
     *
     * @param activity activity
     * @return {@code true}: 存在<br>{@code false}: 不存在
     */
    public static boolean isActivityExistsInStack(@NonNull final Activity activity) {
        List<Activity> activities = activityList;
        for (Activity aActivity : activities) {
            if (aActivity.equals(activity)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断 Activity 是否存在栈中
     *
     * @param clz Activity 类
     * @return {@code true}: 存在<br>{@code false}: 不存在
     */
    public static boolean isActivityExistsInStack(@NonNull final Class<?> clz) {
        List<Activity> activities = activityList;
        for (Activity aActivity : activities) {
            if (aActivity.getClass().equals(clz)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 结束 Activity
     *
     * @param activity activity
     */
    public static void finishActivity(@NonNull final Activity activity) {
        finishActivity(activity, false);
    }

    /**
     * 结束 Activity
     *
     * @param activity   activity
     * @param isLoadAnim 是否启动动画
     */
    public static void finishActivity(@NonNull final Activity activity, final boolean isLoadAnim) {
        activity.finish();
        if (!isLoadAnim) {
            activity.overridePendingTransition(0, 0);
        }
    }

    /**
     * 结束 Activity
     *
     * @param activity  activity
     * @param enterAnim 入场动画
     * @param exitAnim  出场动画
     */
    public static void finishActivity(@NonNull final Activity activity,
                                      @AnimRes final int enterAnim,
                                      @AnimRes final int exitAnim) {
        activity.finish();
        activity.overridePendingTransition(enterAnim, exitAnim);
    }

    /**
     * 结束 Activity
     *
     * @param clz Activity 类
     */
    public static void finishActivity(@NonNull final Class<?> clz) {
        finishActivity(clz, false);
    }

    /**
     * 结束 Activity
     *
     * @param clz        Activity 类
     * @param isLoadAnim 是否启动动画
     */
    public static void finishActivity(@NonNull final Class<?> clz, final boolean isLoadAnim) {
        List<Activity> activities = activityList;
        for (Activity activity : activities) {
            if (activity.getClass().equals(clz)) {
                activity.finish();
                if (!isLoadAnim) {
                    activity.overridePendingTransition(0, 0);
                }
            }
        }
    }

    /**
     * 结束 Activity
     *
     * @param clz       Activity 类
     * @param enterAnim 入场动画
     * @param exitAnim  出场动画
     */
    public static void finishActivity(@NonNull final Class<?> clz,
                                      @AnimRes final int enterAnim,
                                      @AnimRes final int exitAnim) {
        List<Activity> activities = activityList;
        for (Activity activity : activities) {
            if (activity.getClass().equals(clz)) {
                activity.finish();
                activity.overridePendingTransition(enterAnim, exitAnim);
            }
        }
    }

    /**
     * 结束到指定 Activity
     *
     * @param activity      activity
     * @param isIncludeSelf 是否结束该 activity 自己
     */
    public static boolean finishToActivity(@NonNull final Activity activity,
                                           final boolean isIncludeSelf) {
        return finishToActivity(activity, isIncludeSelf, false);
    }

    /**
     * 结束到指定 Activity
     *
     * @param activity      activity
     * @param isIncludeSelf 是否结束该 activity 自己
     * @param isLoadAnim    是否启动动画
     */
    public static boolean finishToActivity(@NonNull final Activity activity,
                                           final boolean isIncludeSelf,
                                           final boolean isLoadAnim) {
        List<Activity> activities = activityList;
        for (int i = activities.size() - 1; i >= 0; --i) {
            Activity aActivity = activities.get(i);
            if (aActivity.equals(activity)) {
                if (isIncludeSelf) {
                    finishActivity(aActivity, isLoadAnim);
                }
                return true;
            }
            finishActivity(aActivity, isLoadAnim);
        }
        return false;
    }

    /**
     * 结束到指定 Activity
     *
     * @param activity      activity
     * @param isIncludeSelf 是否结束该 activity 自己
     * @param enterAnim     入场动画
     * @param exitAnim      出场动画
     */
    public static boolean finishToActivity(@NonNull final Activity activity,
                                           final boolean isIncludeSelf,
                                           @AnimRes final int enterAnim,
                                           @AnimRes final int exitAnim) {
        List<Activity> activities = activityList;
        for (int i = activities.size() - 1; i >= 0; --i) {
            Activity aActivity = activities.get(i);
            if (aActivity.equals(activity)) {
                if (isIncludeSelf) {
                    finishActivity(aActivity, enterAnim, exitAnim);
                }
                return true;
            }
            finishActivity(aActivity, enterAnim, exitAnim);
        }
        return false;
    }

    /**
     * 结束到指定 Activity
     *
     * @param clz           Activity 类
     * @param isIncludeSelf 是否结束该 activity 自己
     */
    public static boolean finishToActivity(@NonNull final Class<?> clz,
                                           final boolean isIncludeSelf) {
        return finishToActivity(clz, isIncludeSelf, false);
    }

    /**
     * 结束到指定 Activity
     *
     * @param clz           Activity 类
     * @param isIncludeSelf 是否结束该 activity 自己
     * @param isLoadAnim    是否启动动画
     */
    public static boolean finishToActivity(@NonNull final Class<?> clz,
                                           final boolean isIncludeSelf,
                                           final boolean isLoadAnim) {
        List<Activity> activities = activityList;
        for (int i = activities.size() - 1; i >= 0; --i) {
            Activity aActivity = activities.get(i);
            if (aActivity.getClass().equals(clz)) {
                if (isIncludeSelf) {
                    finishActivity(aActivity, isLoadAnim);
                }
                return true;
            }
            finishActivity(aActivity, isLoadAnim);
        }
        return false;
    }

    /**
     * 结束到指定 Activity
     *
     * @param clz           Activity 类
     * @param isIncludeSelf 是否结束该 activity 自己
     * @param enterAnim     入场动画
     * @param exitAnim      出场动画
     */
    public static boolean finishToActivity(@NonNull final Class<?> clz,
                                           final boolean isIncludeSelf,
                                           @AnimRes final int enterAnim,
                                           @AnimRes final int exitAnim) {
        List<Activity> activities = activityList;
        for (int i = activities.size() - 1; i >= 0; --i) {
            Activity aActivity = activities.get(i);
            if (aActivity.getClass().equals(clz)) {
                if (isIncludeSelf) {
                    finishActivity(aActivity, enterAnim, exitAnim);
                }
                return true;
            }
            finishActivity(aActivity, enterAnim, exitAnim);
        }
        return false;
    }

    /**
     * 结束除最新之外的同类型 Activity
     * <p>也就是让栈中最多只剩下一种类型的 Activity</p>
     *
     * @param clz Activity 类
     */
    public static void finishOtherActivitiesExceptNewest(@NonNull final Class<?> clz) {
        finishOtherActivitiesExceptNewest(clz, false);
    }

    /**
     * 结束除最新之外的同类型 Activity
     * <p>也就是让栈中最多只剩下一种类型的 Activity</p>
     *
     * @param clz        Activity 类
     * @param isLoadAnim 是否启动动画
     */
    public static void finishOtherActivitiesExceptNewest(@NonNull final Class<?> clz,
                                                         final boolean isLoadAnim) {
        List<Activity> activities = activityList;
        boolean flag = false;
        for (int i = activities.size() - 1; i >= 0; i--) {
            Activity activity = activities.get(i);
            if (activity.getClass().equals(clz)) {
                if (flag) {
                    finishActivity(activity, isLoadAnim);
                } else {
                    flag = true;
                }
            }
        }
    }

    /**
     * 结束除最新之外的同类型 Activity
     * <p>也就是让栈中最多只剩下一种类型的 Activity</p>
     *
     * @param clz       Activity 类
     * @param enterAnim 入场动画
     * @param exitAnim  出场动画
     */
    public static void finishOtherActivitiesExceptNewest(@NonNull final Class<?> clz,
                                                         @AnimRes final int enterAnim,
                                                         @AnimRes final int exitAnim) {
        List<Activity> activities = activityList;
        boolean flag = false;
        for (int i = activities.size() - 1; i >= 0; i--) {
            Activity activity = activities.get(i);
            if (activity.getClass().equals(clz)) {
                if (flag) {
                    finishActivity(activity, enterAnim, exitAnim);
                } else {
                    flag = true;
                }
            }
        }
    }

    /**
     * 结束所有 Activity
     */
    public static void finishAllActivities() {
        finishAllActivities(false);
    }

    /**
     * 结束所有 Activity
     *
     * @param isLoadAnim 是否启动动画
     */
    public static void finishAllActivities(final boolean isLoadAnim) {
        for (int i = activityList.size() - 1; i >= 0; --i) {// 从栈顶开始移除
            Activity activity = activityList.get(i);
            activity.finish();// 在 onActivityDestroyed 发生 remove
            if (!isLoadAnim) {
                activity.overridePendingTransition(0, 0);
            }
        }
    }

    /**
     * 结束所有 Activity
     *
     * @param enterAnim 入场动画
     * @param exitAnim  出场动画
     */
    public static void finishAllActivities(@AnimRes final int enterAnim, @AnimRes final int exitAnim) {
        for (int i = activityList.size() - 1; i >= 0; --i) {// 从栈顶开始移除
            Activity activity = activityList.get(i);
            activity.finish();// 在 onActivityDestroyed 发生 remove
            activity.overridePendingTransition(enterAnim, exitAnim);
        }
    }

    /**
     * 获取当前Activity或者Application
     *
     * @return 当前Activity或者Application
     */
    private static Context getActivityOrApp() {
        Activity topActivity = getTopActivity();
        return topActivity == null ? app : topActivity;
    }

    /**
     * 启动Activity
     *
     * @param context 上下文
     * @param extras  额外参数
     * @param pkg     目标包名
     * @param cls     目标包名
     * @param options 启动参数
     */
    private static void startActivity(final Context context,
                                      final Bundle extras,
                                      final String pkg,
                                      final String cls,
                                      final Bundle options) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (extras != null) intent.putExtras(extras);
        intent.setComponent(new ComponentName(pkg, cls));
        startActivity(intent, context, options);
    }

    /**
     * 启动Activity
     *
     * @param intent  新Intent
     * @param context 上下文
     * @param options 启动参数
     */
    private static void startActivity(final Intent intent,
                                      final Context context,
                                      final Bundle options) {
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        if (options != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            context.startActivity(intent, options);
        } else {
            context.startActivity(intent);
        }
    }

    /**
     * 启动Activity队列
     *
     * @param intents 新的Intent队列
     * @param context 上下文
     * @param options 启动参数
     */
    private static void startActivities(final Intent[] intents,
                                        final Context context,
                                        final Bundle options) {
        if (!(context instanceof Activity)) {
            for (Intent intent : intents) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
        }
        if (options != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            context.startActivities(intents, options);
        } else {
            context.startActivities(intents);
        }
    }

    /**
     * 获取启动参数
     *
     * @param context   上下文
     * @param enterAnim 进入动画
     * @param exitAnim  退出动画
     * @return 启动参数
     */
    private static Bundle getOptionsBundle(final Context context,
                                           final int enterAnim,
                                           final int exitAnim) {
        return ActivityOptionsCompat.makeCustomAnimation(context, enterAnim, exitAnim).toBundle();
    }

    /**
     * 获取启动参数
     *
     * @param activity       当前Activity
     * @param sharedElements 共享元素列表
     * @return 启动参数
     */
    private static Bundle getOptionsBundle(final Activity activity,
                                           final View[] sharedElements) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int len = sharedElements.length;
            @SuppressWarnings("unchecked")
            Pair<View, String>[] pairs = new Pair[len];
            for (int i = 0; i < len; i++) {
                pairs[i] = Pair.create(sharedElements[i], sharedElements[i].getTransitionName());
            }
            return ActivityOptionsCompat.makeSceneTransitionAnimation(activity, pairs).toBundle();
        }
        return ActivityOptionsCompat.makeSceneTransitionAnimation(activity, null, null).toBundle();
    }
}
