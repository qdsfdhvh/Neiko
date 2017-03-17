package seiko.neiko.ui.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.gson.reflect.TypeToken;
import com.jakewharton.rxbinding.view.RxView;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import seiko.neiko.R;
import seiko.neiko.dao.FabScroll;
import seiko.neiko.dao.db.SiteDbApi;
import seiko.neiko.models.SourceModel;
import seiko.neiko.rx.RxEvent;
import seiko.neiko.app.FragmentBase;
import seiko.neiko.utils.FileUtil;
import seiko.neiko.widget.fab.FloatingActionButton;
import seiko.neiko.widget.fab.FloatingActionMenu;
import seiko.neiko.widget.helper.OnDragListener;
import seiko.neiko.widget.helper.SimpleItemTouchHelperCallback;
import zlc.season.practicalrecyclerview.PracticalRecyclerView;

import static seiko.neiko.dao.mNum.HOME_NUMBER;
import static seiko.neiko.dao.mPath.cachePath;
import static seiko.neiko.dao.mPath.sitedPath;

/**
 * Created by Seiko on 2016/11/9. YiKu
 */

public class MainSiteDFragment extends FragmentBase implements OnDragListener {

    @BindView(R.id.recView)
    PracticalRecyclerView recView;
    @BindView(R.id.fab_menu)
    FloatingActionMenu menu;
    @BindView(R.id.fab_delete)
    FloatingActionButton fab_delete;

    private MainSiteDAdapter adapter;
    private List<ShowView> shows;
    private boolean isdelete = false;

    void addShowView(ShowView mshowView) {
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
    }

    private void setRec() {
        adapter = new MainSiteDAdapter(this);
        final GridLayoutManager glm = new GridLayoutManager(getContext(), HOME_NUMBER);
        recView.setLayoutManager(glm);
        recView.setHasFixedSize(true);
        recView.setAdapterWithLoading(adapter);
        FabScroll.showFab(recView.get(), menu);
        new SimpleItemTouchHelperCallback(adapter, this).attachToRecyclerView(recView.get());
    }

    //===============================================
    /** 打开插件中心 */
    private AlertDialog dialog;

    @OnClick(R.id.fab_web)
    void onWeb() {
        LinearLayout linear = new LinearLayout(getActivity());
        linear.setOrientation(LinearLayout.VERTICAL);
        Button bt1 = new Button(getActivity());
        bt1.setText("插件中心");
        Button bt2 = new Button(getActivity());
        bt2.setText("独立插件");
        Button bt3 = new Button(getActivity());
        bt3.setText(String.valueOf("Guang插件"));
        linear.addView(bt1);
        linear.addView(bt2);
        linear.addView(bt3);

        rxView(bt1, "http://sited.noear.org/");
        rxView(bt2, "http://sited.ka94.com/");
        rxView(bt3, "http://guang.ka94.com/sited.html");

        dialog = new AlertDialog.Builder(getActivity())
                .setView(linear)
                .setPositiveButton("关闭", (DialogInterface dif, int j) -> dif.dismiss())
                .create();
        dialog.show();
    }

    private void rxView(View v, String url) {
        RxView.clicks(v).subscribe((Void aVoid) -> {
            dialog.dismiss();

            final Uri uri = Uri.parse(url);
            final Intent it = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(it);
        });
    }

    //=====================================
    /** RxBus */
    private void RxAndroid() {
        addSubscription(RxEvent.EVENT_COPY_SITED, (RxEvent event) -> {
            adapter.clear();
            adapter.addAll(SiteDbApi.getSources());
            adapter.notifyDataSetChanged();
        });
        addSubscription(RxEvent.EVENT_MAIN_SITED, (RxEvent event) -> {
            SourceModel m = (SourceModel) event.getData();
            if (adapter != null) {
                for (SourceModel m1:adapter.getData()) {
                    if (m1.title.contains(m.title))
                        return;
                }

                adapter.add(m);

                saveData();
            }
        });
        /* 删除模式 */
        RxView.clicks(fab_delete).subscribe((Void aVoid) -> {
            if (isdelete) {
                fab_delete.setSelected(false);
                isdelete = false;
                for (ShowView view:shows) {
                    view.ShowCheck(false);
                }
            } else {
                fab_delete.setSelected(true);
                isdelete = true;
                for (ShowView view:shows) {
                    view.ShowCheck(true);
                }
            }
            adapter.setDelete(isdelete);
            ischange = true;
        });
    }

    //=====================================
    /** List相关 */
    private void loadList() {
        Flowable.create((FlowableEmitter<List<SourceModel>> e) -> {
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
                            list.add(m);
                        }
                    }
                }

                e.onNext(list);
                e.onComplete();
        }, BackpressureStrategy.ERROR)
                .subscribeOn(Schedulers.io())
//                .delay(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((List<SourceModel> list) ->adapter.addAll(list));
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
