package org.wbing.oss;

/**
 * 接口：上传功能抽象
 *
 * @author 王冰
 * @date 2018/4/9
 */
public interface Uploader {
    /**
     * 添加一个任务
     *
     * @param task 要上传的文件
     * @return 文件的唯一标识
     */
    String addTask(UploadTask task);

    /**
     * 暂停任务
     *
     * @param taskId 任务标识
     * @return 是否成功，任务id是否存在于任务队列中
     */
    boolean pauseTask(String taskId);

    /**
     * 删除任务
     *
     * @param taskId 任务标识
     * @return 是否成功，任务id是否存在于任务队列中
     */
    boolean deleteTask(String taskId);

}