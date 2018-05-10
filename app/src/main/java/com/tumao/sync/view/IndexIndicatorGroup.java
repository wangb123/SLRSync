package com.tumao.sync.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * @author 10213
 * @date 2017/12/26
 */

public class IndexIndicatorGroup extends LinearLayout {
    private IndexIndicatorView checkChild;
    private IndexIndicatorView.OnCheckedChangeListener onCheckedChangeListener = new IndexIndicatorView.OnCheckedChangeListener() {
        @Override
        public void onCheckedChange(IndexIndicatorView view, boolean isChecked) {
            if (isChecked) {
                checkChild = view;
                for (int i = 0; i < getChildCount(); i++) {
                    View child = getChildAt(i);
                    if (child != view && child instanceof IndexIndicatorView) {
                        ((IndexIndicatorView) child).setChecked(false);
                    }
                }
            } else {
                if (checkChild == view) {
                    checkChild = null;
                }
            }
        }
    };

    public IndexIndicatorGroup(Context context) {
        super(context);
    }

    public IndexIndicatorGroup(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public IndexIndicatorGroup(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);

        if (child instanceof IndexIndicatorView) {
            ((IndexIndicatorView) child).addOnCheckedChangeListener(onCheckedChangeListener);
        }
    }

    public IndexIndicatorView getCheckChild() {
        return checkChild;
    }
}
