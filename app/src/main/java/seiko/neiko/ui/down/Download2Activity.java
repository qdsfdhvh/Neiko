package seiko.neiko.ui.down;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;
import seiko.neiko.R;
import seiko.neiko.dao.FabScroll;
import seiko.neiko.dao.db.DbApi;
import seiko.neiko.dao.db.DownDbApi;
import seiko.neiko.rx.RxBus;
import seiko.neiko.rx.RxEvent;
import seiko.neiko.models.DownBookBean;
import seiko.neiko.models.DownSectionBean;
import seiko.neiko.service.DownloadManger;
import seiko.neiko.app.BaseSwipeLayout;
import seiko.neiko.widget.fab.FloatingActionMenu;
import zlc.season.practicalrecyclerview.PracticalRecyclerView;

/**
 * Created by Seiko on 2016/11/21. YiKu
 */

public class Download2Activity extends BaseSwipeLayout {

    public static DownBookBean data;

    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.recView)
    PracticalRecyclerView recView;
    @BindView(R.id.fab_menu)
    FloatingActionMenu fab_menu;

    private Download2Adapter adapter;
    private DownloadManger manager;

    @Override
    public int getLayoutId() {return R.layout.activity_down2;}

    @Override
    public void initViews(Bundle bundle) {
        mTitle.setText(data.getTitle());
        setRecView();
        loadData();
        RxAndroid();
        loadSet();
    }

    private void setRecView() {
        manager = DownloadManger.create(this);
        adapter = new Download2Adapter(manager);
        recView.setLayoutManager(new LinearLayoutManager(this));
        recView.setAdapterWithLoading(adapter);
        FabScroll.showFab(recView.get(), fab_menu);
    }

    private void loadSet() {
        adapter.setLast_surl(DbApi.getLastBookUrl(data.getUrl()));

        int lastS = DbApi.getBookLastLookSection(data.getUrl());
        if (lastS > 3) {
            recView.get().scrollToPosition(lastS - 3);//无动画，可以主动触发
        }
    }


    private void loadData() {
        List<DownSectionBean> list = DownDbApi.getDownedSection(data.getBkey());
        DownDbApi.setDownedQueueTotal(data.getBkey(), list.size());
        adapter.clear();
        adapter.addAll(list);
    }
    //============================================
    /** 按钮 */
    @OnClick(R.id.cardView)
    void cardView() {
        if (isVisible(fab_menu))
            fab_menu.hideMenu(true);
        else
            fab_menu.showMenu(true);
    }

    @OnClick(R.id.fab_play)
    void fab_play() {
        for (DownSectionBean bean : adapter.getData()) {
            manager.addDownload(bean);
        }
    }

    @OnClick(R.id.fab_pause)
    void fab_pause() {
        for (DownSectionBean bean : adapter.getData()) {
            manager.pauseDownload(bean);
        }
    }

    //============================================
    /** RxJava */
    private void RxAndroid() {
        //修改阅读记录
        addSubscription(RxEvent.EVENT_SECTION1_SAVE, new Consumer<RxEvent>() {
            @Override
            public void accept(RxEvent event) throws Exception {
                String url = (String) event.getData();
                adapter.setLast_surl(url);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        //重绘下载界面1
        RxBus.getDefault().post(new RxEvent(RxEvent.EVENT_DOWN1_PROCESS));
        //注销service
        manager.unbindService();

        adapter.unsubscribe();
    }
}
