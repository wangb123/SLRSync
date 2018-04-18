package org.wbing.oss;

/**
 * @author 王冰
 * @date 2018/4/17
 */
public interface UploadTaskListener<Res extends UploadRes> {


    void onCreate(UploadTask<Res> task);

    void onStart(UploadTask<Res> task);

    void onProgress(UploadTask<Res> task, int length, int total);

    boolean onError(UploadTask<Res> task, Throwable throwable);

    void onPause(UploadTask<Res> task);

    void onCancle(UploadTask<Res> task);
}
