package com.tumao.sync.ui.main;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.tumao.sync.App;
import com.tumao.sync.R;
import com.tumao.sync.bean.SimpleAlbum;
import com.tumao.sync.ui.album.AlbumActivity;
import com.tumao.sync.ui.base.BaseFragment;
import com.tumao.sync.util.HttpConnectionUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 王冰
 * @date 2018/5/9
 */
public class WorkFragment extends BaseFragment {
    public static WorkFragment newInstance() {

        Bundle args = new Bundle();

        WorkFragment fragment = new WorkFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private Adapter adapter;
    private RecyclerView content;
    private SwipeRefreshLayout refresh;
    private int page;
    private Task task;

    @Override
    public int rootLayoutId() {
        return R.layout.fragment_tab_work;
    }

    @Override
    protected void onCreateView() {
        super.onCreateView();

        refresh = (SwipeRefreshLayout) findViewById(R.id.refresh);
        refresh.setOnRefreshListener(this::refresh);

        content = (RecyclerView) findViewById(R.id.content);
        content.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new Adapter(R.layout.item_tab_work);
        adapter.bindToRecyclerView(content);
        adapter.setOnLoadMoreListener(this::loadMore, content);
        adapter.setEmptyView(R.layout.fragment_tab_work_empty);
        adapter.setOnItemClickListener((a, v, p) -> {
            SimpleAlbum item = adapter.getItem(p);
            if (item == null) {
                return;
            }
            AlbumActivity.start(getActivity(), item.getId());
        });
        content.setAdapter(adapter);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (task != null && !task.isCancelled()) {
            task.cancel(true);
        }
    }

    private void refresh() {

        if (task != null && !task.isCancelled()) {
            task.cancel(true);
        }

        Map<String, String> params = new HashMap<>();
        params.put("user_id", App.getApp().getUserInfo().getUser_id());
        params.put("page", "1");
        task = new Task(response -> {
            if (response.isSuccess()) {
                adapter.setNewData(response.info);
                refresh.setRefreshing(false);
                adapter.loadMoreComplete();
                adapter.setEnableLoadMore(response.page.hasMore());
                page = 2;
            }
        });
        task.execute(params);
    }

    private void loadMore() {
        if (task != null && !task.isCancelled()) {
            task.cancel(true);
        }
        Map<String, String> params = new HashMap<>();
        params.put("user_id", App.getApp().getUserInfo().getUser_id());
        params.put("page", String.valueOf(page));

        task = new Task(response -> {
            if (response.isSuccess()) {
                adapter.addData(response.info);
                refresh.setRefreshing(false);
                adapter.loadMoreComplete();
                adapter.setEnableLoadMore(response.page.hasMore());
                page = response.page.getNext_page();
                if (response.page.hasMore()) {
                    adapter.setEnableLoadMore(true);
                } else {
                    adapter.setEnableLoadMore(false);
                    adapter.loadMoreEnd();
                }
            }
        });
        task.execute(params);
    }

    class Adapter extends BaseQuickAdapter<SimpleAlbum, BaseViewHolder> {

        public Adapter(int layoutResId) {
            super(layoutResId);
        }

        @Override
        protected void convert(BaseViewHolder helper, SimpleAlbum item) {
            Glide.with(WorkFragment.this).load(item.getCover_img()).into((ImageView) helper.getView(R.id.img));
            ((TextView) helper.getView(R.id.name)).setText(item.getName());
            ((TextView) helper.getView(R.id.time)).setText(item.getCreated_at());
            ((TextView) helper.getView(R.id.price)).setText(item.getMoney() + "元");
            ((TextView) helper.getView(R.id.type)).setText(item.getType());
        }
    }

    static class Task extends AsyncTask<Map<String, String>, Void, SimpleAlbum.Response> {

        private OnResponse onResponse;

        public Task(OnResponse onResponse) {
            this.onResponse = onResponse;
        }

        @Override
        protected SimpleAlbum.Response doInBackground(Map<String, String>[] maps) {
            String response = HttpConnectionUtil.getHttp().postRequset("http://yst.tomomall.com/app/direct.php", maps[0]);
            return App.getApp().getGson().fromJson(response, SimpleAlbum.Response.class);
        }

        @Override
        protected void onPostExecute(SimpleAlbum.Response response) {
            super.onPostExecute(response);
            if (onResponse != null) {
                onResponse.response(response);
            }
        }
    }

    interface OnResponse {
        void response(SimpleAlbum.Response albums);
    }
}
