package org.wbing.oss;


import org.wbing.oss.impl.UploaderImpl;

/**
 * 上传引擎
 *
 * @author 王冰
 * @date 2018/4/9
 */
public class UploaderEngine implements Uploader {

    private static UploaderEngine instance;

    public static UploaderEngine instance() {
        if (instance == null) {
            instance = new UploaderEngine();
        }
        return instance;
    }

    private boolean atOnceUpload = false;
    private Uploader uploader;

    private UploaderEngine() {
        uploader = new UploaderImpl();
    }

    public void setAtOnceUpload(boolean atOnceUpload) {
        this.atOnceUpload = atOnceUpload;
    }

    public boolean isAtOnceUpload() {
        return atOnceUpload;
    }

    @Override
    public String addTask(UploadTask task) {
        String taskId = uploader.addTask(task);
        if (atOnceUpload) {
            getUploadTaskListener().onStart(task);
        }
        return taskId;
    }

    @Override
    public boolean pauseTask(String taskId) {
        return uploader.pauseTask(taskId);
    }

    @Override
    public boolean deleteTask(String taskId) {
        return uploader.deleteTask(taskId);
    }

    @Override
    public UploadTask pullWaitingTask() {
        return uploader.pullWaitingTask();
    }

    @Override
    public UploadTask pullTaskById(String taskId) {
        return uploader.pullTaskById(taskId);
    }

    @Override
    public void addUploadTaskListener(UploadTaskListener taskListener) {
        uploader.addUploadTaskListener(taskListener);
    }

    @Override
    public void removeUploadTaskListener(UploadTaskListener taskListener) {
        uploader.removeUploadTaskListener(taskListener);
    }

    @Override
    public UploadTaskListener getUploadTaskListener() {
        return uploader.getUploadTaskListener();
    }

}
