package com.tumao.sync;

import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tumao.sync.bean.UserInfo;
import com.tumao.sync.util.SpHelper;

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

    private Gson gson;
    private UserInfo userInfo;

    public UserInfo getUserInfo() {
        if (userInfo == null) {
            synchronized (UserInfo.class) {
                if (userInfo == null) {
                    if (SpHelper.getBoolean(UserInfo.class, "login")) {
                        String info = SpHelper.getString(UserInfo.class, "info");
                        UserInfo.Response response = App.getApp().getGson().fromJson(info, UserInfo.Response.class);
                        if (response != null && response.isSuccess()) {
                            userInfo = response.info;
                        }
                    }
                }
            }
        }
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        UploaderEngine.instance().setApp(this);
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
