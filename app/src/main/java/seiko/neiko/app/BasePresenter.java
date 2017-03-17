package seiko.neiko.app;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import seiko.neiko.rx.RxBus;
import seiko.neiko.rx.RxEvent;

/**
 * Created by Seiko on 2017/2/25. Y
 */

public class BasePresenter<T> {

    protected T mBaseView;
    protected CompositeDisposable compositeDisposable;

    public void attachView(T baseView) {
        mBaseView = baseView;
        compositeDisposable = new CompositeDisposable();
    }

//    protected void addDisposable(int type, Consumer<RxEvent> action) {
//        compositeDisposable.add(RxBus.getDefault().toObservable(type).subscribe(action));
//    }

    protected void addDisposable(Disposable disposable) {compositeDisposable.add(disposable);}

    public void detachView() {
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
        mBaseView = null;
    }
}
