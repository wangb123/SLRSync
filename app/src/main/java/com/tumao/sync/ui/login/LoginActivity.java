package com.tumao.sync.ui.login;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.tumao.sync.App;
import com.tumao.sync.R;
import com.tumao.sync.bean.UserInfo;
import com.tumao.sync.ui.base.BaseActivity;
import com.tumao.sync.ui.main.MainActivity;
import com.tumao.sync.util.HttpConnectionUtil;
import com.tumao.sync.util.SpHelper;
import com.tumao.sync.util.ToastUtils;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends BaseActivity {

    public static void start(Context context) {
        Intent starter = new Intent(context, LoginActivity.class);
        context.startActivity(starter);
    }

    private EditText account;
    private EditText pwd;
    private LoginTask loginTask;
    private TextView icon;
    private OnLogin onLogin = info -> {
        if (info != null) {
            SpHelper.putBoolean(UserInfo.class, "login", true);
            App.getApp().setUserInfo(info);
            MainActivity.start(this);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        resetStatusBarMode(true);

        account = findViewById(R.id.account);
        pwd = findViewById(R.id.pwd);
        icon = findViewById(R.id.remember_icon);

        findViewById(R.id.remember).setOnClickListener(v -> icon.setText(TextUtils.isEmpty(icon.getText()) ? "√" : ""));
        findViewById(R.id.login).setOnClickListener(this::login);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loginTask != null && !loginTask.isCancelled()) {
            loginTask.cancel(true);
        }
    }

    /**
     * 登录
     *
     * @param v
     */
    private void login(View v) {
        String a = account.getText().toString().trim();
        String p = pwd.getText().toString().trim();

        if (TextUtils.isEmpty(a)) {
            ToastUtils.showMessage("请输入用户名");
            return;
        }
        if (TextUtils.isEmpty(p)) {
            ToastUtils.showMessage("请输入密码");
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("phone", a);
        params.put("pwd", p);

        if (loginTask != null && !loginTask.isCancelled()) {
            loginTask.cancel(true);
        }
        loginTask = new LoginTask(onLogin);
        loginTask.execute(params);
    }

    static class LoginTask extends AsyncTask<Map<String, String>, Void, UserInfo.Response> {

        private OnLogin onLogin;

        public LoginTask(OnLogin onLogin) {
            this.onLogin = onLogin;
        }

        @Override
        protected UserInfo.Response doInBackground(Map<String, String>[] maps) {
            String response = HttpConnectionUtil.getHttp().postRequset("http://yst.tomomall.com/app/login.php", maps[0]);
            SpHelper.putString(UserInfo.class, "info", response);
            return App.getApp().getGson().fromJson(response, UserInfo.Response.class);
        }

        @Override
        protected void onPostExecute(UserInfo.Response response) {
            if (response == null) {
                ToastUtils.showMessage("网络错误");
                return;
            }
            if (response.isSuccess()) {
                if (onLogin != null) {
                    onLogin.onLogin(response.info);
                }
            } else {
                ToastUtils.showMessage(response.tip);
            }

        }

        @Override
        protected void onCancelled() {
            onLogin = null;
        }
    }

    interface OnLogin {
        void onLogin(UserInfo userInfo);
    }
}
