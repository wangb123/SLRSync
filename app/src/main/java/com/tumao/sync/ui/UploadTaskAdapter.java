package com.tumao.sync.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tumao.sync.R;

import org.wbing.oss.UploadTask;
import org.wbing.oss.UploaderEngine;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 王冰
 * @date 2018/4/17
 */
public class UploadTaskAdapter extends RecyclerView.Adapter<UploadTaskAdapter.Holder> {

    public static final String PAYLOAD_STATUS = "status";
    public static final String PAYLOAD_PROGRESS = "progress";

    private List<String> taskIdList = new ArrayList<>();

    public void clear() {
        this.taskIdList.clear();
        notifyDataSetChanged();
    }

    public int indexOf(String taskId) {
        return taskIdList.indexOf(taskId);
    }

    public void add(String taskId) {
        if (taskIdList.contains(taskId)) {
            return;
        }
        taskIdList.add(taskId);
        notifyItemInserted(taskIdList.indexOf(taskId));
    }

    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file_task, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.fill(UploaderEngine.instance().pullTaskById(taskIdList.get(position)));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            for (Object obj : payloads) {
                switch (obj.toString()) {
                    case PAYLOAD_STATUS:
                        holder.fill(UploaderEngine.instance().pullTaskById(taskIdList.get(position)));
                        break;
                    case PAYLOAD_PROGRESS:
                        holder.fill(UploaderEngine.instance().pullTaskById(taskIdList.get(position)));
                        break;
                }
            }
        }
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public int getItemCount() {
        return taskIdList == null ? 0 : taskIdList.size();
    }

    class Holder extends RecyclerView.ViewHolder {

        ImageView pic;
        TextView path;

        Holder(View itemView) {
            super(itemView);
            pic = itemView.findViewById(R.id.pic);
            path = itemView.findViewById(R.id.path);
        }

        void fill(UploadTask task) {
            path.setText(task.getRes().getFile().getName().substring(13));
            path.append("\n" + getStatusString(task.getStatus()));
            Glide.with(itemView.getContext())
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
