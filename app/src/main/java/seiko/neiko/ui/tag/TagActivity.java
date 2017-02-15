package seiko.neiko.ui.tag;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import seiko.neiko.R;
import seiko.neiko.viewModels.TagViewModel;
import seiko.neiko.app.SwipeLayoutBase;
import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller;
import zlc.season.practicalrecyclerview.PracticalRecyclerView;

/**
 * Created by Seiko on 2016/9/10.YiKu
 */

public class TagActivity extends SwipeLayoutBase {

    public static TagModel m;

    //界面
    @BindView(R.id.recView)
    PracticalRecyclerView recView;
    @BindView(R.id.fast_scroller)
    VerticalRecyclerViewFastScroller fastScroller;
    @BindView(R.id.title)
    TextView mTitle;

    private TagViewModel viewModel;
    private TagAdapter adapter;


    @Override
    public int getLayoutId() {return R.layout.activity_tag;}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new TagViewModel();
        mTitle.setText(m.tagName);
        setRecView();
        DoLoadViewModel();
    }

    /** 加载列表 */
    private void setRecView() {
        adapter = new TagAdapter(source);
        recView.setLayoutManager(new LinearLayoutManager(this));
        recView.setHasFixedSize(true);
        recView.setAdapterWithLoading(adapter);
        fastScroller.setRecyclerView(recView.get());

        recView.get().addOnScrollListener(fastScroller.getOnScrollListener());
        recView.setLoadMoreListener(() -> {
            viewModel.currentPage += 1;
            DoLoadViewModel();
        });
    }

    //===============================================
    /** 加载数据 */
    public void DoLoadViewModel(int page) {
        viewModel.currentPage = page;
        adapter.clear();
        adapter.notifyDataSetChanged();
        adapter.showLoading();

        DoLoadViewModel();
    }

    private void DoLoadViewModel() {
        source.getNodeViewModel(viewModel, false, viewModel.currentPage, m.tagUrl, source.tag(m.tagUrl), (code) -> {
            if (code == 1) {
                mTitle.setText(m.tagName + " " + viewModel.currentPage + "页");
                adapter.addAll(viewModel.mDatas);
            } else {
                toast("加载失败");
            }
        });
    }

    //===============================================
    /** 标题按钮 */
    @OnClick(R.id.title)
    void OnTitle() {
        LayoutInflater inflater = getLayoutInflater();
        View dialog = inflater.inflate(R.layout.dialog_button, (ViewGroup) findViewById(R.id.dialog));
        new DialogTagPage(dialog, this, viewModel.currentPage);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        m = null;
    }
}
