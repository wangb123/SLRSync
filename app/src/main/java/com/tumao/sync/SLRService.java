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
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.tumao.sync.bean.SLRExifInfo;
import com.tumao.sync.ui.UploadTaskAdapter;
import com.tumao.sync.util.HttpConnectionUtil;

import org.wbing.oss.UploadTask;
import org.wbing.oss.UploadTaskListener;
import org.wbing.oss.UploaderEngine;
import org.wbing.oss.impl.FileUploadTask;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
        context.getApplicationContext().startService(starter);
    }

    public static void bind(Context context, ServiceConnection conn) {
        Intent starter = new Intent(context, SLRService.class);
        context.bindService(starter, conn, Context.BIND_AUTO_CREATE);
    }

    private SLRDevice[] slrDevices;
    private int currentDevice;

    private Handler handler = new Handler(Looper.getMainLooper());


    private UploadTaskAdapter uploadTaskAdapter = new UploadTaskAdapter();

    private UploadTaskListener taskListener = new UploadTaskListener() {
        @Override
        public void onCreate(UploadTask task) {
            File file = task.getRes().getFile();
            try {
                SLRExifInfo info = SLRExifInfo.createByExif(new ExifInterface(file.getAbsolutePath()));
                task.setExtra(info.getResultJson());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onStart(UploadTask task) {
            if (uploadTaskAdapter.getItemCount() == 0) {
                return;
            }
            final int index = uploadTaskAdapter.indexOf(task.getId());
            if (index >= 0) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        uploadTaskAdapter.notifyItemChanged(index, UploadTaskAdapter.PAYLOAD_STATUS);
                    }
                });
            }
        }

        @Override
        public void onProgress(UploadTask task, long length, long total) {
            if (uploadTaskAdapter.getItemCount() == 0) {
                return;
            }
            final int index = uploadTaskAdapter.indexOf(task.getId());
            if (index >= 0) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        uploadTaskAdapter.notifyItemChanged(index, UploadTaskAdapter.PAYLOAD_PROGRESS);
                    }
                });
            }
        }

        @Override
        public boolean onError(UploadTask task, Throwable throwable) {
            if (uploadTaskAdapter.getItemCount() == 0) {
                return false;
            }
            final int index = uploadTaskAdapter.indexOf(task.getId());
            if (index >= 0) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        uploadTaskAdapter.notifyItemChanged(index, UploadTaskAdapter.PAYLOAD_STATUS);
                    }
                });
            }
            return false;
        }

        @Override
        public void onComplete(UploadTask task) {
            if (uploadTaskAdapter.getItemCount() == 0) {
                return;
            }
            final int index = uploadTaskAdapter.indexOf(task.getId());
            if (index >= 0) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        uploadTaskAdapter.notifyItemChanged(index, UploadTaskAdapter.PAYLOAD_STATUS);
                    }
                });
            }
            associatedAtlas(task);
        }

        @Override
        public void onPause(UploadTask task) {
            if (uploadTaskAdapter.getItemCount() == 0) {
                return;
            }
            final int index = uploadTaskAdapter.indexOf(task.getId());
            if (index >= 0) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        uploadTaskAdapter.notifyItemChanged(index, UploadTaskAdapter.PAYLOAD_STATUS);
                    }
                });
            }
        }

        @Override
        public void onCancel(UploadTask task) {
            if (uploadTaskAdapter.getItemCount() == 0) {
                return;
            }
            final int index = uploadTaskAdapter.indexOf(task.getId());
            if (index >= 0) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        uploadTaskAdapter.notifyItemChanged(index, UploadTaskAdapter.PAYLOAD_STATUS);
                    }
                });
            }
        }
    };

    public SLRService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new ServiceBinder(this, intent, uploadTaskAdapter);
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
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(usbReceiver);
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
                slrDevice.init(new SLRDevice.OnSLRDeviceFileScanListener() {
                    @Override
                    public void onScanStart() {
                        UploaderEngine.instance().reset();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                uploadTaskAdapter.clear();
                            }
                        });
                    }

                    @Override
                    public void onFileAdd(File file) {
                        Log.e(TAG, String.format("path:%s\nlen:%s", file.getAbsolutePath(), file.length()));
                        FileUploadTask uploadTask = new FileUploadTask(file);
                        uploadTask.setTaskListener(taskListener);
                        final String taskId = UploaderEngine.instance().addTask(uploadTask);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                uploadTaskAdapter.add(taskId);
                            }
                        });
                    }

                    @Override
                    public void onScanEnd(List<File> files) {

                    }
                });
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


    private void associatedAtlas(final UploadTask task) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, String> params = new HashMap<>();
                params.put("oss_pic_path", task.getUrl());
                params.put("exif", task.getExtra());
                params.put("file_size", String.valueOf(task.getTotal()));
                Log.e("params", params.toString());
                String param = App.getApp().getGson().toJson(params);
                params.clear();
                params.put("data", param);
                String response = HttpConnectionUtil.getHttp().postRequset("http://yst.tomomall.com/app/api.php", params);
                Log.e("associatedAtlas:", "resp:" + response);
            }
        }).start();
    }

    public static class ServiceBinder extends Binder {
        private SLRService service;
        private Intent intent;
        private UploadTaskAdapter uploadTaskAdapter;

        public ServiceBinder(SLRService service, Intent intent, UploadTaskAdapter uploadTaskAdapter) {
            this.service = service;
            this.intent = intent;
            this.uploadTaskAdapter = uploadTaskAdapter;
        }

        public SLRService getService() {
            return service;
        }

        public Intent getIntent() {
            return intent;
        }

        public UploadTaskAdapter getUploadTaskAdapter() {
            return uploadTaskAdapter;
        }
    }

}
