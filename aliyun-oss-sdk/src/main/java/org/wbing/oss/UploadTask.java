package org.wbing.oss;


/**
 * 上传任务
 *
 * @author 王冰
 * @date 2018/4/9
 */

public class UploadTask<Res extends UploadRes> {


    /**
     * 上传状态：上传失败
     */
    public static final int STATUS_FAIL = -3;
    /**
     * 上传状态：取消上传
     */
    public static final int STATUS_CANCLE = -2;
    /**
     * 上传状态：暂停上传
     */
    public static final int STATUS_PAUSE = -1;
    /**
     * 上传状态：任务创建，未添加到上传队列中
     */
    public static final int STATUS_CREATE = 0;
    /**
     * 上传状态：等待中
     */
    public static final int STATUS_WAIT = 1;
    /**
     * 上传状态：正在上传
     */
    public static final int STATUS_GOING = 2;
    /**
     * 上传状态：上传成功
     */
    public static final int STATUS_COMPLETE = 3;

    /**
     * 任务id
     */
    private String id;
    /**
     * 要上传的数据
     */
    private Res res;
    /**
     * 任务状态
     */
    private int status;

    /**
     * 存放位置
     */
    private String url;
    /**
     * 上传长度
     */
    private long length;
    /**
     * 上传文件的总长度
     */
    private long total;
    /**
     * 一些额外的信息
     */
    private String extra;

    private UploadTaskListener taskListener;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public UploadTask(Res res) {
        this.res = res;
    }

    public int getStatus() {
        return status;
    }

    public Res getRes() {
        return res;
    }

    public long getLength() {
        return length;
    }

    public long getTotal() {
        return total;
    }

    public void setRes(Res res) {
        this.res = res;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public String getUrl() {
        return url;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public UploadTaskListener getTaskListener() {
        return taskListener;
    }

    public void setTaskListener(UploadTaskListener taskListener) {
        this.taskListener = taskListener;
    }

    public void onCreate() {
        this.status = STATUS_CREATE;
        if (this.taskListener != null) {
            taskListener.onCreate(this);
        }
    }

    public void onStart() {
        this.status = STATUS_WAIT;
        if (this.taskListener != null) {
            taskListener.onStart(this);
        }
    }

    public void onProgress(long length, long total) {
        this.length = length;
        this.total = total;
        this.status = STATUS_GOING;
        if (this.taskListener != null) {
            taskListener.onProgress(this, length, total);
        }
    }


    public void onComplete() {
        this.status = STATUS_COMPLETE;
        if (this.taskListener != null) {
            taskListener.onComplete(this);
        }
    }

    public void onError(Throwable throwable) {
        this.status = STATUS_FAIL;
        if (this.taskListener != null) {
            taskListener.onError(this, throwable);
        }
    }

    public void onPause() {
        this.status = STATUS_PAUSE;
        if (this.taskListener != null) {
            taskListener.onPause(this);
        }
    }

    public void onCancle() {
        this.status = STATUS_CANCLE;
        if (this.taskListener != null) {
            taskListener.onCancel(this);
        }
    }


    @Override
    public String toString() {
        return "UploadTask{" +
                "id='" + id + '\'' +
                ", res=" + res +
                ", status=" + status +
                ", url='" + url + '\'' +
                ", length=" + length +
                ", total=" + total +
                ", extra='" + extra + '\'' +
                '}';
    }

}
