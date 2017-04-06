package seiko.neiko.ui.search;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.noear.sited.ISdViewModel;
import org.noear.sited.SdNode;
import org.noear.sited.SdSourceCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import seiko.neiko.app.BasePresenter;
import seiko.neiko.dao.SourceApi;
import seiko.neiko.dao.engine.DdNode;
import seiko.neiko.dao.engine.DdSource;
import seiko.neiko.ui.book.BookModel;
import seiko.neiko.view.SearchItemView;

import static seiko.neiko.dao.mPath.sitedPath;

/**
 * Created by Seiko on 2017/4/5. Y
 */

class SearchPresenter extends BasePresenter<SearchItemView> implements ISdViewModel {

    private DdSource source;
    private List<BookModel> list;

    SearchPresenter(DdSource source) {
        this.source = source;
        list = new ArrayList<>();
    }

    void loadData(final String key, final boolean allSource) {
        if (allSource) {
            addDisposable(disposable(key));
        } else {
            DoLoadViewModel(source, key);
        }
    }

    //=================================
    /** 创建多任务subscription */
    private Disposable disposable(final String key) {
        return Flowable.create(new FlowableOnSubscribe<String>() {
                    @Override
                    public void subscribe(FlowableEmitter<String> e) throws Exception {
                        File file = new File(sitedPath);
                        for (File file1 : file.listFiles()) {
                            String name = file1.getName().replace(".sited", "");
                            e.onNext(name);
                        }
                        e.onComplete();
                    }
                }, BackpressureStrategy.ERROR)
                .delay(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String name) throws Exception {
                        DdSource sd = SourceApi.getDefault().getByTitle(name);
                        if (sd != null) {
                            DoLoadViewModel(sd, key);
                        }
                    }
                });
    }

    //=================================
    /** xx插件进行搜索 */
    private void DoLoadViewModel(final DdSource source, final String key) {
        list.clear();
        source.getNodeViewModel(this, false, key, 1, source.search, new SdSourceCallback() {
            @Override
            public void run(Integer code) {
                if (code == 1) {
                    mBaseView.onSuccess(list);
                }
            }
        });
    }

    @Override
    public void loadByConfig(SdNode config) {

    }

    @Override
    public void loadByJson(SdNode c, String... jsons) {
        if (jsons == null || jsons.length == 0)
            return;
        DdNode config = (DdNode) c;
        int dtype = config.s().main.dtype();
        String from = config.source.title;

        for (String json : jsons) { //支持多个数据块加载
            loadByJsonData(json, dtype, from);
        }
    }

    private void loadByJsonData(String json, int dtype, String from) {
        JsonArray data = new JsonParser().parse(json).getAsJsonArray();

        if (data.isJsonArray()) {
            for (JsonElement el:data) {
                JsonObject n = el.getAsJsonObject();
                String name = getString(n, "name");
                String url = getString(n,"url");
                String logo = getString(n, "logo");
                String author = getString(n, "author");

                BookModel book = new BookModel(dtype, name, url, logo, author);
                book.setSource(from);
                list.add(book);
            }
        }
    }
}
