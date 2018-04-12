package org.wbing.oss;


import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

/**
 * 上传引擎
 *
 * @author 王冰
 * @date 2018/4/9
 */
public class UploaderEngine implements Uploader {

    private static Context ctx;

    public static void init(Context ctx) {
        UploaderEngine.ctx = ctx.getApplicationContext();
        UploaderService.start(ctx);
        instance();
    }

    private static UploaderEngine instance;

    public static UploaderEngine instance() {
        if (instance == null) {
            instance = new UploaderEngine();
        }
        return instance;
    }

    private Uploader uploader;

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("TAG", "on service connected " + name);
            UploaderService.ServiceBinder binder = (UploaderService.ServiceBinder) service;
            uploader = binder.getService();
            Log.e("tag", (uploader == null) + " ");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("TAG", "on service connected " + name);
            uploader = null;
        }
    };

    private UploaderEngine() {
        UploaderService.bind(ctx, serviceConnection);
    }

    @Override
    public String addTask(UploadTask task) {
        if (uploader == null) {
            UploaderService.bind(ctx, serviceConnection);
            return null;
        }
        return uploader.addTask(task);
    }

    @Override
    public boolean pauseTask(String taskId) {
        if (uploader == null) {
            UploaderService.bind(ctx, serviceConnection);
            return false;
        }
        return uploader.pauseTask(taskId);
    }

    @Override
    public boolean deleteTask(String taskId) {
        if (uploader == null) {
            UploaderService.bind(ctx, serviceConnection);
            return false;
        }
        return uploader.deleteTask(taskId);
    }

}
