package com.tumao.sync.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.tumao.sync.util.StatusBarUtils;

/**
 * Created by 10213 on 2017/11/20.
 */

public class TopLayout extends FrameLayout {
    public TopLayout(@NonNull Context context) {
        super(context);
        init();
    }

    public TopLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TopLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setPadding(getPaddingLeft(), getPaddingTop() + StatusBarUtils.getStatusBarHeight(getContext()), getPaddingRight(), getPaddingBottom());
        }
    }

}
