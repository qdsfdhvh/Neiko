package seiko.neiko.app;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import java.util.List;

import butterknife.ButterKnife;
import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;
import seiko.neiko.dao.FabScroll;
import seiko.neiko.models.Book;
import seiko.neiko.rx.RxBus;
import seiko.neiko.rx.RxEvent;
import seiko.neiko.view.FragmentView;
import seiko.neiko.widget.fab.FloatingActionButton;
import seiko.neiko.widget.fab.FloatingActionMenu;

/**
 * Created by Seiko on 2016/11/15. YiKu
 */

public abstract class FragmentBase extends Fragment implements FragmentView {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        ButterKnife.bind(this, view);
        Subscription();
        initView();
        return view;
    }

    @Override
    public void onDestroy() {
        detachView();
        super.onDestroy();
    }

    //==================================================
    /** Rx订阅 */
    private CompositeSubscription mCompositeSubscription;

    private void Subscription() {mCompositeSubscription = new CompositeSubscription();}

    protected void addSubscription(int type, Action1<RxEvent> action) {
        mCompositeSubscription.add(RxBus.getDefault().toObservable(type).subscribe(action));
    }

    protected void addSubscription(Subscription subscription) {
        mCompositeSubscription.add(subscription);
    }

    protected void detachView() {
        if (mCompositeSubscription != null) {
            mCompositeSubscription.unsubscribe();
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
        new Handler().postDelayed(() -> toast.cancel(), 500);
    }

}
