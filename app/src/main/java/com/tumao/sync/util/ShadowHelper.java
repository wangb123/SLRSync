package com.tumao.sync.util;

import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 王冰
 * @date 2018/3/10
 */
public class ShadowHelper {
    //绘制矩形的区域至少要距离view边框Ratio倍blur,否定导致裁剪掉
    private static final float RATIO = 1.3f;

    private static Path sPath = new Path();
    private static Paint sPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static RectF sRectF = new RectF();


    public static void draw(Canvas canvas, View view, Config config) {
        Check.ifNull(canvas);
        Check.ifNull(view);
        Check.ifNull(config);

        View parent = (View) view.getParent();
        if (parent.getLayerType() != View.LAYER_TYPE_SOFTWARE) {
            parent.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            return;
        }

        int count = canvas.save();
        int viewHeight = view.getHeight();
        int viewWidth = view.getWidth();
        float padding = config.radius * RATIO;
        int xOffset = config.xOffset;
        int yOffset = config.yOffset;

        initPath(viewWidth, viewHeight, config);

        try {
            /*
                clipPath时部分4.0 手机会抛出如下异常, 比如
                OPPO X907 cpu高通骁龙Snapdragon MSM8260 内存1GB 系统版本4.0.3 分辨率800x480
                FATAL EXCEPTION:main
                java.lang.UnsupportedOperationException
                at android.view.GLES20Canvas.clipPath(GLES20Canvas.java:429)
            */
            canvas.clipPath(sPath, Region.Op.REPLACE);
        } catch (Exception e) {
            Log.e("shadow", "不支持clipPath");
            e.printStackTrace();
            canvas.restoreToCount(count);
            config.recycle();
            return;

        }

        sRectF.left = xOffset - padding;
        sRectF.top = yOffset - padding;
        sRectF.right = viewWidth + xOffset + padding;
        sRectF.bottom = viewHeight + yOffset + padding;

        canvas.clipRect(sRectF, Region.Op.REVERSE_DIFFERENCE);

        canvas.translate(xOffset, yOffset);

        sPaint.setColor(config.color);
        sPaint.setMaskFilter(new BlurMaskFilter(config.radius, BlurMaskFilter.Blur.NORMAL));

        canvas.drawPath(sPath, sPaint);

        canvas.restoreToCount(count);
        config.recycle();
    }


    /**
     * 圆角path
     */
    private static void initPath(int w, int h, Config config) {

        float lt = config.leftTopCorner;
        float rt = config.rightTopCorner;
        float rb = config.rightBottomCorner;
        float lb = config.leftBottomCorner;
        if (lt + rt > w) {
            float scale = w / (lt + rt);
            lt *= scale;
            rt *= scale;
            rb *= scale;
            lb *= scale;
        }
        if (rt + rb > h) {
            float scale = h / (rt + rb);
            lt *= scale;
            rt *= scale;
            rb *= scale;
            lb *= scale;
        }

        if (rb + lb > h) {
            float scale = w / (rb + lb);
            lt *= scale;
            rt *= scale;
            rb *= scale;
            lb *= scale;
        }

        if (lt + lb > h) {
            float scale = w / (lt + lb);
            lt *= scale;
            rt *= scale;
            rb *= scale;
            lb *= scale;
        }

        sPath.reset();

        sRectF.left = 0;
        sRectF.top = 0;
        sRectF.right = lt * 2;
        sRectF.bottom = lt * 2;
        sPath.arcTo(sRectF, 180, 90, false);

        sRectF.left = w - rt * 2;
        sRectF.top = 0;
        sRectF.right = w;
        sRectF.bottom = rt * 2;
        sPath.arcTo(sRectF, -90, 90, false);

        sRectF.left = w - rb * 2;
        sRectF.top = h - rb * 2;
        sRectF.right = w;
        sRectF.bottom = h;
        sPath.arcTo(sRectF, 0, 90, false);

        sRectF.left = 0;
        sRectF.top = h - lb * 2;
        sRectF.right = lb * 2;
        sRectF.bottom = h;
        sPath.arcTo(sRectF, 90, 90, false);

        sPath.close();

    }


    public static class Config {
        private static List<Config> sConfigs = new ArrayList<>();

        private Config() {
        }

        public static Config obtain() {
            if (sConfigs.isEmpty()) {
                return new Config();
            }
            return sConfigs.remove(sConfigs.size() - 1);
        }

        public static Config obtain(Config config) {
            Config b = obtain();
            b.color = config.color;
            b.xOffset = config.xOffset;
            b.yOffset = config.yOffset;
            b.radius = config.radius;
            b.leftTopCorner = config.leftTopCorner;
            b.rightTopCorner = config.rightTopCorner;
            b.rightBottomCorner = config.rightBottomCorner;
            b.leftBottomCorner = config.leftBottomCorner;
            return b;
        }

        int color;
        int xOffset;
        int yOffset;
        float radius;
        int leftTopCorner;
        int rightTopCorner;
        int rightBottomCorner;
        int leftBottomCorner;

        /**
         * @param color 阴影颜色
         * @return this
         */
        public Config color(int color) {
            this.color = color;
            return this;
        }

        /**
         * @param xOffset x轴偏移量
         * @return this
         */
        public Config xOffset(int xOffset) {
            this.xOffset = xOffset;
            return this;
        }

        /**
         * @param yOffset y轴偏移量
         * @return this
         */
        public Config yOffset(int yOffset) {
            this.yOffset = yOffset;
            return this;
        }

        /**
         * @param radius 模糊半径
         * @return this
         */
        public Config radius(float radius) {
            if (radius < 1e-6) {
                radius = 0.01f;
            }
            this.radius = radius;
            return this;
        }

        public Config leftTopCorner(int leftTopCorner) {
            this.leftTopCorner = leftTopCorner;
            return this;
        }

        public Config rightTopCorner(int rightTopCorner) {
            this.rightTopCorner = rightTopCorner;
            return this;
        }

        public Config rightBottomCorner(int rightBottomCorner) {
            this.rightBottomCorner = rightBottomCorner;
            return this;
        }

        public Config leftBottomCorner(int leftBottomCorner) {
            this.leftBottomCorner = leftBottomCorner;
            return this;
        }

        void recycle() {
            if (sConfigs.contains(this)) {
                throw new RuntimeException("build has been recycled!");
            }
            color = 0;
            xOffset = 0;
            yOffset = 0;
            radius = 0;
            leftTopCorner = 0;
            rightTopCorner = 0;
            rightBottomCorner = 0;
            leftBottomCorner = 0;
            if (sConfigs.size() < 50) {
                sConfigs.add(this);
            }
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Config)) {
                return false;
            }
            Config b = ((Config) obj);
            if (obj == this ||
                    b.color == color &&
                            b.xOffset == xOffset &&
                            b.yOffset == yOffset &&
                            b.radius == radius &&
                            b.leftTopCorner == leftTopCorner &&
                            b.rightTopCorner == rightTopCorner &&
                            b.rightBottomCorner == rightBottomCorner &&
                            b.leftBottomCorner == leftBottomCorner
                    ) {
                return true;
            }

            return false;
        }
    }

    public static class Check {
        static void ifNull(Object o) {
            if (o == null) {
                throw new RuntimeException("can not be null !");
            }
        }
    }
}
