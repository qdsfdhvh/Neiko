package seiko.neiko.widget.refresh;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by yuety on 16/7/8. Y
 */

public class ZoomableRecyclerView extends RecyclerView {

    private Context mContext;
    /** 缩放因子*/
    private float mScale = 1.0f;
    /** canvas的偏移量 */
    private float mPosX = 0.0f;
    private float mPosY = 0.0f;
    /** 上次触摸点坐标 */
    PointF start = new PointF();
    /** 缩放中心 */
    PointF mid = new PointF();
    /** 双击自动缩放 */
    private int mAutoTime = 6;
    private float mAutoBigger = 1.03f;
    private float mAutoSmall  = 0.97f;

    private final int NONE = 0;
    private int mode = NONE;
    final int DRAG = 1;
    final int ZOOM = 2;
    float oldDist;

    private GestureDetector gestureScanner;

    public ZoomableRecyclerView(Context context) {this(context, null);}

    public ZoomableRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomableRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setLayoutManager(new SupportLinearLayoutManager(context, VERTICAL, false));
        initDetector();
        mContext = context;
    }

    /** 控制缩放后上下滑动的速度 */
    private class SupportLinearLayoutManager extends LinearLayoutManager {

        SupportLinearLayoutManager(Context context,int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        @Override
        public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
            float result = dy / (mScale * mScale);
            return super.scrollVerticallyBy((int) (result), recycler, state);
        }
    }

    /** 双击放大 */
    private void initDetector() {
        gestureScanner = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (mScale == 1.0f) {
                    mid.set(e.getRawX(), e.getRawY());
                }

                if (mScale < 1.25f) {
                    postDelayed(new AutoScaleRunnable(1.8f, mAutoBigger), mAutoTime);
                } else {
                    postDelayed(new AutoScaleRunnable(1.0f, mAutoSmall), mAutoTime);
                }
                return true;
            }
        });
    }

    /** 自动缩放的核心类 */
    private class AutoScaleRunnable implements Runnable {
        //目标Scale
        private float mTargetScale;
        //Scale变化梯度
        private float mGrad;

        private AutoScaleRunnable(float TargetScale, float grad) {
            mTargetScale = TargetScale;
            mGrad = grad;
        }

        @Override
        public void run() {

            if ((mGrad > 1.0f && mScale < mTargetScale)
                    || (mGrad < 1.0f && mScale > mTargetScale)) {
                mScale *= mGrad;
                postDelayed(this, mAutoTime);
            } else {
                mScale = mTargetScale;
            }

            checkBorder();
            invalidate();
        }
    }


    /** 缩放绘制 */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save(Canvas.MATRIX_SAVE_FLAG);

        if (mScale == 1.0f) {
            mPosX = 0.0f;
            mPosY = 0.0f;
        }

        Matrix matrix = new Matrix();

        matrix.preTranslate(mPosX / mScale, mPosY / mScale);
        matrix.postScale(mScale, mScale, mid.x, mid.y);

        canvas.setMatrix(matrix);
        super.dispatchDraw(canvas);
        canvas.restore();
    }


    @Override
    public boolean onTouchEvent(MotionEvent e) {
        super.onTouchEvent(e);

        gestureScanner.onTouchEvent(e);

        /* 拖动事件的处理 */
        int action = e.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_POINTER_UP:
                if (mScale<1.0f)
                    postDelayed(new AutoScaleRunnable(1.0f, 1.01f), mAutoTime);
                if (mScale>2.2f)
                    postDelayed(new AutoScaleRunnable(2.2f, 0.99f), mAutoTime);
                mode = NONE;
                break;
            case MotionEvent.ACTION_DOWN:
                start.set(e.getX(), e.getY());
//                lastmScale = mScale;
                mode = DRAG;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(e);
                if (oldDist<10f) break;
                if (mScale==1.0f)
                    midPoint(mid, e);
                mode = ZOOM;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    //当前坐标
                    final float x2 = e.getX();
                    final float y2 = e.getY();
                    //获得距离
                    float dx = x2 - start.x;
//                    float dy = y2 - start.y;
                    //开始位移
                    mPosX += dx;
//                    mPosY += dy;
                    /* 保存坐标 */
                    start.set(x2, y2);
                    //检查边界
                    checkBorder();
                    invalidate();
                }
                else if (mode == ZOOM) {
                    float newDist = spacing(e);
                    if (newDist<10f) break;

                    float scale = newDist / oldDist;
                    oldDist = newDist;

                    mScale *= Math.sqrt(scale);
                    mScale = Math.max(0.8f, Math.min(mScale, 3.0f));

                    //检查边界
                    checkBorder();
                    invalidate();
                }
                break;
        }
        return true;
    }

    /** 计算距离 */
    private float spacing(MotionEvent ev) {
        float x = ev.getX(0) - ev.getX(1);
        float y = ev.getY(0) - ev.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /** 计算中点 */
    private void midPoint(PointF point, MotionEvent ev) {
        float x = ev.getX(0) + ev.getX(1);
        float y = ev.getY(0) + ev.getY(1);
        point.set(x / 2, y / 2);
    }

    /** 检查边界 */
    private void checkBorder() {
        if (mScale > 1.0f) {
            //左边界
            if (mPosX > 0.0f + mid.x * (mScale - 1.0f))
                mPosX = 0.0f + mid.x * (mScale - 1.0f);
            //右边界
            if (-mPosX > (getWidth() - mid.x) * (mScale - 1.0f))
                mPosX = -(getWidth() - mid.x) * (mScale - 1.0f);
            //上边界
            if (mPosY > 0.0f + mid.y * (mScale - 1.0f))
                mPosY = 0.0f + mid.y * (mScale - 1.0f);
            //下边界
            if (-mPosY > (getHeight() - mid.y) * (mScale - 1.0F))
                mPosY = -(getHeight() - mid.y) * (mScale - 1.0f);
        }
    }
}
