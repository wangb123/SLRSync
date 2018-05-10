package com.tumao.sync.ui.unknown;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.tumao.sync.R;
import com.tumao.sync.ui.base.BaseActivity;

public class UnknownActivity extends BaseActivity {
    public static void start(Context context) {
        Intent starter = new Intent(context, UnknownActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unknown);
    }
}
