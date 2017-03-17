package seiko.neiko.ui.tag;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import seiko.neiko.R;
import seiko.neiko.app.SwipeLayoutBase;
import seiko.neiko.ui.book.BookModel;
import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller;
import zlc.season.practicalrecyclerview.PracticalRecyclerView;

/**
 * Created by Seiko on 2016/9/10.YiKu
 */

public class TagActivity extends SwipeLayoutBase implements TagView {

    public static TagModel m;

    //界面
    @BindView(R.id.recView)
    PracticalRecyclerView recView;
    @BindView(R.id.fast_scroller)
    VerticalRecyclerViewFastScroller fastScroller;
    @BindView(R.id.title)
    TextView mTitle;

    private TagAdapter mAdapter;
    private TagPresenter mPresenter;

    @Override
    public int getLayoutId() {return R.layout.activity_tag;}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTitle.setText(m.tagName);

        mAdapter = new TagAdapter();
        mPresenter = new TagPresenter(source, m.tagUrl);
        mPresenter.attachView(this);

        recView.setLayoutManager(new LinearLayoutManager(this));
        recView.setHasFixedSize(true);
        recView.setAdapterWithLoading(mAdapter);

        fastScroller.setRecyclerView(recView.get());
        recView.get().addOnScrollListener(fastScroller.getOnScrollListener());
        recView.setLoadMoreListener(() -> mPresenter.loadData(false));

        mPresenter.loadData(true);
    }

    //===============================================
    /** 加载数据 */
    @Override
    public void onLoadSuccess(List<BookModel> list) {
        mTitle.setText(m.tagName + " 第" + mPresenter.getPage() + "页");
        mAdapter.addAll(list);
    }

    @Override
    public void onLoadFailed() {mAdapter.showError();}

    //===============================================
    /** Dialog刷新数据 */
    void DoLoadViewModel(int page) {
        mAdapter.clear();
        mAdapter.notifyDataSetChanged();
        mAdapter.showLoading();

        mPresenter.loadData(page);
    }

    //===============================================
    /** 标题按钮 */
    @OnClick(R.id.title)
    void OnTitle() {DialogTagPage.create(this, mPresenter.getPage());}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
        m = null;
    }

}
