package com.tumao.sync.ui.base;

import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.tumao.sync.util.StatusBarUtils;

/**
 * 基础的activity
 *
 * @author 王冰
 * @date 2018/5/8
 */
public abstract class BaseActivity extends AppCompatActivity {

    /**
     * 设置状态栏文字颜色为黑色
     *
     * @param isLight 是否为黑色
     */
    public void resetStatusBarMode(boolean isLight) {
        if (isLight) {
            StatusBarUtils.StatusBarLightMode(this);
        } else {
            StatusBarUtils.StatusBarDarkMode(this);
        }
    }

    public void finish(View v) {
        finish();
    }
}
