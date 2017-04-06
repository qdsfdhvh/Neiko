package seiko.neiko.ui.sited;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import seiko.neiko.app.BasePresenter;
import seiko.neiko.models.SourceModel;
import seiko.neiko.view.SitedItemView;

import static seiko.neiko.dao.mPath.sitedPath;

/**
 * Created by Seiko on 2017/2/25. Y
 */

class SitedPresenter extends BasePresenter<SitedItemView> {

    SitedPresenter() {compositeDisposable = new CompositeDisposable();}

    void loadData() {
        Disposable disposable = createObservable()
                .subscribeOn(Schedulers.io())
                .delay(800, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<SourceModel>>() {
                    @Override
                    public void accept(List<SourceModel> list) throws Exception {
                        mBaseView.onLoadSuccess(list);
                    }

                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mBaseView.onLoadFailed();
                    }
                });
        addDisposable(disposable);
    }

    private Flowable<List<SourceModel>> createObservable() {
        return Flowable.create(new FlowableOnSubscribe<List<SourceModel>>() {
            @Override
            public void subscribe(FlowableEmitter<List<SourceModel>> e) throws Exception {
                File file = new File(sitedPath);

                if (file.exists()) {
                    List<SourceModel> list = new ArrayList<>();

                    for (File file1:file.listFiles()) {
                        SourceModel m = new SourceModel();
                        m.title = file1.getName();
                        list.add(m);
                    }

                    e.onNext(list);
                    e.onComplete();
                }
            }
        }, BackpressureStrategy.ERROR);
    }
}
