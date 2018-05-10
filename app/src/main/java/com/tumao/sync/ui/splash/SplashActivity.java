package com.tumao.sync.ui.splash;

import android.os.Bundle;
import android.view.View;

import com.tumao.sync.App;
import com.tumao.sync.R;
import com.tumao.sync.ui.base.BaseActivity;
import com.tumao.sync.ui.login.LoginActivity;
import com.tumao.sync.ui.main.MainActivity;
import com.tumao.sync.ui.unknown.UnknownActivity;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (App.getApp().getUserInfo() == null) {
            findViewById(R.id.wrapper).setVisibility(View.VISIBLE);
            findViewById(R.id.register).setOnClickListener(this::register);
            findViewById(R.id.login).setOnClickListener(this::login);
            findViewById(R.id.login_wx).setOnClickListener(this::loginWx);
        } else {
            MainActivity.start(this);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isFinishing()) {
            return;
        }
        if (App.getApp().getUserInfo() != null) {
            finish();
        }
    }

    private void register(View view) {
        UnknownActivity.start(this);
    }

    private void login(View view) {
        LoginActivity.start(this);
    }

    private void loginWx(View view) {
        UnknownActivity.start(this);
    }
}
