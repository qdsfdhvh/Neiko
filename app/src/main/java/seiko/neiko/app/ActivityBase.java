package seiko.neiko.app;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.List;

import butterknife.ButterKnife;
//import rx.functions.Action1;
//import io.reactivex.functions.Action;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
//import rx.subscriptions.CompositeSubscription;
import io.reactivex.disposables.CompositeDisposable;
import seiko.neiko.R;
import seiko.neiko.dao.Permission;
import seiko.neiko.dao.engine.DdSource;
import seiko.neiko.rx.RxBus;
import seiko.neiko.rx.RxEvent;
import seiko.neiko.view.BaseView;

/**
 * Created by Seiko on 2016/10/1.
 *
 * 当前类注释:基类Actvity 主要封装一些工具类的使用,公共方法,配置
 */

public abstract class ActivityBase extends FragmentActivity implements BaseView {
    /** 贯穿全部 */
    protected static DdSource source;
    private CompositeDisposable compositeDisposable;

    @Override
    protected void onStart() {
        super.onStart();
        Permission.get(this); //6.0以上的请求权限
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activity_ani_enter, 0);
        compositeDisposable = new CompositeDisposable();
        setContentView(getLayoutId());
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        transparent();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.activity_ani_exist);
    }


    //====================================================================
    /** 沉浸式状态栏 */
    protected void transparent() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
    }


    //====================================================================
    /** 获取当前view的LayoutInflater实例 */
    protected View inflate(int layoutID, int viewID) {
        return inflate(layoutID,(ViewGroup) findViewById(viewID));
    }

    protected View inflate(int layoutID, ViewGroup view) {
        return LayoutInflater.from(this).inflate(layoutID, view, false);
    }

    /** 根据传入的类(class)打开指定的activity */
    protected void openActivity(Class<?> pClass) {
        Intent _Intent = new Intent();
        _Intent.setClass(this, pClass);
        startActivity(_Intent);
    }

    //====================================================================
    /** 更换fragment */
    protected void shiftView(final Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame_layout, fragment);
        ft.commit();
    }

    public void shiftView2(final android.app.Fragment fragment) {
        android.app.FragmentManager fm = getFragmentManager();
        android.app.FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame_layout, fragment);
        ft.commit();
    }

    //====================================================================
    /** 显示或隐藏的相关处理 */
    protected boolean isVisible(View view) {return view.getVisibility() == View.VISIBLE;}

    protected void gone(final View... views) {
        if (views!=null && views.length>0) {
            for (View view:views) {
                if (view != null) {
                    view.setVisibility(View.GONE);
                }
            }
        }
    }

    protected void visible(final View... views) {
        if (views!=null && views.length>0) {
            for (View view:views) {
                if (view != null) {
                    view.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    protected void changeVisible(View view) {
        if (isVisible(view)) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
        }
    }

    protected void changeOri() {
        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
        else
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    //====================================================================
    /** 全屏 */
    public void setImmersiveFullscreen(boolean isFullscreen) {
        Window window = getWindow();

        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        int uiOpts;

        if (isFullscreen) {
            uiOpts = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        } else {
            uiOpts = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        }

        window.getDecorView().setSystemUiVisibility(uiOpts);
    }

    //====================================================================
    /** 通知相关 */
    protected void toast(String msg) {
        final Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        toast.show();
        new Handler().postDelayed(() -> toast.cancel(), 1000);
    }


    //====================================================================
    /** RxJava相关 */
    protected void addSubscription(int type, Consumer<RxEvent> action) {
        compositeDisposable.add(RxBus.getDefault().toObservable(type).subscribe(action));
    }

    protected void addSubscription(Disposable disposable) {
        compositeDisposable.add(disposable);
    }

    protected void detachView() {
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
    }

    @Override
    protected void onDestroy() {
        detachView();
        super.onDestroy();
    }

    //====================================================================
    /** 其他 */
    protected boolean isTrue(List<?>... lists) {
        if (lists != null && lists.length > 0) {
            for (List<?> list:lists) {
                if (list == null || list.size()==0)
                    return false;
            }
            return true;
        }
        return false;
    }
}
