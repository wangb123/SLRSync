package org.wbing.oss.impl;

import android.util.Log;

import org.wbing.oss.UploadRes;
import org.wbing.oss.UploadTask;
import org.wbing.oss.UploadTaskListener;
import org.wbing.oss.Uploader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 王冰
 * @date 2018/4/13
 */
public class UploaderImpl implements Uploader {

    private static final String TAG = "UploaderImpl";

    private List<UploadTaskListener> taskListenerList = new ArrayList<>();
    private UploadTaskListener taskListener = new UploadTaskListener() {

        @Override
        public void onCreate(UploadTask task) {
            task.onCreate();
            for (UploadTaskListener listener : taskListenerList) {
                listener.onCreate(task);
            }
            Log.e(TAG + " onCreate", task.toString());
        }

        @Override
        public void onStart(UploadTask task) {
            task.onStart();
            Log.e(TAG + " onStart", task.toString());
        }

        @Override
        public void onProgress(UploadTask task, int length, int total) {
            Log.e(TAG + " onProgress", task.toString());
        }

        @Override
        public boolean onError(UploadTask task, Throwable throwable) {
            Log.e(TAG + " onError", task.toString());
            throwable.printStackTrace();
            return false;
        }

        @Override
        public void onPause(UploadTask task) {

            Log.e(TAG + " onPause", task.toString());
        }

        @Override
        public void onCancle(UploadTask task) {
            Log.e(TAG + " onCancle", task.toString());
        }
    };

    public UploaderImpl() {

    }

    @Override
    public String addTask(UploadTask task) {
        String id;
        if (task.getRes().getFile() != null) {
            id = fileToMD5(task.getRes().getFile());
        } else {
            id = byteToMD5(task.getRes().getByte());
        }
        task.setId(id);
        getUploadTaskListener().onCreate(task);
        return id;
    }

    @Override
    public boolean pauseTask(String taskId) {
        return false;
    }

    @Override
    public boolean deleteTask(String taskId) {
        return false;
    }

    @Override
    public UploadTask pullWaitingTask() {
        return null;
    }

    @Override
    public UploadTask pullTaskById(String taskId) {
        return null;
    }

    @Override
    public List<UploadTask> pullAllTask() {
        return null;
    }

    @Override
    public void addUploadTaskListener(UploadTaskListener taskListener) {
        if (!taskListenerList.contains(taskListener)) {
            taskListenerList.add(taskListener);
        }
    }

    @Override
    public void removeUploadTaskListener(UploadTaskListener taskListener) {
        if (taskListenerList.contains(taskListener)) {
            taskListenerList.remove(taskListener);
        }
    }

    @Override
    public UploadTaskListener<UploadRes> getUploadTaskListener() {
        return taskListener;
    }

    private String fileToMD5(File file) {
        if (file == null || !file.isFile()) {
            return "";
        }
        MessageDigest messageDigest = null;
        InputStream fis = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int numRead;
            while ((numRead = fis.read(buffer)) > 0) {
                messageDigest.update(buffer, 0, numRead);
            }
            return toHexStr(messageDigest.digest());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ex) {
                }
            }
        }
        return "";
    }

    private String byteToMD5(byte[] bytes) {
        if (bytes == null) {
            return "";
        }
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(bytes, 0, bytes.length);
            return toHexStr(messageDigest.digest());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private String toHexStr(byte[] bytes) {
        StringBuilder md5sb = new StringBuilder();
        for (byte b : bytes) {
            md5sb.append(String.format("%02x", b & 0xff));
        }
        return md5sb.toString();
    }
}
