package com.tumao.sync.ui.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.tumao.sync.R;
import com.tumao.sync.ui.unknown.UnknownActivity;
import com.tumao.sync.ui.unknown.UnknownFragment;
import com.tumao.sync.view.QRadioButton;

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

    public static void start(Context context) {
        Intent starter = new Intent(context, MainActivity.class);
        context.startActivity(starter);
    }

    private TextView title;
    private QRadioButton work;
    private QRadioButton school;
    private QRadioButton about;
    private QRadioButton self;

    private ViewPager content;
    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            switch (position) {
                case 0:
                    title.setText(work.getText());
                    work.setChecked(true);
                    break;
                case 1:
                    title.setText(school.getText());
                    school.setChecked(true);
                    break;
                case 2:
                    title.setText(about.getText());
                    about.setChecked(true);
                    break;
                case 3:
                    title.setText(self.getText());
                    self.setChecked(true);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.setting).setOnClickListener(this::setting);

        content = findViewById(R.id.content);
        title = findViewById(R.id.title);
        work = findViewById(R.id.work);
        work.setOnClickListener(v -> content.setCurrentItem(0, false));
        school = findViewById(R.id.school);
        school.setOnClickListener(v -> content.setCurrentItem(1, false));
        about = findViewById(R.id.about);
        about.setOnClickListener(v -> content.setCurrentItem(2, false));
        self = findViewById(R.id.self);
        self.setOnClickListener(v -> content.setCurrentItem(3, false));


        content.addOnPageChangeListener(pageChangeListener);
        PageAdapter adapter = new PageAdapter(getSupportFragmentManager());
        content.setAdapter(adapter);

        content.postDelayed(() -> pageChangeListener.onPageSelected(content.getCurrentItem()), 300);
    }

    private void setting(View view) {
        UnknownActivity.start(this);
    }


    class PageAdapter extends FragmentPagerAdapter {

        public PageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return WorkFragment.newInstance();
//                case 1:
//                    school.setChecked(true);
//                    break;
//                case 2:
//                    about.setChecked(true);
//                    break;
//                case 3:
//                    self.setChecked(true);
//                    break;
                default:
                    return UnknownFragment.newInstance();
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    }

    //    private SLRService slrService;
//
//    private CheckBox checkBox;
//    private RecyclerView recyclerView;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (slrService != null) {
//                    slrService.discoverDevice();
//                }
//            }
//        });
//
//        checkBox = findViewById(R.id.text);
//        checkBox.setChecked(UploaderEngine.instance().isAtOnceUpload());
//        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                UploaderEngine.instance().setAtOnceUpload(isChecked);
//            }
//        });
//
//        recyclerView = findViewById(R.id.task);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        //启动上传服务，防止服务被杀死
//        UploaderService.start(this);
//        SLRService.start(this);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        SLRService.bind(this, serviceConnection);
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        unbindService(serviceConnection);
//    }
//
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//    }
//
//    /**
//     *
//     */
//    private final ServiceConnection serviceConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            SLRService.ServiceBinder binder = (SLRService.ServiceBinder) service;
//            slrService = binder.getService();
//            recyclerView.setAdapter(binder.getUploadTaskAdapter());
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            slrService = null;
//        }
//    };

}
