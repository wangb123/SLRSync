package com.tumao.sync;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.media.ExifInterface;
import android.os.Binder;
import android.os.FileObserver;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.tumao.sync.bean.SLRExifInfo;

import org.wbing.oss.UploadTask;
import org.wbing.oss.UploadTaskListener;
import org.wbing.oss.UploaderEngine;
import org.wbing.oss.compress.Luban;
import org.wbing.oss.compress.OnCompressListener;
import org.wbing.oss.impl.FileUploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * 用于扫描设备,
 *
 * @author 王冰
 * @date 2018/4/13
 */
public class SLRService extends Service {
    private static final String ACTION_USB_PERMISSION = "com.tumao.sync.USB_PERMISSION";
    private static final String TAG = "SLRService";

    public static void start(Context context) {
        Intent starter = new Intent(context, SLRService.class);
        context.startService(starter);
    }

    public static void bind(Context context, ServiceConnection conn) {
        Intent starter = new Intent(context, SLRService.class);
        context.bindService(starter, conn, Context.BIND_AUTO_CREATE);
    }


    private SLRDevice[] slrDevices;
    private int currentDevice = -1;
    private List<UploadTask<FileUploadTask.FileUploadRes>> taskList = new ArrayList<>();

    private UploadTaskListener<FileUploadTask.FileUploadRes> taskListener = new UploadTaskListener<FileUploadTask.FileUploadRes>() {
        @Override
        public void onCreate(UploadTask<FileUploadTask.FileUploadRes> task) {
            taskList.add(task);
            File file = task.getRes().getFile();
            try {
                SLRExifInfo info = SLRExifInfo.createByExif(new ExifInterface(file.getAbsolutePath()));
                task.setExtra(info.getResultJson());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onStart(UploadTask<FileUploadTask.FileUploadRes> task) {
        }

        @Override
        public void onProgress(UploadTask<FileUploadTask.FileUploadRes> task, int length, int total) {

        }

        @Override
        public boolean onError(UploadTask<FileUploadTask.FileUploadRes> task, Throwable throwable) {
            return false;
        }

        @Override
        public void onPause(UploadTask<FileUploadTask.FileUploadRes> task) {

        }

        @Override
        public void onCancle(UploadTask<FileUploadTask.FileUploadRes> task) {

        }
    };

    public SLRService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new ServiceBinder(this, intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //注册广播，获取设备权限，设备插入，设备拔出
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);

        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        registerReceiver(usbReceiver, filter);

        Toast.makeText(getApplicationContext(), "SLRService.onCreate", Toast.LENGTH_LONG).show();
        //搜索设备（防止当前已有设备插入）
        discoverDevice();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(usbReceiver);
        if (currentDevice != -1) {
            slrDevices[currentDevice].close();
        }
    }

    public List<UploadTask<FileUploadTask.FileUploadRes>> getTaskList() {
        return taskList;
    }

    /**
     * 设置外部设备（相机）
     */
    private void setupDevice() {
        if (currentDevice < 0 || slrDevices == null || currentDevice >= slrDevices.length) {
            return;
        }
        UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        if (usbManager == null) {
            return;
        }

        SLRDevice slrDevice = slrDevices[currentDevice];
        if (usbManager.hasPermission(slrDevice.usbDevice)) {
            try {
                SDCardListener listener = new SDCardListener(slrDevice.getRoot().getAbsolutePath());
                listener.startWatching();
                slrDevice.init();
                taskList.clear();
                List<File> files = slrDevice.getFiles();
                for (File file : files) {
                    FileUploadTask uploadTask = new FileUploadTask(file);
                    uploadTask.setTaskListener(taskListener);
                    UploaderEngine.instance().addTask(uploadTask);
                }
                listener.stopWatching();
                slrDevice.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            PendingIntent permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
            usbManager.requestPermission(slrDevice.usbDevice, permissionIntent);
        }
    }

    /**
     * 扫描外部设备（相机）
     */
    public void discoverDevice() {
        slrDevices = SLRDevice.getSLRDevices(this);
        if (slrDevices.length == 0) {
            Log.w(TAG, "no device found!");
            //没有外部设备
        } else {
            currentDevice = 0;
            setupDevice();
        }
    }

    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                //请求usb权限
                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                    if (device != null) {
                        setupDevice();
                    }
                }
            } else if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                //USB设备已经插入
                Log.d(TAG, "USB device attached");
                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                // determine if connected device is a mass storage devuce
                if (device != null) {
                    discoverDevice();
                }
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                //USB设备被卸载
                Log.d(TAG, "USB device detached");
                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                // determine if connected device is a mass storage devuce
                if (device != null) {
                    if (SLRService.this.currentDevice != -1) {
                        SLRService.this.slrDevices[currentDevice].close();
                    }
                    // check if there are other devices or set action bar title
                    // to no device if not
                    discoverDevice();
                }
            }
        }
    };


    public class SDCardListener extends FileObserver {

        private String root;

        SDCardListener(String path) {
            super(path, FileObserver.CREATE | FileObserver.CLOSE_WRITE);
            root = path;
        }

        @Override
        public void onEvent(int event, String path) {
            if (path == null) {
                return;
            }
            File file = new File(root, path);
            switch (event) {
                case FileObserver.CREATE:
                    Log.e("新增文件:", file.getAbsolutePath());
                    break;
                case FileObserver.CLOSE_WRITE:
                    Log.e("文件写入完毕：", file.getAbsolutePath());
                    try {
                        SLRExifInfo info = SLRExifInfo.createByExif(new ExifInterface(file.getAbsolutePath()));

                        Luban.with(getBaseContext())
                                .load(file.getAbsolutePath())
                                .setTargetDir(App.getApp().getExternalSLRThumbCompressDir().getAbsolutePath())
                                .ignoreBy(150)
                                .height(1920)
                                .width(1080)
                                .setCompressListener(new OnCompressListener() {
                                    @Override
                                    public void onStart() {
                                        Log.e("compress", "onStart");
                                    }

                                    @Override
                                    public void onSuccess(File file) {

                                        Log.e("compress", "onSuccess=" + file.getAbsolutePath());
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        e.printStackTrace();
                                        Log.e("compress", "onError");
                                    }
                                }).launch();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public static final class ServiceBinder extends Binder {
        SLRService service;
        Intent intent;

        public ServiceBinder(SLRService service, Intent intent) {
            this.service = service;
            this.intent = intent;
        }

        public SLRService getService() {
            return service;
        }

        public Intent getIntent() {
            return intent;
        }

    }
}
