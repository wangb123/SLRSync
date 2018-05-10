package com.tumao.sync.ui.album;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.tumao.sync.R;
import com.tumao.sync.ui.base.BaseActivity;

public class LogActivity extends BaseActivity {

    public static void start(Context context) {
        Intent starter = new Intent(context, LogActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        resetStatusBarMode(true);
    }
}
