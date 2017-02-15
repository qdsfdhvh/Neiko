package seiko.neiko.dao;

import android.app.Activity;
import android.view.GestureDetector;
import android.view.MotionEvent;

import seiko.neiko.app.App;

/**
 * Created by Seiko on 2017/2/11. Y
 */

public class mTouch extends GestureDetector.SimpleOnGestureListener {

    private int width;
    private int height;
    private int move;

    private TouchListener touchListener;

    public mTouch(TouchListener touchListener) {
        this.touchListener = touchListener;
        getMetrices();
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        final float x1 = e.getRawX();
        final float y1 = e.getRawY();
        /* 转屏后重新获得尺寸 */
        if (width != App.getDisplayMetrics().widthPixels) {
            getMetrices();
        }

        if (x1>width/3 && x1<width/3*2 && y1<height/3*2 && y1>height/3) {
            touchListener.changeVisible();
        }

        if (!touchListener.control()) {
            if (x1<width/3) touchListener.setScroll(move, -1);
            else if(x1>width/3*2) touchListener.setScroll(move, 1);
            else if(x1>width/3 && x1<width/3*2 && y1<height/3) touchListener.setScroll(move, -1);
            else if(x1>width/3 && x1<width/3*2 && y1>height/3*2) touchListener.setScroll(move, 1);
        }

        return super.onSingleTapConfirmed(e);
    }

    private void getMetrices() {
        width  = App.getDisplayMetrics().widthPixels;
        height = App.getDisplayMetrics().heightPixels;
        move = (int) (height * 0.7);
    }


    public interface TouchListener {

        void changeVisible();

        boolean control();

        void setScroll(int move, int t);

    }
}
