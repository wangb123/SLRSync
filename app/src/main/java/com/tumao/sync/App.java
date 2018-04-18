package com.tumao.sync;

import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

    private Gson gson;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }

    public File getExternalSLRThumbDir() {
        File SLRThumb = getExternalFilesDir("SLRThumb");
        if (!SLRThumb.exists()) {
            SLRThumb.mkdirs();
        }
        return SLRThumb;
    }

    public File getExternalSLRThumbCompressDir() {
        File compress = new File(getExternalSLRThumbDir(), "Compress");
        if (!compress.exists()) {
            compress.mkdirs();
        }
        return compress;
    }

    public Gson getGson() {
        if (gson == null) {
            gson = new GsonBuilder()
                    .enableComplexMapKeySerialization() //支持Map的key为复杂对象的形式
                    .create();
        }
        return gson;
    }
}
