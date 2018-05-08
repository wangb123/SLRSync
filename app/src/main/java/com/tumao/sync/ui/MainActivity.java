package com.tumao.sync.ui;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.tumao.sync.R;
import com.tumao.sync.SLRService;

import org.wbing.oss.UploaderEngine;
import org.wbing.oss.UploaderService;

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

    private SLRService slrService;

    private CheckBox checkBox;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (slrService != null) {
                    slrService.discoverDevice();
                }
            }
        });

        checkBox = findViewById(R.id.text);
        checkBox.setChecked(UploaderEngine.instance().isAtOnceUpload());
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                UploaderEngine.instance().setAtOnceUpload(isChecked);
            }
        });

        recyclerView = findViewById(R.id.task);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();
        //启动上传服务，防止服务被杀死
        UploaderService.start(this);
        SLRService.start(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SLRService.bind(this, serviceConnection);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(serviceConnection);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     *
     */
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            SLRService.ServiceBinder binder = (SLRService.ServiceBinder) service;
            slrService = binder.getService();
            recyclerView.setAdapter(binder.getUploadTaskAdapter());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            slrService = null;
        }
    };

}
