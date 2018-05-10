package com.tumao.sync.view;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.tumao.sync.util.DimenUtil;
import com.tumao.sync.util.ShadowHelper;


/**
 * @author 王冰
 * @date 2018/3/10
 */
public class ShadowView extends View {
    public ShadowView(@NonNull Context context) {
        super(context);
    }

    public ShadowView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ShadowView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int radius = (int) DimenUtil.dpToPx(getContext(), 5f);
        int corner = radius / 2;

        ShadowHelper.draw(canvas, this,
                ShadowHelper.Config.obtain()
                        .color(0xFFF2F2F2)
                        .leftTopCorner(corner)
                        .rightTopCorner(corner)
                        .leftBottomCorner(corner)
                        .rightBottomCorner(corner)
                        .radius(radius)
        );
    }
}
