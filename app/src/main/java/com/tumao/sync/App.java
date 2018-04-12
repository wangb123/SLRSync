package com.tumao.sync;

import android.app.Application;

import org.wbing.oss.UploaderEngine;

import java.io.File;

/**
 * @author 王冰
 * @date 2018/4/9
 */
public class App extends Application {
    private static App app;

    public static App getApp() {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        UploaderEngine.init(this);
    }

    public File getExternalSLRThumbDir() {
//        File SLRThumb = new File(Environment.getExternalStorageDirectory(), "SLRThumb");
        File SLRThumb = new File(getExternalCacheDir(), "SLRThumb");
        if (!SLRThumb.exists()) {
            SLRThumb.mkdirs();
        }
        return SLRThumb;
    }
}
