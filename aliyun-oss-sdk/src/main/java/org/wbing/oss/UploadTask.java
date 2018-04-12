package org.wbing.oss;


/**
 * 上传任务
 *
 * @author 王冰
 * @date 2018/4/9
 */
public class UploadTask<Res extends UploadRes> {
    /**
     * 上传状态：等待中
     */
    public static final int STATUS_WAIT = 0;
    /**
     * 上传状态：正在上传
     */
    public static final int STATUS_GOING = 1;
    /**
     * 上传状态：上传成功
     */
    public static final int STATUS_COMPLETE = 2;
    /**
     * 上传状态：暂停上传
     */
    public static final int STATUS_PAUSE = -1;
    /**
     * 上传状态：取消上传
     */
    public static final int STATUS_CANCLE = -2;
    /**
     * 上传状态：上传失败
     */
    public static final int STATUS_FAIL = -3;

    /**
     * 要上传的数据
     */
    private Res res;
    /**
     * 任务状态
     */
    private int status;

    public UploadTask(Res res) {
        this.res = res;
    }

    public int getStatus() {
        return status;
    }

    public Res getRes() {
        return res;
    }
}
