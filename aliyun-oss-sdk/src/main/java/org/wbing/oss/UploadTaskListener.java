package org.wbing.oss;

/**
 * @author 王冰
 * @date 2018/4/17
 */
public interface UploadTaskListener {


    void onCreate(UploadTask task);

    void onStart(UploadTask task);

    void onProgress(UploadTask task, long length, long total);

    void onComplete(UploadTask task);

    boolean onError(UploadTask task, Throwable throwable);

    void onPause(UploadTask task);

    void onCancel(UploadTask task);
}
