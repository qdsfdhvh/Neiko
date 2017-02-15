package seiko.neiko.app;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;

import seiko.neiko.R;
import seiko.neiko.widget.SwipeBackLayout;

/**
 * Created by Seiko on 2017/1/8. Y
 * 有滑返回的layout
 */

public abstract class SwipeLayoutBase extends ActivityBase {
    public SwipeBackLayout swipeBackLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(0));
//        getWindow().getDecorView().setBackgroundDrawable(null);
        swipeBackLayout = (SwipeBackLayout) LayoutInflater.from(this).inflate(R.layout.base, null);
        swipeBackLayout.attachToActivity(this);
    }

}
