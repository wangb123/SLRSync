package com.tumao.sync.util;

import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.widget.Toast;

import com.tumao.sync.App;


/**
 * Created by 王冰 on 2016/12/20.
 */

public class ToastUtils {
    private static Handler handler = new Handler(Looper.getMainLooper());
    private static Toast toast = null;

    public static void showMessage(String msg) {
        showMessage(msg, Toast.LENGTH_SHORT);
    }

    public static void showMessageLong(String msg) {
        showMessage(msg, Toast.LENGTH_LONG);
    }

    public static void showMessage(int msg) {
        showMessage(msg, Toast.LENGTH_SHORT);
    }

    public static void showMessageLong(int msg) {
        showMessage(msg, Toast.LENGTH_LONG);
    }

    public static void showMessage(final int msg, final int len) {
        new Thread(new Runnable() {
            public void run() {
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        synchronized (ToastUtils.class) {
                            if (toast != null) {
                                toast.setText(msg);
                                toast.setDuration(len);
                            } else {
                                toast = Toast.makeText(App.getApp().getApplicationContext(), msg, len);
                            }
                            toast.show();
                        }
                    }
                });
            }
        }).start();
    }

    public static void showMessage(final String msg, final int len) {
        new Thread(new Runnable() {
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (ToastUtils.class) {
                            if (toast != null) {
                                toast.setText(msg);
                                toast.setDuration(len);
                            } else {
                                toast = Toast.makeText(App.getApp().getApplicationContext(), msg, len);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                            }
                            toast.show();
                        }
                    }
                });
            }
        }).start();
    }

    public static void cancelCurrentToast() {
        if (toast != null) {
            toast.cancel();
        }
    }
}
