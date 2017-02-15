package seiko.neiko.ui.main;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import seiko.neiko.R;
import seiko.neiko.dao.FabScroll;
import seiko.neiko.dao.db.SiteDbApi;
import seiko.neiko.models.SourceModel;
import seiko.neiko.rx.RxEvent;
import seiko.neiko.app.FragmentBase;
import seiko.neiko.utils.FileUtil;
import seiko.neiko.widget.fab.FloatingActionButton;
import seiko.neiko.widget.helper.OnDragListener;
import seiko.neiko.widget.helper.SimpleItemTouchHelperCallback;

import static seiko.neiko.dao.mNum.HOME_NUMBER;
import static seiko.neiko.dao.mPath.cachePath;
import static seiko.neiko.dao.mPath.sitedPath;

/**
 * Created by Seiko on 2016/11/9. YiKu
 */

public class MainSiteDFragment extends FragmentBase implements OnDragListener {

    @BindView(R.id.recView)
    RecyclerView recView;
    @BindView(R.id.fab_refresh)
    FloatingActionButton fab_button;

    private MainSiteDAdapter adapter;

    private List<ShowView> shows;

    public void addShowView(ShowView mshowView) {
        if (shows == null) {
            shows = new ArrayList<>();
        }
        shows.add(mshowView);
    }

    @Override
    public int getLayoutId() {return R.layout.fragment_main;}

    /** 从数据库加载数据 */
    @Override
    public void initView() {
        setRec();
        loadList();
        RxAndroid();
        TouchHelper();
    }

    private void setRec() {
        adapter = new MainSiteDAdapter(this);
        final GridLayoutManager glm = new GridLayoutManager(getContext(), HOME_NUMBER);
        recView.setLayoutManager(glm);
        recView.setHasFixedSize(true);
        recView.setAdapter(adapter);
        FabScroll.showFab(recView, fab_button);
    }

    private void TouchHelper() {
        new SimpleItemTouchHelperCallback(adapter)
                .setmOnDragListener(this)
                .attachToRecyclerView(recView);
    }

    //===============================================
    private boolean isdelete = false;
    /** 删除模式 */
    @OnClick(R.id.fab_refresh)
    void fab_button() {
        if (isdelete) {
            fab_button.setSelected(false);
            isdelete = false;
            for (ShowView view:shows) {
                view.ShowCheck(false);
            }
        } else {
            fab_button.setSelected(true);
            isdelete = true;
            for (ShowView view:shows) {
                view.ShowCheck(true);
            }
        }
        adapter.setDelete(isdelete);
        ischange = true;
    }

    //=====================================
    /** RxBus */
    private void RxAndroid() {
        addSubscription(RxEvent.EVENT_MAIN_SITED, (RxEvent event) -> {
            SourceModel m = new SourceModel();
            m.title = (String) event.getData();
            m.url   = (String) event.getData(1);
            adapter.addSiteD(m);
            saveData();
        });

        addSubscription(RxEvent.EVENT_COPY_SITED, (RxEvent event) -> {
            adapter.clear();
            adapter.addAll(SiteDbApi.getSources());
            adapter.notifyDataSetChanged();
        });
    }

    //=====================================
    /** List相关 */
    private void loadList() {
        Type type = new TypeToken<List<SourceModel>>(){}.getType();
        List<SourceModel> list = FileUtil.get(cachePath + "main/siteDList", type);

        if (list == null) {
            Log.d("MainSiteD", "本地加载失败");
            list = new ArrayList<>();
            File file = new File(sitedPath);

            if (file.listFiles() != null && file.listFiles().length > 0) {
                for (File a : file.listFiles()) {
                    SourceModel m = new SourceModel();
                    m.title = a.getName();
                    m.key = "1";
                    list.add(m);
                }
            }
        }
        adapter.addAll(list);
    }

    private boolean ischange = false;

    @Override
    public void onFinishDrag() {ischange = true;}

    @Override
    public void onDestroy() {
        super.onDestroy();
        //存入缓存
        if (ischange) {
            saveData();
        }
    }

    private void saveData() {
        Log.d("MainSiteD", "存入缓存");
        FileUtil.save(cachePath + "main/siteDList", adapter.getData());
    }

}
