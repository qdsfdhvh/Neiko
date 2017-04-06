package seiko.neiko.ui.home;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import org.noear.sited.SdSourceCallback;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import seiko.neiko.dao.engine.DdApi;
import seiko.neiko.models.HomeSaveEvent;
import seiko.neiko.R;
import seiko.neiko.dao.SourceApi;
import seiko.neiko.ui.search.SearchActivity;
import seiko.neiko.utils.FileUtil;
import seiko.neiko.ui.main.MainViewModel;
import seiko.neiko.app.BaseSwipeLayout;
import zlc.season.practicalrecyclerview.PracticalRecyclerView;

import static seiko.neiko.dao.mPath.getHomeCachePath;
import static seiko.neiko.dao.mNum.HOTS_NUMBER;
import static seiko.neiko.dao.mNum.TAGS_NUMBER;

public class AnimeHomeActivity extends BaseSwipeLayout implements FloatingSearchView.OnSearchListener {

    public static HomeModel m;

    //界面
    @BindView(R.id.floating_search_view)
    FloatingSearchView mSearchBar;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;
    @BindView(R.id.refresh)
    SwipeRefreshLayout refresh;

    private HomeHotAdapter adapter_hot;
    private HomeTagAdapter adapter_tag;
    private MainViewModel model = new MainViewModel();
    private String path;

    @Override
    public int getLayoutId() {return R.layout.activity_home;}

    @Override
    public void initViews(Bundle bundle) {
        source = SourceApi.getDefault().getByUrl(m.url);
        path = getHomeCachePath(m.url);
        addRefresh();
        getPath();  //加载参数
        mViewPager.setAdapter(new PagerAdapterMain2());
        SearchHint();
    }

    //=================================================
    /** 尝试从本地获得参数 */
    private void getPath() {
        HomeSaveEvent event = FileUtil.get(path, HomeSaveEvent.class);
        if (event != null)
            addList(event);
        else
            getNodeViewModel();
    }

    /** 从网络中获得内容 */
    private void getNodeViewModel() {
        model.clear();
        source.getNodeViewModel(model, source.home, true, new SdSourceCallback() {
            @Override
            public void run(Integer code) {
                if (code==1) {
                    HomeSaveEvent event = new HomeSaveEvent(model.hotList, model.tagList);
                    FileUtil.save(path, event);
                    addList(event);
                } else {
                    adapter_hot.loadMoreFailed();
                }
            }
        });
    }

    private void addList(HomeSaveEvent ev) {
        if (ev.getHotList() != null && ev.getHotList().size() > 0) {
            if (adapter_hot == null) {
                addHot();
            }
            adapter_hot.addAll(ev.getHotList());
        }

        if (ev.getTagList() != null && ev.getTagList().size() > 0) {
            if (adapter_tag == null) {
                addTag();
            }
            adapter_tag.addAll(ev.getTagList());
        }

        refresh.setRefreshing(false);
    }

    //=================================================
    /** 添加界面及其设置 */
    //热门
    private void addHot() {
        View hot = inflate(R.layout.fragment_home_hot, mViewPager);
        views.add(hot);
        PracticalRecyclerView recView_hot = ButterKnife.findById(hot, R.id.recView_hot);

        adapter_hot = new HomeHotAdapter(source, m.url);
        recView_hot.setLayoutManager(new StaggeredGridLayoutManager(HOTS_NUMBER, OrientationHelper.VERTICAL));
        recView_hot.setHasFixedSize(true);
        recView_hot.setAdapterWithLoading(adapter_hot);
    }

    //分类
    private void addTag() {
        View tag = inflate(R.layout.fragment_home_tag, mViewPager);
        views.add(tag);
        RecyclerView recView_tag = ButterKnife.findById(tag, R.id.recView_tag);

        adapter_tag = new HomeTagAdapter();
        recView_tag.setLayoutManager(new GridLayoutManager(this, TAGS_NUMBER));
        recView_tag.setHasFixedSize(true);
        recView_tag.setAdapter(adapter_tag);
    }

    //下拉刷新
    private void addRefresh() {
        refresh.setColorSchemeResources(
                R.color.deep_purple_500, R.color.pink_500,
                R.color.orange_500, R.color.brown_500,
                R.color.indigo_500, R.color.blue_500,
                R.color.teal_500);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (adapter_hot != null) adapter_hot.clear();
                if (adapter_tag != null) adapter_tag.clear();
                getNodeViewModel();
            }
        });
    }

    //=====================================================
    /** 定义自己的ViewPager适配器。*/
    private ArrayList<View> views = new ArrayList<>();
    private class PagerAdapterMain2 extends PagerAdapter {

        @Override
        public int getCount() {return views!=null ? views.size():0;}

        @Override
        public boolean isViewFromObject(View view, Object object) {return view == object;}

        //每次滑动的时候生成的组件
        @Override
        public Object instantiateItem(ViewGroup container, int i) {
            container.addView(views.get(i));
            return views.get(i);
        }

        //滑动切换的时候销毁当前的组件
        @Override
        public void destroyItem(ViewGroup container, int i, Object object) {
            container.removeView((View) object);
        }
    }


    //=====================================================
    /** 搜索框相关 */
    @Override
    public void onSearchAction(String key) {
        SearchActivity.key = key;
        SearchActivity.allSource = false;
        openActivity(SearchActivity.class);
    }

    @Override
    public void onSuggestionClicked(SearchSuggestion ss) {}

    private void SearchHint() {
        String name = DdApi.dtypeName(source.book(m.url).dtype());
        mSearchBar.setSearchHint("搜索" + name + "....");
        mSearchBar.setOnSearchListener(this);
    }

    //=====================================================
    /** 重命名站点名称 */
    @Override
    protected void onResume() {
        super.onResume();
        mSearchBar.setSearchBarTitle(m.name + " v" + source.ver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        m = null;
    }
}
