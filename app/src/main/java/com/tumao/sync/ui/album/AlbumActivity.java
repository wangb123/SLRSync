package com.tumao.sync.ui.album;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.tumao.sync.R;
import com.tumao.sync.ui.base.BaseActivity;
import com.tumao.sync.view.IndexIndicatorView;

import java.text.DecimalFormat;

public class AlbumActivity extends BaseActivity {

    public static void start(Context context, String id) {
        Intent starter = new Intent(context, AlbumActivity.class);
        context.startActivity(starter);
    }

    private static final int MSG_SPEED = 2018051001;

    private TextView speed;
    private long rxtxTotal = 0;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_SPEED) {
                updateViewData();
                handler.sendEmptyMessageDelayed(MSG_SPEED, TIME_SPAN);
            }
        }
    };

    private static final int DEFAULT_TRANSLATE_ANI_TIME = 200;

    /**
     * 向上的位移动画
     */
    private TranslateAnimation upAnimation;
    /**
     * 向下的位移动画
     */
    private TranslateAnimation downAnimation;

    //初始化动画
    {
        upAnimation = new TranslateAnimation(TranslateAnimation.RELATIVE_TO_SELF, 0f,
                TranslateAnimation.RELATIVE_TO_SELF, 0f,
                TranslateAnimation.RELATIVE_TO_SELF, 0f,
                TranslateAnimation.RELATIVE_TO_SELF, -1f);
        upAnimation.setInterpolator(new LinearInterpolator());
        upAnimation.setDuration(DEFAULT_TRANSLATE_ANI_TIME);
        upAnimation.setFillAfter(false);

        downAnimation = new TranslateAnimation(TranslateAnimation.RELATIVE_TO_SELF, 0f,
                TranslateAnimation.RELATIVE_TO_SELF, 0f,
                TranslateAnimation.RELATIVE_TO_SELF, -1f,
                TranslateAnimation.RELATIVE_TO_SELF, 0f);
        downAnimation.setInterpolator(new LinearInterpolator());
        downAnimation.setDuration(DEFAULT_TRANSLATE_ANI_TIME);
        downAnimation.setFillAfter(false);

    }

    private IndexIndicatorView index1;
    private IndexIndicatorView index2;
    private TextView index1Text;
    private TextView index2Text;
    private View filter;
    private View index1Target;
    private View index2Target;
    private View start;
    private View stop;

    private int filter1 = -1;
    private int filter2 = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        speed = findViewById(R.id.speed);
        index1 = findViewById(R.id.index1);
        index2 = findViewById(R.id.index2);
        index1Text = findViewById(R.id.index1_text);
        index2Text = findViewById(R.id.index2_text);
        filter = findViewById(R.id.filter);
        index1Target = findViewById(R.id.index1Target);
        index2Target = findViewById(R.id.index2Target);
        start = findViewById(R.id.start);
        stop = findViewById(R.id.stop);


        //设置筛选条件的切换
        index1.addOnCheckedChangeListener((view, isChecked) -> resetFilter(index1Target, isChecked));
        index2.addOnCheckedChangeListener((view, isChecked) -> resetFilter(index2Target, isChecked));

        rxtxTotal = TrafficStats.getTotalRxBytes()
                + TrafficStats.getTotalTxBytes();

        findViewById(R.id.log).setOnClickListener(v -> LogActivity.start(this));


        filter1(0);
        filter2(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.removeMessages(MSG_SPEED);
        handler.sendEmptyMessage(MSG_SPEED);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeMessages(MSG_SPEED);
    }

    public void filter(View v) {
        index1.setChecked(false);
        index2.setChecked(false);

        switch (v.getId()) {
            case R.id.filter_all:
                filter1(0);
                break;
            case R.id.filter_upload:
                filter1(1);
                break;
            case R.id.filter_wait:
                filter1(2);
                break;
            case R.id.filter_auto:
                filter2(0);
                break;
            case R.id.filter_hand:
                filter2(1);
                break;
        }
    }

    private void filter1(int filter) {
        if (filter1 == filter) {
            return;
        }
        filter1 = filter;
        SpannableStringBuilder builder = new SpannableStringBuilder();
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(0xFFFAD009);
        switch (filter1) {
            case 0:
                builder.append("全部").append("(").append(String.valueOf(0)).append(")");
                builder.setSpan(colorSpan, 2, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                break;
            case 1:
                builder.append("已上传").append("(").append(String.valueOf(0)).append(")");
                builder.setSpan(colorSpan, 3, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                break;
            case 2:
                builder.append("未上传").append("(").append(String.valueOf(0)).append(")");
                builder.setSpan(colorSpan, 3, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                break;
            default:
                break;
        }
        index1Text.setText(builder);
    }

    private void filter2(int filter) {
        if (filter2 == filter) {
            return;
        }
        filter2 = filter;
        String str = null;
        switch (filter2) {
            case 0:
                str = "自动边拍边传";
                stop.setVisibility(View.VISIBLE);
                start.setVisibility(View.GONE);
                break;
            case 1:
                str = "手动点选上传";
                stop.setVisibility(View.GONE);
                start.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
        index2Text.setText(str);
    }

    /**
     * 重置选择器背景的显示、隐藏
     *
     * @param view      相关的view
     * @param isChecked 是否选中
     */
    private void resetFilter(View view, boolean isChecked) {
        if (index1.isChecked() || index2.isChecked()) {
            filter.setVisibility(View.VISIBLE);
        } else {
            filter.setVisibility(View.GONE);
        }

        view.clearAnimation();
        if (isChecked) {
            view.startAnimation(downAnimation);
            view.setVisibility(View.VISIBLE);
        } else {
            view.startAnimation(upAnimation);
            view.setVisibility(View.GONE);
        }
    }

    int TIME_SPAN = 1000;

    public void updateViewData() {
        long tempSum = TrafficStats.getUidRxBytes(getApplicationInfo().uid)
                + TrafficStats.getUidTxBytes(getApplicationInfo().uid);
        long rxtxLast = tempSum - rxtxTotal;
        double totalSpeed = rxtxLast * 1000d / TIME_SPAN;
        rxtxTotal = tempSum;
        if (totalSpeed >= 0d) {
            speed.setText(showSpeed(totalSpeed));
        }
    }

    private DecimalFormat showFloatFormat = new DecimalFormat("0.00");

    private String showSpeed(double speed) {
        String speedString;
        if (speed >= 1048576d) {
            speedString = showFloatFormat.format(speed / 1048576d) + "MB/s";
        } else {
            speedString = showFloatFormat.format(speed / 1024d) + "KB/s";
        }
        return speedString;
    }

}
