package org.wbing.oss;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class UploaderService extends Service implements Uploader {
    public static void start(Context context) {
        Intent starter = new Intent(context, UploaderService.class);
        context.startService(starter);
    }

    public static void bind(Context context, ServiceConnection conn) {
        Intent starter = new Intent(context, UploaderService.class);
        context.bindService(starter, conn, Context.BIND_AUTO_CREATE);
    }


    private static final String TAG = "UploaderService";

    public UploaderService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new ServiceBinder(this, intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }


    @Override
    public String addTask(UploadTask task) {
        return task.getRes().getFile().getAbsolutePath();
    }

    @Override
    public boolean pauseTask(String taskId) {
        return false;
    }

    @Override
    public boolean deleteTask(String taskId) {
        return false;
    }

    public static final class ServiceBinder extends Binder {
        UploaderService service;
        Intent intent;

        public ServiceBinder(UploaderService service, Intent intent) {
            this.service = service;
            this.intent = intent;
        }

        public UploaderService getService() {
            return service;
        }

        public Intent getIntent() {
            return intent;
        }

    }
}
