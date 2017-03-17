package seiko.neiko.ui.search;

import android.os.Bundle;

import android.os.Handler;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import seiko.neiko.R;
import seiko.neiko.dao.engine.DdSource;
import seiko.neiko.dao.SourceApi;
import seiko.neiko.models.Book;
import seiko.neiko.viewModels.SearchViewModel;
import seiko.neiko.app.SwipeLayoutBase;
import zlc.season.practicalrecyclerview.PracticalRecyclerView;

import static seiko.neiko.dao.mNum.SEARCH_NUMBER;
import static seiko.neiko.dao.mPath.sitedPath;

public class SearchActivity extends SwipeLayoutBase implements FloatingSearchView.OnSearchListener {

    public static String key;
    public static boolean allSource;

    //界面
    @BindView(R.id.rv_result)
    PracticalRecyclerView recView;
    @BindView(R.id.floating_search_view)
    FloatingSearchView mSearchBar;

    private SearchAdapter adapter;

    @Override
    public int getLayoutId() {return R.layout.activity_search;}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRecView();
        mSearchBar.setOnSearchListener(this);
        if (!TextUtils.isEmpty(key)) {
            DoLoadViewModel(source, key);
            mSearchBar.setSearchText(key);
        }
    }

    private void setRecView() {
        adapter = new SearchAdapter();
        recView.setLayoutManager(new StaggeredGridLayoutManager(SEARCH_NUMBER, OrientationHelper.VERTICAL));
        recView.setHasFixedSize(true);
        if (allSource) {
            recView.setAdapterWithLoading(adapter);
            adapter.addAll(new ArrayList<>());
        } else {
            recView.setAdapterWithLoading(adapter);
        }
    }


    //=================================
    /** 是否为多插件搜索 */
    private void isallSource(String key, boolean allSource) {
        adapter.clear();
        adapter.notifyDataSetChanged();
        adapter.showLoading();
        if (allSource) {
            addSubscription(disposable(key));
        } else {
            DoLoadViewModel(source, key);
        }
    }

    //=================================
    /** 创建多任务subscription */
    private Disposable disposable(String key) {
        return Flowable.create((FlowableEmitter<String> e) -> {
                        File file = new File(sitedPath);
                        for (File file1 : file.listFiles()) {
                            String name = file1.getName().replace(".sited", "");
                            e.onNext(name);
                        }
                        e.onComplete();
                }, BackpressureStrategy.ERROR)
                .delay(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((String name) -> {
                    DdSource source = SourceApi.getDefault().getByTitle(name);
                    if (source != null) {
                        DoLoadViewModel(source, key);
                    }
                });
    }

    //=================================
    /** xx插件进行搜索 */
    private void DoLoadViewModel(DdSource source, String key) {
        SearchViewModel viewModel = new SearchViewModel();
        viewModel.clear();
        source.getNodeViewModel(viewModel, false, key, 1, source.search, (code) -> {
            if (code == 1) {
                adapter.addAll(viewModel.list);
            }
        });
    }

    //=================================
    /** 搜索 */
    @Override
    public void onSuggestionClicked(SearchSuggestion searchSuggestion) {}

    @Override
    public void onSearchAction(String key) {isallSource(key, allSource);}
}
