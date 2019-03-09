package com.yhy.utils.core;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.FloatRange;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * author : 颜洪毅
 * e-mail : yhyzgn@gmail.com
 * time   : 2017-12-11 11:43
 * version: 1.0.0
 * desc   : 状态栏与导航栏工具类
 */
@SuppressLint("PrivateApi")
public class StatusBarUtils {
    public static int DEFAULT_COLOR = Color.TRANSPARENT;
    // Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? 0.2f : 0.3f;
    public static float DEFAULT_ALPHA = 0;
    public static final int MIN_API = Build.VERSION_CODES.KITKAT;
    // OPPO
    public static final int SYSTEM_UI_FLAG_OPPO_STATUS_BAR_TINT = 0x00000010;

    /**
     * 沉浸状态栏
     *
     * @param activity 当前Activity
     */
    public static void immersive(Activity activity) {
        immersive(activity, DEFAULT_COLOR, DEFAULT_ALPHA);
    }

    /**
     * 沉浸状态栏
     *
     * @param activity 当前Activity
     * @param color    颜色
     * @param alpha    透明度
     */
    public static void immersive(Activity activity, int color, @FloatRange(from = 0.0, to = 1.0) float alpha) {
        immersive(activity.getWindow(), color, alpha);
    }

    /**
     * 沉浸状态栏
     *
     * @param activity 当前Activity
     * @param color    颜色
     */
    public static void immersive(Activity activity, int color) {
        immersive(activity.getWindow(), color, 1f);
    }

    /**
     * 沉浸状态栏
     *
     * @param window 当前窗口
     */
    public static void immersive(Window window) {
        immersive(window, DEFAULT_COLOR, DEFAULT_ALPHA);
    }

    /**
     * 沉浸状态栏
     *
     * @param window 当前窗口
     * @param color  颜色
     */
    public static void immersive(Window window, int color) {
        immersive(window, color, 1f);
    }

    /**
     * 沉浸状态栏
     *
     * @param window 当前窗口
     * @param color  颜色
     * @param alpha  透明度
     */
    public static void immersive(Window window, int color, @FloatRange(from = 0.0, to = 1.0) float alpha) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 5.0 +
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(mixtureColor(color, alpha));

            int systemUiVisibility = window.getDecorView().getSystemUiVisibility();
            systemUiVisibility |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            systemUiVisibility |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            window.getDecorView().setSystemUiVisibility(systemUiVisibility);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 4.0 - 5.0
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setTranslucentView((ViewGroup) window.getDecorView(), color, alpha);
        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            // 4.0 - 4.4
            int systemUiVisibility = window.getDecorView().getSystemUiVisibility();
            systemUiVisibility |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            systemUiVisibility |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            window.getDecorView().setSystemUiVisibility(systemUiVisibility);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    /**
     * 使状态栏透明，并设置状态栏darkMode,字体颜色及icon变黑
     * <p>
     * 目前支持MIUI6以上,Flyme4以上,Android M以上
     *
     * @param activity 当前Activity
     * @param dark     是否改为深色
     */
    public static void darkMode(Activity activity, boolean dark) {
        darkMode(activity.getWindow(), dark, DEFAULT_COLOR, DEFAULT_ALPHA);
    }

    /**
     * 使状态栏透明，并设置状态栏darkMode,字体颜色及icon变黑
     * <p>
     * 目前支持MIUI6以上,Flyme4以上,Android M以上
     *
     * @param activity 当前Activity
     */
    public static void darkMode(Activity activity) {
        darkMode(activity, DEFAULT_COLOR, DEFAULT_ALPHA);
    }

    /**
     * 使状态栏透明，并设置状态栏darkMode,字体颜色及icon变黑
     * <p>
     * 目前支持MIUI6以上,Flyme4以上,Android M以上
     *
     * @param activity 当前Activity
     * @param color    状态栏颜色
     * @param alpha    透明度
     */
    public static void darkMode(Activity activity, int color, @FloatRange(from = 0.0, to = 1.0) float alpha) {
        darkMode(activity.getWindow(), true, color, alpha);
    }

    /**
     * 使状态栏透明，并设置状态栏darkMode,字体颜色及icon变黑
     * <p>
     * 目前支持MIUI6以上,Flyme4以上,Android M以上
     *
     * @param window 当前Activity
     * @param dark   是否将字体颜色及icon变黑
     * @param color  状态栏颜色
     * @param alpha  透明度
     */
    public static void darkMode(Window window, boolean dark, int color, @FloatRange(from = 0.0, to = 1.0) float alpha) {
        if (isFlyme4Later()) {
            // 魅族Flyme4以上
            immersive(window, color, alpha);
            darkModeForFlyme4(window, dark);
        } else if (isMIUI6Later()) {
            // 小米MIUI6.0以上
            immersive(window, color, alpha);
            darkModeForMIUI(window, dark);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            immersive(window, color, alpha);
            darkModeForM(window, dark);
        } else if (isOPPO()) {
            immersive(window, color, alpha);
            darkModeForOPPO(window, dark);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setTranslucentView((ViewGroup) window.getDecorView(), color, alpha);
        } else {
            immersive(window, color, alpha);
        }
    }

    /**
     * android 6.0设置字体颜色
     *
     * @param window 当前窗口
     * @param dark   是否深色
     */
    @RequiresApi(Build.VERSION_CODES.M)
    private static void darkModeForM(Window window, boolean dark) {
        int systemUiVisibility = window.getDecorView().getSystemUiVisibility();
        if (dark) {
            systemUiVisibility |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        } else {
            systemUiVisibility &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }
        window.getDecorView().setSystemUiVisibility(systemUiVisibility);
    }

    /**
     * 设置Flyme4+的darkMode,darkMode时候字体颜色及icon变黑
     * http://open-wiki.flyme.cn/index.php?title=Flyme%E7%B3%BB%E7%BB%9FAPI
     *
     * @param window 当前窗口
     * @param dark   是否深色
     * @return 是否支持
     */
    public static boolean darkModeForFlyme4(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            try {
                WindowManager.LayoutParams e = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(e);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }

                meizuFlags.setInt(e, value);
                window.setAttributes(e);
                result = true;
            } catch (Exception e) {
                Log.e("StatusBar", "darkIcon: failed");
            }
        }

        return result;
    }

    /**
     * 设置小米状态栏
     *
     * @param window 当前窗口
     * @param dark   是否深色
     * @return 是否支持
     */
    public static boolean darkModeForMIUI(Window window, boolean dark) {
        // Android 6.0+ 小米用得是原生状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            darkModeForM(window, dark);
            return true;
        }
        return darkModeForMIUI6(window, dark);
    }

    /**
     * 设置MIUI6+的状态栏是否为darkMode,darkMode时候字体颜色及icon变黑
     * Android 6.0以下
     * http://dev.xiaomi.com/doc/p=4769/
     *
     * @param window 当前窗口
     * @param dark   是否深色
     * @return 是否支持
     */
    public static boolean darkModeForMIUI6(Window window, boolean dark) {
        Class<? extends Window> clazz = window.getClass();
        try {
            int darkModeFlag = 0;
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(window, dark ? darkModeFlag : 0, darkModeFlag);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void darkModeForOPPO(Window window, boolean dark) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        int vis = window.getDecorView().getSystemUiVisibility();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            darkModeForM(window, dark);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (dark) {
                vis |= SYSTEM_UI_FLAG_OPPO_STATUS_BAR_TINT;
            } else {
                vis &= ~SYSTEM_UI_FLAG_OPPO_STATUS_BAR_TINT;
            }
        }
        window.getDecorView().setSystemUiVisibility(vis);
    }

    /**
     * 判断是否Flyme4以上
     *
     * @return 是否Flyme4以上
     */
    public static boolean isFlyme4Later() {
        return Build.FINGERPRINT.contains("Flyme_OS_4")
                || Build.VERSION.INCREMENTAL.contains("Flyme_OS_4")
                || Pattern.compile("Flyme OS [4|5]", Pattern.CASE_INSENSITIVE).matcher(Build.DISPLAY).find();
    }

    /**
     * 判断是否为MIUI6以上
     *
     * @return 是否为MIUI6以上
     */
    public static boolean isMIUI6Later() {
        try {
            Class<?> clz = Class.forName("android.os.SystemProperties");
            Method mtd = clz.getMethod("getString", String.class);
            String val = (String) mtd.invoke(null, "ro.miui.ui.version.name");
            val = val.replaceAll("[vV]", "");
            int version = Integer.parseInt(val);
            return version >= 6;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断是否是OPPO
     *
     * @return 是否是OPPO
     */
    public static boolean isOPPO() {
        return Build.MANUFACTURER.equalsIgnoreCase("OPPO");
    }

    /**
     * 增加View的paddingTop,增加的值为状态栏高度
     *
     * @param context 上下文
     * @param view    要设置的view
     */
    public static void setPadding(Context context, View view) {
        if (Build.VERSION.SDK_INT >= MIN_API) {
            view.setPadding(view.getPaddingLeft(), view.getPaddingTop() + getStatusBarHeight(context),
                    view.getPaddingRight(), view.getPaddingBottom());
        }
    }

    /**
     * 增加View的paddingTop,增加的值为状态栏高度 (智能判断，并设置高度)
     *
     * @param context 上下文
     * @param view    要设置的view
     */
    public static void setPaddingSmart(Context context, View view) {
        if (Build.VERSION.SDK_INT >= MIN_API) {
            ViewGroup.LayoutParams lp = view.getLayoutParams();
            if (lp != null && lp.height > 0) {
                lp.height += getStatusBarHeight(context);//增高
            }
            view.setPadding(view.getPaddingLeft(), view.getPaddingTop() + getStatusBarHeight(context),
                    view.getPaddingRight(), view.getPaddingBottom());
        }
    }

    /**
     * 增加View的高度以及paddingTop,增加的值为状态栏高度.一般是在沉浸式全屏给ToolBar用的
     *
     * @param context 上下文
     * @param view    要设置的view
     */
    public static void setHeightAndPadding(Context context, View view) {
        if (Build.VERSION.SDK_INT >= MIN_API) {
            ViewGroup.LayoutParams lp = view.getLayoutParams();
            lp.height += getStatusBarHeight(context);//增高
            view.setPadding(view.getPaddingLeft(), view.getPaddingTop() + getStatusBarHeight(context),
                    view.getPaddingRight(), view.getPaddingBottom());
        }
    }

    /**
     * 增加View上边距（MarginTop）一般是给高度为 WARP_CONTENT 的小控件用的
     *
     * @param context 上下文对象
     * @param view    要设置的view
     */
    public static void setMargin(Context context, View view) {
        if (Build.VERSION.SDK_INT >= MIN_API) {
            ViewGroup.LayoutParams lp = view.getLayoutParams();
            if (lp instanceof ViewGroup.MarginLayoutParams) {
                ((ViewGroup.MarginLayoutParams) lp).topMargin += getStatusBarHeight(context);//增高
            }
            view.setLayoutParams(lp);
        }
    }

    /**
     * 创建假的透明栏
     *
     * @param container 容器
     * @param color     颜色
     * @param alpha     透明度
     */
    public static void setTranslucentView(ViewGroup container, int color, @FloatRange(from = 0.0, to = 1.0) float alpha) {
        if (Build.VERSION.SDK_INT >= 19) {
            int mixtureColor = mixtureColor(color, alpha);
            View translucentView = container.findViewById(android.R.id.custom);
            if (translucentView == null && mixtureColor != 0) {
                translucentView = new View(container.getContext());
                translucentView.setId(android.R.id.custom);
                ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(container.getContext()));
                container.addView(translucentView, lp);
            }
            if (translucentView != null) {
                translucentView.setBackgroundColor(mixtureColor);
            }
        }
    }

    /**
     * 将颜色透明化
     *
     * @param color 颜色
     * @param alpha 透明度
     * @return 透明化后的颜色
     */
    public static int mixtureColor(int color, @FloatRange(from = 0.0, to = 1.0) float alpha) {
        int a = (color & 0xff000000) == 0 ? 0xff : color >>> 24;
        return (color & 0x00ffffff) | (((int) (a * alpha)) << 24);
    }

    /**
     * 获取状态栏高度
     *
     * @param context 上下文对象
     * @return 状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        int result = 24;
        int resId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resId > 0) {
            result = context.getResources().getDimensionPixelSize(resId);
        } else {
            result = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    result, Resources.getSystem().getDisplayMetrics());
        }
        return result;
    }
}
