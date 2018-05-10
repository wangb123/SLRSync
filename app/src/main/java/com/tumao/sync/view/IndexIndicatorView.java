package com.tumao.sync.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Checkable;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 10213
 * @date 2017/12/26
 */

public class IndexIndicatorView extends LinearLayout implements Checkable, View.OnClickListener {
    private static final int DEFAULT_ROTATE_ANI_TIME = 200;
    private boolean checked;
    private RotateAnimation mFlipAnimation;
    private RotateAnimation mReverseFlipAnimation;
    private int mRotateAniTime = DEFAULT_ROTATE_ANI_TIME;

    {
        mFlipAnimation = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF,
                0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mFlipAnimation.setInterpolator(new LinearInterpolator());
        mFlipAnimation.setDuration(mRotateAniTime);
        mFlipAnimation.setFillAfter(true);

        mReverseFlipAnimation = new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF,
                0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mReverseFlipAnimation.setInterpolator(new LinearInterpolator());
        mReverseFlipAnimation.setDuration(mRotateAniTime);
        mReverseFlipAnimation.setFillAfter(true);
    }

    private List<OnCheckedChangeListener> listeners = new ArrayList<>();

    public IndexIndicatorView(Context context) {
        super(context);
        init();
    }


    public IndexIndicatorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IndexIndicatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOnClickListener(this);
    }

    @Override
    public void setChecked(boolean checked) {
        if (checked != this.checked) {
            this.checked = checked;
            getChildAt(1).clearAnimation();
            getChildAt(1).startAnimation(this.checked ? mFlipAnimation : mReverseFlipAnimation);

            for (OnCheckedChangeListener listener : listeners) {
                listener.onCheckedChange(this, checked);
            }
        }
    }

    @Override
    public boolean isChecked() {
        return checked;
    }

    @Override
    public void toggle() {
        setChecked(!isChecked());
    }

    @Override
    public void onClick(View v) {
        toggle();
    }

    public void addOnCheckedChangeListener(OnCheckedChangeListener listener) {
        listeners.add(listener);
    }

    /**
     * 状态变化监听
     */
    public interface OnCheckedChangeListener {
        /**
         * 状态变化回调方法
         *
         * @param view      变化的view
         * @param isChecked 是否选中
         */
        void onCheckedChange(IndexIndicatorView view, boolean isChecked);
    }
}
