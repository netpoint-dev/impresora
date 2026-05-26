package com.common.demo.touch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.HashSet;
import java.util.function.Consumer;

/**
 * @author zhangjf
 * @date 2022/9/14
 */
public class TouchView extends View {

    private final Paint mLinePaint;                             // 线条画笔
    private final Paint mWithSpacePaint;                        // 空白区域画笔
    private final Paint mActivePaint;                           // 激活方格画笔
    private final Paint mTouchTrackPaint;                       // 触摸轨迹画笔
    private final Path mPath;                                   // 路径
    private float mPreX, mPreY;                                 // 触摸点坐标
    // 行列数要符合2n+1，即是奇数
    private static final int ROWS_NUM = 15;                     // 行数
    private static final int COLS_NUM = 15;                     // 列数
    private static final int LINE_WIDTH = 5;                    // 线条宽度
    @ColorInt
    private static final int INACTIVE_COLOR = Color.RED;        // 非激活颜色
    @ColorInt
    private static final int ACTIVE_COLOR = Color.BLUE;         // 激活颜色
    @ColorInt
    private static final int LINE_COLOR = Color.BLACK;          // 线条颜色
    @ColorInt
    private static final int TOUCH_TRACK_COLOR = Color.GREEN;   // 触摸移动轨迹
    @ColorInt
    private static final int WHITE_SPACE_COLOR = Color.WHITE;   // 空白区域填充颜色

    private boolean hasInitArea = false;                        // 是否已经初始化区域
    private final HashSet<Rect> mTouchArea = new HashSet<>();   // 可触摸区域
    private final HashSet<Rect> mActiveArea = new HashSet<>();  // 已激活区域
    /**
     * 可激活区域的总数。三列三行重叠9
     */
    private final static int TOUCH_AREA_NUM = 3 * ROWS_NUM + 3 * COLS_NUM - 9;
    private onActiveAreaListener mListener;                     // 回调监听

    public TouchView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        // 初始化线条画笔
        mLinePaint = new Paint();
        mLinePaint.setColor(LINE_COLOR);
        mLinePaint.setStrokeWidth(LINE_WIDTH);

        // 初始化空白区域画笔
        mWithSpacePaint = new Paint();
        mWithSpacePaint.setColor(WHITE_SPACE_COLOR);
        mWithSpacePaint.setStrokeWidth(0);

        // 初始化激活区域画笔
        mActivePaint = new Paint();
        mActivePaint.setColor(ACTIVE_COLOR);

        // 初始化触摸轨迹画笔
        mTouchTrackPaint = new Paint();
        mTouchTrackPaint.setColor(TOUCH_TRACK_COLOR);
        mTouchTrackPaint.setStrokeWidth(LINE_WIDTH / 2f);
        mTouchTrackPaint.setStyle(Paint.Style.STROKE);

        // 初始化路径
        mPath = new Path();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w != oldw && h != oldh) {
            hasInitArea = false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);

        if (!hasInitArea) {
            initArea();
            hasInitArea = true;
        }

        final float width = getWidth();
        final float height = getHeight();
        final int nW = (COLS_NUM - 1) / 2;
        final int nH = (ROWS_NUM - 1) / 2;
        final int halfLineWidth = LINE_WIDTH / 2;

        // ************ 全局填充背景颜色
        canvas.drawColor(INACTIVE_COLOR);

        // ************ 绘制已激活区域
        mActiveArea.forEach(new Consumer<Rect>() {
            @Override
            public void accept(Rect area) {
                canvas.drawRect(area, mActivePaint);
            }
        });

        // ************ 画线
        // 先画横线
        for (int n = 1; n < ROWS_NUM; ++n) {
            float tmpH = n * height / ROWS_NUM;
            canvas.drawLine(0, tmpH, width, tmpH, mLinePaint);
        }
        // 再画竖线
        for (int n = 1; n < COLS_NUM; ++n) {
            float tmpW = n * width / COLS_NUM;
            canvas.drawLine(tmpW, 0, tmpW, height, mLinePaint);
        }

        // ************ 填充非触摸区域
        // 第一个矩形
        canvas.drawRect(width / COLS_NUM + halfLineWidth, height / ROWS_NUM + halfLineWidth,
                nW * width / COLS_NUM - halfLineWidth, nH * height / ROWS_NUM - halfLineWidth, mWithSpacePaint);
        // 第二个矩形
        canvas.drawRect((nW + 1) * width / COLS_NUM + halfLineWidth, height / ROWS_NUM + halfLineWidth,
                (COLS_NUM - 1) * width / COLS_NUM - halfLineWidth, nH * height / ROWS_NUM - halfLineWidth, mWithSpacePaint);
        // 第三个矩形
        canvas.drawRect(width / COLS_NUM + halfLineWidth, (nH + 1) * height / ROWS_NUM + halfLineWidth,
                nW * width / COLS_NUM - halfLineWidth, (ROWS_NUM - 1) * height / ROWS_NUM - halfLineWidth, mWithSpacePaint);
        // 第四个矩形
        canvas.drawRect((nW + 1) * width / COLS_NUM + halfLineWidth, (nH + 1) * height / ROWS_NUM + halfLineWidth,
                (COLS_NUM - 1) * width / COLS_NUM - halfLineWidth, (ROWS_NUM - 1) * height / ROWS_NUM - halfLineWidth, mWithSpacePaint);

        // ************ 绘制路径
        canvas.drawPath(mPath, mTouchTrackPaint);


    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                mPath.moveTo(event.getX(), event.getY());
                mPreX = event.getX();
                mPreY = event.getY();
                checkArea((int) mPreX, (int) mPreY);
                return true;
            }
            case MotionEvent.ACTION_MOVE:
                float endX = (mPreX + event.getX()) / 2;
                float endY = (mPreY + event.getY()) / 2;
                mPath.quadTo(mPreX, mPreY, endX, endY);
                mPreX = event.getX();
                mPreY = event.getY();
                checkArea((int) mPreX, (int) mPreY);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                return performClick();
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    private void initArea() {

        // 初始化可触摸区域
        mTouchArea.clear();
        final int height = getHeight();
        final int width = getWidth();
        final int nW = (COLS_NUM - 1) / 2;
        final int nH = (ROWS_NUM - 1) / 2;

        // 按行添加区域
        for (int n = 0; n < COLS_NUM; ++n) {
            mTouchArea.add(new Rect(n * width / COLS_NUM, 0, (n + 1) * width / COLS_NUM, height / ROWS_NUM));
            mTouchArea.add(new Rect(n * width / COLS_NUM, nH * height / ROWS_NUM, (n + 1) * width / COLS_NUM, (nH + 1) * height / ROWS_NUM));
            mTouchArea.add(new Rect(n * width / COLS_NUM, (ROWS_NUM - 1) * height / ROWS_NUM, (n + 1) * width / COLS_NUM, height));
        }

        // 按列添加区域
        for (int n = 0; n < ROWS_NUM; ++n) {
            mTouchArea.add(new Rect(0, n * height / ROWS_NUM, width / COLS_NUM, (n + 1) * height / ROWS_NUM));
            mTouchArea.add(new Rect(nW * width / COLS_NUM, n * height / ROWS_NUM, (nW + 1) * width / COLS_NUM, (n + 1) * height / ROWS_NUM));
            mTouchArea.add(new Rect((COLS_NUM - 1) * width / COLS_NUM, n * height / ROWS_NUM, width, (n + 1) * height / ROWS_NUM));
        }

        // 初始化已激活区域
        mActiveArea.clear();
    }

    private void checkArea(int w, int h) {
        for (Rect rect : mTouchArea) {
            if (rect.contains(w, h)) {
                mActiveArea.add(rect);
                break;
            }
        }
        if (mListener != null && mActiveArea.size() == TOUCH_AREA_NUM) {
            mListener.onActiveFinish();
        }
    }

    public void setHasInitArea(boolean hasInitArea) {
        this.hasInitArea = hasInitArea;
    }

    public void setOnActiveAreaListener(onActiveAreaListener listener) {
        this.mListener = listener;
    }

    public interface onActiveAreaListener {
        void onActiveFinish();
    }
}
