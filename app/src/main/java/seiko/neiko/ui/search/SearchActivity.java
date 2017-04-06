package seiko.neiko.ui.search;

import android.os.Bundle;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import java.util.List;
import butterknife.BindView;
import seiko.neiko.R;
import seiko.neiko.ui.book.BookModel;
import seiko.neiko.app.BaseSwipeLayout;
import seiko.neiko.view.SearchItemView;
import zlc.season.practicalrecyclerview.PracticalRecyclerView;

import static seiko.neiko.dao.mNum.SEARCH_NUMBER;

public class SearchActivity extends BaseSwipeLayout implements FloatingSearchView.OnSearchListener, SearchItemView {

    public static String key;
    public static boolean allSource;

    //界面
    @BindView(R.id.rv_result)
    PracticalRecyclerView recView;
    @BindView(R.id.floating_search_view)
    FloatingSearchView mSearchBar;
    @BindView(R.id.custom_progress_bar)
    ProgressBar progressBar;

    private SearchAdapter mAdapter;
    private SearchPresenter mPresenter;

    @Override
    public int getLayoutId() {return R.layout.activity_search;}

    @Override
    public void initViews(Bundle bundle) {
        setRecView();
    }

    private void setRecView() {
        mAdapter = new SearchAdapter();
        recView.setLayoutManager(new StaggeredGridLayoutManager(SEARCH_NUMBER, OrientationHelper.VERTICAL));
        recView.setHasFixedSize(true);
        recView.setAdapter(mAdapter);
        mPresenter = new SearchPresenter(source);
        mPresenter.setItemCallBack(this);

        mSearchBar.setOnSearchListener(this);
        if (!TextUtils.isEmpty(key)) {
            mSearchBar.setSearchText(key);
            mPresenter.loadData(key, false);
        }
    }

    @Override
    public void onSuccess(List<BookModel> list) {
        progressBar.setVisibility(View.GONE);
        mAdapter.addAll(list);
    }

    @Override
    public void onFailed() {

    }

    //=================================
    /** 搜索 */
    @Override
    public void onSuggestionClicked(SearchSuggestion searchSuggestion) {}

    @Override
    public void onSearchAction(String key) {
        progressBar.setVisibility(View.VISIBLE);
        mAdapter.clear();
        mPresenter.loadData(key, allSource);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        allSource = true;
        key = null;
    }
}
