package seiko.neiko.app;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import java.util.List;

import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import seiko.neiko.models.Book;
import seiko.neiko.rx.RxBus;
import seiko.neiko.rx.RxEvent;

/**
 * Created by Seiko on 2016/11/15. YiKu
 */

public abstract class BaseFragment extends Fragment implements BaseView {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        ButterKnife.bind(this, view);
        Subscription();
        initViews(savedInstanceState);
        return view;
    }

    @Override
    public void onDestroy() {
        detachView();
        super.onDestroy();
    }

    //==================================================
    /** Rx订阅 */

    private CompositeDisposable compositeDisposable;

    private void Subscription() {compositeDisposable = new CompositeDisposable();}

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


    //==================================================
    /** 某个数值在list中的位置 */
    protected static int contain(List<Book> books, String bKey) {
        int i  = 0;
        for (Book book:books) {
            if (bKey.equals(book.getBkey())) {
                return i;
            }
            i++;
        }
        return -1;
    }

    //==================================================
    /** 通知 */
    protected void toast(String msg) {
        final Toast toast= Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT);
        toast.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, 1000);
    }

}
