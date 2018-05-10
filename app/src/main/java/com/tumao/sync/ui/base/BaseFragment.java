package com.tumao.sync.ui.base;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 基础的fragment
 *
 * @author 王冰
 * @date 2018/5/8
 */
public abstract class BaseFragment extends Fragment {

    private View root;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (root == null) {
            int rootLayoutId = rootLayoutId();
            if (rootLayoutId == 0) {
                return super.onCreateView(inflater, container, savedInstanceState);
            }
            root = inflater.inflate(rootLayoutId, container, false);
            onCreateView();
        }
        return root;
    }

    protected View findViewById(@IdRes int id) {
        return root.findViewById(id);
    }

    protected void onCreateView() {
    }

    public abstract int rootLayoutId();
}
