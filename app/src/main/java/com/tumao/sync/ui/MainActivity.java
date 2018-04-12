package com.tumao.sync.ui;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.tumao.sync.R;
import com.tumao.sync.SLRDevice;

import org.wbing.oss.UploaderService;

import java.io.IOException;

/**
 * 首页
 * 功能：
 * 1、注册广播，监听USB设备的插拔和获取设备权限
 * 2、
 *
 * @author 王冰
 * @date 2018/4/8
 */
public class MainActivity extends AppCompatActivity {

    private static final String ACTION_USB_PERMISSION = "com.tumao.sync.USB_PERMISSION";
    private static final String TAG = "MainActivity";

    private SLRDevice[] slrDevices;
    private int currentDevice = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //注册广播，获取设备权限，设备插入，设备拔出
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);

        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        registerReceiver(usbReceiver, filter);

        //搜索设备（防止当前已有设备插入）
        discoverDevice();

        findViewById(R.id.text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        //启动上传服务，防止服务被杀死
        UploaderService.start(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(usbReceiver);
        if (currentDevice != -1) {
        }
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
                slrDevice.init();
//                FileSystem currentFs = slrDevice.getPartitions().get(0).getFileSystem();
//
//                Log.e(TAG, "Directory: " + Arrays.toString(currentFs.getRootDirectory().list()));
//                Log.e(TAG, "Capacity: " + currentFs.getCapacity());
//                Log.e(TAG, "Occupied Space: " + currentFs.getOccupiedSpace());
//                Log.e(TAG, "Free Space: " + currentFs.getFreeSpace());
//                Log.e(TAG, "Chunk size: " + currentFs.getChunkSize());
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
    private void discoverDevice() {
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
                    if (MainActivity.this.currentDevice != -1) {
                        MainActivity.this.slrDevices[currentDevice].close();
                    }
                    // check if there are other devices or set action bar title
                    // to no device if not
                    discoverDevice();
                }
            }

        }
    };

}
