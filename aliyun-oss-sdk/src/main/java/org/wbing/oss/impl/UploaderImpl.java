package org.wbing.oss.impl;

import android.content.ContentValues;
import android.util.Log;

import org.wbing.oss.UploadTask;
import org.wbing.oss.UploadTaskListener;
import org.wbing.oss.Uploader;
import org.wbing.oss.UploaderEngine;
import org.wbing.oss.database.DBManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 王冰
 * @date 2018/4/13
 */
public class UploaderImpl implements Uploader {

    private static final String TAG = "UploaderImpl";

    private Map<String, UploadTask> map = new HashMap<>();

    private DBManager dbManager;

    private List<UploadTaskListener> taskListenerList = new ArrayList<>();
    private UploadTaskListener taskListener = new UploadTaskListener() {

        @Override
        public void onCreate(UploadTask task) {
            task.onCreate();
            for (UploadTaskListener listener : taskListenerList) {
                listener.onCreate(task);
            }
            ContentValues cv = new ContentValues();
            cv.put("status", task.getStatus());
            cv.put("extra", task.getExtra());
            getDbManager().update(task.getId(), cv);

            Log.e(TAG + " onCreate", task.toString());
        }

        @Override
        public void onStart(UploadTask task) {
            task.onStart();
            for (UploadTaskListener listener : taskListenerList) {
                listener.onStart(task);
            }
            ContentValues cv = new ContentValues();
            cv.put("status", task.getStatus());
            getDbManager().update(task.getId(), cv);

            Log.e(TAG + " onStart", task.toString());
        }

        @Override
        public void onProgress(UploadTask task, long length, long total) {
            task.onProgress(length, total);
            for (UploadTaskListener listener : taskListenerList) {
                listener.onProgress(task, length, total);
            }
            ContentValues cv = new ContentValues();
            cv.put("status", task.getStatus());
            cv.put("length", length);
            cv.put("total", total);
            getDbManager().update(task.getId(), cv);

            Log.e(TAG + " onProgress", task.toString());
        }

        @Override
        public void onComplete(UploadTask task) {
            task.onComplete();
            for (UploadTaskListener listener : taskListenerList) {
                listener.onComplete(task);
            }
            ContentValues cv = new ContentValues();
            cv.put("status", task.getStatus());
            cv.put("url", task.getUrl());
            getDbManager().update(task.getId(), cv);

            Log.e(TAG + " onComplete", task.toString());
        }

        @Override
        public boolean onError(UploadTask task, Throwable throwable) {
            task.onError(throwable);
            for (UploadTaskListener listener : taskListenerList) {
                listener.onError(task, throwable);
            }
            ContentValues cv = new ContentValues();
            cv.put("status", task.getStatus());
            getDbManager().update(task.getId(), cv);

            Log.e(TAG + " onError", task.toString());
            return true;
        }

        @Override
        public void onPause(UploadTask task) {
            task.onPause();
            for (UploadTaskListener listener : taskListenerList) {
                listener.onPause(task);
            }
            ContentValues cv = new ContentValues();
            cv.put("status", task.getStatus());
            getDbManager().update(task.getId(), cv);

            Log.e(TAG + " onPause", task.toString());
        }

        @Override
        public void onCancel(UploadTask task) {
            task.onCancle();
            for (UploadTaskListener listener : taskListenerList) {
                listener.onCancel(task);
            }
            ContentValues cv = new ContentValues();
            cv.put("status", task.getStatus());
            getDbManager().update(task.getId(), cv);

            Log.e(TAG + " onCancel", task.toString());
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
        if (!map.containsKey(id)) {
            map.put(id, task);
        }
        getDbManager().createOrUpdateTask(task);
        getUploadTaskListener().onCreate(task);
        return id;
    }

    @Override
    public boolean pauseTask(String taskId) {
        UploadTask task = map.get(taskId);
        if (task == null) {
            return false;
        } else {
            getUploadTaskListener().onPause(task);
            return true;
        }
    }

    @Override
    public boolean deleteTask(String taskId) {
        UploadTask task = map.get(taskId);
        if (task == null) {
            return false;
        } else {
            getUploadTaskListener().onCancel(task);
            return true;
        }
    }

    @Override
    public UploadTask pullWaitingTask() {
        UploadTask task = null;
        for (Map.Entry<String, UploadTask> entry : map.entrySet()) {
            if (entry.getValue().getStatus() == UploadTask.STATUS_WAIT) {
                task = entry.getValue();
                break;
            }
        }
        return task;
    }

    @Override
    public UploadTask pullTaskById(String taskId) {
        return map.get(taskId);
    }

    @Override
    public List<UploadTask> pullAllTask() {
        return new ArrayList<UploadTask>() {{
            addAll(map.values());
        }};
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
    public UploadTaskListener getUploadTaskListener() {
        return taskListener;
    }

    @Override
    public void reset() {
        map.clear();
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

    private synchronized DBManager getDbManager() {
        if (dbManager == null) {
            dbManager = new DBManager(UploaderEngine.instance().getApp());
        }
        return dbManager;
    }
}
