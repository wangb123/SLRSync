package com.tumao.sync.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by mier on 16/7/7.
 */
public class StatusBarUtils {

    /**
     * 修改状态栏颜色，支持4.4以上版本
     *
     * @param activity
     * @param color
     * @param dark      false:设置状态栏黑色字体图标
     * @param fitSystem
     * @return
     */
    public static boolean setStatusBarColor(Activity activity, int color, boolean dark, boolean fitSystem) {
        int flag = -1;
        if (dark) {
            flag = StatusBarUtils.StatusBarDarkMode(activity);
        } else {
            flag = StatusBarUtils.StatusBarLightMode(activity);
        }
        if (flag == -1 && color != 0x00000000) {
            color = 0x66000000;
        }
        return StatusBarUtils.setStatusBarColor(activity, color, fitSystem);
    }

    /**
     * 修改状态栏颜色，支持4.4以上版本
     *
     * @param activity
     * @param color
     */
    public static boolean setStatusBarColor(Activity activity, int color, boolean fitSystem) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window win = activity.getWindow();
            win.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                win.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                win.getDecorView().setSystemUiVisibility(win.getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                win.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                win.setStatusBarColor(Color.TRANSPARENT);
            }
            ViewGroup decorViewGroup = (ViewGroup) win.getDecorView();
            ViewGroup rootView = (ViewGroup) decorViewGroup.findViewById(android.R.id.content);
            if (rootView != null) {
                View contentView = rootView.getChildAt(0);
                if (contentView != null) {
                    contentView.setFitsSystemWindows(fitSystem);
                }
                View mStatusBarTintView = rootView.getChildAt(1);
                if (mStatusBarTintView == null) {
                    mStatusBarTintView = new View(activity);
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(activity));
                    params.gravity = Gravity.TOP;
                    mStatusBarTintView.setLayoutParams(params);
                    rootView.addView(mStatusBarTintView);
                }
                mStatusBarTintView.setBackgroundColor(color);
            }
            return true;
        }
        return false;
    }

    /**
     * 修改状态栏颜色，支持4.4以上版本
     *
     * @param activity
     * @param color
     */
    public static void setStatusBarColor(Activity activity, int color) {
        setStatusBarColor(activity, color, true);
    }

    /**
     * 设置状态栏黑色字体图标，
     * 适配4.4以上版本MIUIV、Flyme和6.0以上版本其他Android
     *
     * @param activity
     * @return 1:MIUUI 2:Flyme 3:android6.0
     */
    public static int StatusBarLightMode(Activity activity) {
        int result = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (MIUISetStatusBarLightMode(activity.getWindow(), true)) {
                result = 1;
            } else if (FlymeSetStatusBarLightMode(activity, true)) {
                result = 2;
            } else if (Build.VERSION.SDK_INT >= 23) {
                activity.getWindow().getDecorView().setSystemUiVisibility(activity.getWindow().getDecorView().getSystemUiVisibility() | 0x00000400 | 0x00002000);
                result = 3;
            } else {
                result = -1;
            }
        }
        return result;
    }

    /**
     * 清除MIUI或flyme或6.0以上版本状态栏黑色字体
     */
    public static int StatusBarDarkMode(Activity activity) {
        int result = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (MIUISetStatusBarLightMode(activity.getWindow(), false)) {
                result = 1;
            } else if (FlymeSetStatusBarLightMode(activity, false)) {
                result = 2;
            } else if (Build.VERSION.SDK_INT >= 23) {
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                result = 3;
            } else {
                result = -1;
            }
        }
        return result;
    }

    /**
     * 修改状态栏为全透明
     *
     * @param activity
     */
    @TargetApi(19)
    private static void transparencyBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(window.getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = activity.getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * 设置状态栏图标为深色和魅族特定的文字风格
     * 可以用来判断是否为Flyme用户
     *
     * @param activity 需要设置的上下文
     * @param dark     是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    private static boolean FlymeSetStatusBarLightMode(Activity activity, boolean dark) {
        boolean result = false;
        if (activity != null) {
            try {
                Method mSetStatusBarDarkIcon = Activity.class.getMethod("setStatusBarDarkIcon", boolean.class);
                mSetStatusBarDarkIcon.invoke(activity, dark);

                WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                activity.getWindow().setAttributes(lp);
                result = true;
            } catch (Exception e) {

            }
        }
        return result;
    }

    /**
     * 设置状态栏字体图标为深色，需要MIUIV6以上
     *
     * @param window 需要设置的窗口
     * @param dark   是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    private static boolean MIUISetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            Class clazz = window.getClass();
            try {
                int darkModeFlag = 0;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if (dark) {
                    //状态栏透明且黑色字体
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag);
                    //适配miui9
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    }
                } else {
                    //清除黑色字体
                    extraFlagField.invoke(window, 0, darkModeFlag);
                    //适配miui9
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        int flag = window.getDecorView().getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                        window.getDecorView().setSystemUiVisibility(flag);
                    }
                }
                result = true;
            } catch (Exception e) {

            }
        }
        return result;
    }

    /**
     * 获取手机状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }
}
