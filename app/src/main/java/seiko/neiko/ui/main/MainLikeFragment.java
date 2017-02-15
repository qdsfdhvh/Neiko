package seiko.neiko.ui.main;

import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jakewharton.rxbinding.view.RxView;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;
import seiko.neiko.R;
import seiko.neiko.dao.FabScroll;
import seiko.neiko.dao.SourceApi;
import seiko.neiko.dao.db.DbApi;
import seiko.neiko.dao.engine.DdSource;
import seiko.neiko.dao.mPath;
import seiko.neiko.models.Book;
import seiko.neiko.rx.RxEvent;
import seiko.neiko.app.FragmentBase;
import seiko.neiko.utils.FileUtil;
import seiko.neiko.utils.HintUtil;
import seiko.neiko.viewModels.BookViewModel;
import seiko.neiko.widget.fab.FloatingActionMenu;
import zlc.season.practicalrecyclerview.PracticalRecyclerView;

import static seiko.neiko.dao.mNum.LIKE_NUMBER;
import static seiko.neiko.dao.mPath.backPath;

/**
 * Created by Seiko on 2016/11/9. YiKu
 */

public class MainLikeFragment extends FragmentBase {

    @BindView(R.id.recView)
    PracticalRecyclerView recView;
    @BindView(R.id.fab_menu)
    FloatingActionMenu menu;

    private MainLikeAdapter adapter;

    @Override
    public int getLayoutId() {return R.layout.fragment_main_like;}

    @Override
    public void initView() {
        setRecView();
        RxAndroid();
    }

    private void setRecView() {
        adapter = new MainLikeAdapter();
        recView.setLayoutManager(new StaggeredGridLayoutManager(LIKE_NUMBER, StaggeredGridLayoutManager.VERTICAL));
        recView.setHasFixedSize(true);
        recView.setAdapterWithLoading(adapter);
        adapter.addAll(DbApi.getLike());
        FabScroll.showFab(recView.get(), menu);
    }

    //=====================================
    /** RxBus */
    private void RxAndroid() {
        addSubscription(RxEvent.EVENT_MAIN_LIKE,(RxEvent event) -> {
            final Book book = (Book) event.getData();

            DbApi.addLike(book);

            int num = contain(adapter.getData(), book.getBkey());
            if (num >= 0) {
                if (!book.isref()) {
                    HintUtil.show("已经收藏");
                }
                return;
            }

            if (adapter.getData().size() == 0) {
                adapter.add(book);
            } else {
                adapter.insert(0, book);
                adapter.notifyDataSetChanged();
            }
            HintUtil.show("收藏成功");
        });
        addSubscription(RxEvent.EVENT_COPY_LIKE, (RxEvent event) -> {
            adapter.clear();
            adapter.addAll(DbApi.getLike());
            adapter.notifyDataSetChanged();
        });
    }

    //===============================================
    /** 检测更新相关 */
    @OnClick(R.id.fab_refresh)
    void refresh() {
        new AlertDialog.Builder(getActivity())
                .setMessage("是否探测更新")
                .setNegativeButton("是", (DialogInterface dif, int j) -> getRefresh())      //通知中间按钮
                .setPositiveButton("否", (DialogInterface dif, int j) -> dif.dismiss())      //通知最右按钮
                .create()
                .show();
    }

    /* 检测更新 */
    private void getRefresh() {
        addSubscription(subscription());
    }

    private Subscription subscription() {
        return Observable.from(adapter.getData())
                .subscribeOn(Schedulers.newThread())
//                        .observeOn(AndroidSchedulers.mainThread())
                .subscribe((Book book) -> {
                    DdSource source = SourceApi.getDefault().getByTitle(book.getSource());
                    if (source != null) {
                        getNodeViewModel(source, book);
                    }
                });
    }

    /* 从网上加载目录内容 */
    private void getNodeViewModel(DdSource source, Book book) {
        BookViewModel viewModel = new BookViewModel(book.getSource(), book.getUrl(), book.getType());
        viewModel.clear();
        if (source != null) {
            source.getNodeViewModel(viewModel, true,  book.getUrl(), source.book( book.getUrl()), (code) -> {
                if (code == 1) {
                    int number = viewModel.sectionList.size();

                    if (number != 0 && number != book.getNumber()) {
                        DbApi.setNumber(book.getBkey(), number);
                        book.setNew(true);
                        //刷新界面需要在UI主线程
                        getActivity().runOnUiThread(() -> adapter.notifyDataSetChanged());
                    } else {
                        book.setNew(false);
                    }
                }
            });
        }
    }

    //===============================================
    /** 检测更新相关 */
    private AlertDialog dialog;

    @OnClick(R.id.fab_copy)
    void copy() {
        LinearLayout linear = new LinearLayout(getActivity());
        linear.setOrientation(LinearLayout.VERTICAL);
        Button bt1 = new Button(getActivity());
        bt1.setText("备份");
        Button bt2 = new Button(getActivity());
        bt2.setText("恢复");
        linear.addView(bt1);
        linear.addView(bt2);

        RxView.clicks(bt1).subscribe((Void aVoid) -> likesBackup());
        RxView.clicks(bt2).subscribe((Void aVoid) -> likesRecover());

        dialog = new AlertDialog.Builder(getActivity())
                .setView(linear)
                .setPositiveButton("关闭", (DialogInterface dif, int j) -> dif.dismiss())
                .create();
        dialog.show();
    }

    private void likesBackup() {
        dialog.dismiss();

        ArrayList<Book> list_like = DbApi.getLike();
        if (list_like != null && list_like.size() != 0) {
            String json = new Gson().toJson(list_like);
            FileUtil.saveText2Sdcard(backPath + "Backup", json);
            toast("备份成功");
        } else {
            toast("没有收藏");
        }
    }

    private void likesRecover() {
        dialog.dismiss();
        adapter.clear();
        adapter.notifyDataSetChanged();
        adapter.showLoading();

        new Handler().postDelayed(() -> {
            String json = FileUtil.readTextFromSDcard(backPath + "Backup");
            if (!TextUtils.isEmpty(json)) {
                Type type = new TypeToken<List<Book>>(){}.getType();
                ArrayList<Book> list_like = new Gson().fromJson(json, type);
                if (list_like != null && list_like.size() != 0) {
                    for (Book book : list_like) {
                        book.setIsref(true);
                        DbApi.addLike(book);
                    }
                }
                adapter.addAll(DbApi.getLike());
                toast("恢复成功");
            } else {
                adapter.showError();
                toast("没有找到恢复文件");
            }
        }, 300);
    }
}
