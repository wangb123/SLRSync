package com.tumao.sync.ui.unknown;

import android.os.Bundle;

import com.tumao.sync.R;
import com.tumao.sync.ui.base.BaseFragment;

/**
 * @author 王冰
 * @date 2018/5/8
 */
public class UnknownFragment extends BaseFragment {
    public static UnknownFragment newInstance() {

        Bundle args = new Bundle();

        UnknownFragment fragment = new UnknownFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int rootLayoutId() {
        return R.layout.fragment_unknown;
    }
}
