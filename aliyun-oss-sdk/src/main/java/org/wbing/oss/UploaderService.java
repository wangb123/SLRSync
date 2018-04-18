package org.wbing.oss;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

public class UploaderService extends Service {
    public static void start(Context context) {
        Intent starter = new Intent(context, UploaderService.class);
        context.startService(starter);
    }

    public static void bind(Context context, ServiceConnection conn) {
        Intent starter = new Intent(context, UploaderService.class);
        context.bindService(starter, conn, Context.BIND_AUTO_CREATE);
    }

    private Handler handler;

    public UploaderService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new ServiceBinder(this, intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        handler();
    }

    private void handler() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                handler();
            }
        }, 1000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
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
