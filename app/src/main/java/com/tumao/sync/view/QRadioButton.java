package com.tumao.sync.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RadioButton;

/**
 * Created by 王冰 on 2017/8/17.
 */

public class QRadioButton extends android.support.v7.widget.AppCompatRadioButton {
    private View.OnClickListener clickListener;

    public QRadioButton(Context context) {
        super(context);
    }

    public QRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public QRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean performClick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            return callOnClick();
        } else {
            clickListener.onClick(this);
            return true;
        }
    }

    @Override
    public void setOnClickListener(@Nullable View.OnClickListener l) {
        super.setOnClickListener(l);
        clickListener = l;
    }
}
