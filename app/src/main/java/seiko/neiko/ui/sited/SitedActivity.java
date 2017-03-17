package seiko.neiko.ui.sited;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import seiko.neiko.R;
import seiko.neiko.app.SwipeLayoutBase;
import seiko.neiko.models.SourceModel;
import zlc.season.practicalrecyclerview.PracticalRecyclerView;

/**
 * Created by Seiko on 2017/2/13. Y
 */

public class SitedActivity extends SwipeLayoutBase implements SitedView {

    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.recView)
    PracticalRecyclerView recView;

    private SitedAdapter mAdapter;
    private SitedPresenter mPresenter;

    @Override
    public int getLayoutId() {return R.layout.activity_sited;}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTitle.setText("本地插件");

        mAdapter = new SitedAdapter();
        mPresenter = new SitedPresenter();
        mPresenter.attachView(this);

        recView.setLayoutManager(new LinearLayoutManager(this));
        recView.setAdapterWithLoading(mAdapter);
        recView.setRefreshListener(() -> mPresenter.loadData());

        mPresenter.loadData();
    }

    //===============================================
    /** 加载数据 */
    @Override
    public void onLoadSuccess(List<SourceModel> menu) {
        mAdapter.clear();
        mAdapter.addAll(menu);
    }

    @Override
    public void onLoadFailed() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }
}
