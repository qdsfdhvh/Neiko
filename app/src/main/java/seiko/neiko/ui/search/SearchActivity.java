package seiko.neiko.ui.search;

import android.os.Bundle;

import android.os.Handler;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import seiko.neiko.R;
import seiko.neiko.dao.engine.DdSource;
import seiko.neiko.dao.SourceApi;
import seiko.neiko.dao.db.SiteDbApi;
import seiko.neiko.dao.mPath;
import seiko.neiko.models.SourceModel;
import seiko.neiko.utils.FileUtil;
import seiko.neiko.viewModels.SearchViewModel;
import seiko.neiko.app.SwipeLayoutBase;
import zlc.season.practicalrecyclerview.PracticalRecyclerView;

import static seiko.neiko.dao.mPath.sitedPath;

public class SearchActivity extends SwipeLayoutBase {
    private static final int LIST_NUMBER = 2;

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
        setSearchBar();
        if (!TextUtils.isEmpty(key)) {
            DoLoadViewModel(source, key);
            mSearchBar.setSearchText(key);
        }
    }

    private void setRecView() {
        adapter = new SearchAdapter(source);
        recView.setLayoutManager(new StaggeredGridLayoutManager(LIST_NUMBER, OrientationHelper.VERTICAL));
        recView.setHasFixedSize(true);
        if (allSource) {
            recView.setAdapterWithLoading(adapter);
            adapter.addAll(new ArrayList<>());
        } else {
            recView.setAdapterWithLoading(adapter);
        }
    }

    private void setSearchBar() {
        mSearchBar.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {}

            @Override
            public void onSearchAction(String key) {isallSource(key, allSource);}
        });
    }

    //=================================
    private Handler handler = new Handler();
    /** 是否为多插件搜索 */
    private void isallSource(String key, boolean allSource) {
        adapter.clear();
        adapter.notifyDataSetChanged();
        adapter.showLoading();
        if (allSource) {
            handler.postDelayed(() -> addSubscription(subscription(key)), 300);
        } else {
            DoLoadViewModel(source, key);
        }
    }

    //=================================
    /** 创建多任务subscription */
    private Subscription subscription(String key) {
        File file = new File(sitedPath);
        return Observable.from(file.listFiles())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((File file1) ->{
                    DdSource source = SourceApi.getDefault().getByTitle(file1.getName());
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
            if (code == 1)
                adapter.addAll(viewModel.mDatas);
        });
    }
}
