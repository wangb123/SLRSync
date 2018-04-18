package com.tumao.sync.ui;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tumao.sync.R;

import org.wbing.oss.UploadTask;
import org.wbing.oss.impl.FileUploadTask;

import java.util.List;

/**
 * @author 王冰
 * @date 2018/4/17
 */
public class UploadTaskAdapter extends RecyclerView.Adapter<UploadTaskAdapter.Holder> {
    Activity activity;
    List<UploadTask<FileUploadTask.FileUploadRes>> uploadTaskList;

    public UploadTaskAdapter(Activity activity) {
        this.activity = activity;
    }

    public void setUploadTaskList(List<UploadTask<FileUploadTask.FileUploadRes>> uploadTaskList) {
        this.uploadTaskList = uploadTaskList;
        notifyDataSetChanged();
    }

    public List<UploadTask<FileUploadTask.FileUploadRes>> getUploadTaskList() {
        return uploadTaskList;
    }

    public void addTask(UploadTask<FileUploadTask.FileUploadRes> task) {
//        if (this.uploadTaskList == null) {
//            this.uploadTaskList = new ArrayList<>();
//        }
//        this.uploadTaskList.add(task);
    }


    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        Holder holder = new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file_task, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        holder.fill(uploadTaskList.get(position));
    }

    @Override
    public int getItemCount() {
        return uploadTaskList == null ? 0 : uploadTaskList.size();
    }

    class Holder extends RecyclerView.ViewHolder {

        ImageView pic;
        TextView path;

        Holder(View itemView) {
            super(itemView);
            pic = itemView.findViewById(R.id.pic);
            path = itemView.findViewById(R.id.path);
        }

        void fill(UploadTask<FileUploadTask.FileUploadRes> task) {
            path.setText(task.getRes().getFile().getName());
            path.append("\n" + getStatusString(task.getStatus()));
            Glide.with(activity)
                    .load(task.getRes().getFile())
                    .into(pic);
        }

        private String getStatusString(int status) {
            switch (status) {
                case UploadTask.STATUS_FAIL:
                    return "上传错误";
                case UploadTask.STATUS_CANCLE:
                    return "取消上传";
                case UploadTask.STATUS_PAUSE:
                    return "暂停上传";
                case UploadTask.STATUS_CREATE:
                    return "创建任务";
                case UploadTask.STATUS_WAIT:
                    return "队列中，等待上传";
                case UploadTask.STATUS_GOING:
                    return "正在上传";
                case UploadTask.STATUS_COMPLETE:
                    return "上传完毕";
                default:
                    return "其他";
            }
        }
    }
}
