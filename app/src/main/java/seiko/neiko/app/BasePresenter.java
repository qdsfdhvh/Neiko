package seiko.neiko.app;

import com.google.gson.JsonObject;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by Seiko on 2017/2/25. Y
 */

public class BasePresenter<T> {

    protected T mBaseView;
    protected CompositeDisposable compositeDisposable;

    public void setItemCallBack(T baseView) {
        mBaseView = baseView;
        compositeDisposable = new CompositeDisposable();
    }

//    protected void addDisposable(int type, final Consumer<RxEvent> action) {
//        compositeDisposable.add(RxBus.getDefault().toObservable(type).subscribe(action));
//    }

    protected void addDisposable(Disposable disposable) {compositeDisposable.add(disposable);}

    public void detachView() {
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
        mBaseView = null;
    }

    //=========================================
    protected String getString(JsonObject data, String key) {
        return data.get(key) != null ? data.get(key).getAsString():"";
    }

    protected int getInt(JsonObject data, String key) {
        return data.get(key) != null ? data.get(key).getAsInt():0;
    }
}
