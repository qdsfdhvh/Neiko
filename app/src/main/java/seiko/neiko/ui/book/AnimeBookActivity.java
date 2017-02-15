package seiko.neiko.ui.book;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import butterknife.BindView;
import seiko.neiko.dao.FabScroll;
import seiko.neiko.dao.SourceApi;
import seiko.neiko.dao.ImageLoader;
import seiko.neiko.dao.mNum;
import seiko.neiko.models.BookInfBean;
import seiko.neiko.rx.RxBus;
import seiko.neiko.rx.RxEvent;
import seiko.neiko.R;
import seiko.neiko.models.Book;
import seiko.neiko.dao.mIntent;
import seiko.neiko.dao.db.DbApi;
import seiko.neiko.utils.FileUtil;
import seiko.neiko.viewModels.BookViewModel;
import seiko.neiko.app.SwipeLayoutBase;
import seiko.neiko.widget.fab.FloatingActionMenu;

import static seiko.neiko.dao.mPath.getBookCachePath;

public class AnimeBookActivity extends SwipeLayoutBase {

    public static BookModel m;

    //界面
    @BindView(R.id.book_logo)
    ImageView book_logo;
    @BindView(R.id.book_name)
    TextView book_name;
    @BindView(R.id.book_author)
    TextView book_author;
    @BindView(R.id.book_intro)
    TextView book_intro;
    @BindView(R.id.book_card)
    CardView book_card;
    @BindView(R.id.now_read2)
    TextView nowread2;
    @BindView(R.id.recView)
    RecyclerView recView;
    @BindView(R.id.fab_menu)
    FloatingActionMenu fab_menu;

    private BookViewModel viewModel;
    private BookAdapter adapter;
    private GridLayoutManager glm;
    private boolean isReadOk = false;  //读取完成才能使用fab按钮

    private Context mContext;
    private String path;
    private BookInfBean bean;

    @Override
    public int getLayoutId() {return R.layout.activity_book;}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        source = SourceApi.getDefault().getByUrl(m.bookUrl);
        if (source == null) return;

        if (m.dtype < 1)
            m.dtype = source.book(m.bookUrl).dtype();

        path = getBookCachePath(m.bookUrl);

        setRecView();
        initLocal();
        onButtons();
        RxAndroid();
        FabScroll.showFab(recView, fab_menu);
    }

    private void setRecView() {
        adapter = new BookAdapter();
        glm = new GridLayoutManager(this, 1);
        recView.setLayoutManager(glm);
        recView.setHasFixedSize(true);
        recView.setAdapter(adapter);
    }

    private void RxAndroid() {
        //修改阅读记录
        addSubscription(RxEvent.EVENT_SECTION1_SAVE, (RxEvent event) -> {
            String url = (String) event.getData();
            int lastS = (int) event.getData(1);

            adapter.setLast_surl(url);
            recView.smoothScrollToPosition(lastS);
        });
    }

    private void initLocal() {
        // 尝试读取本地记录
        viewModel = FileUtil.get(path, BookViewModel.class);
        if (viewModel != null  && viewModel.sectionList != null && viewModel.book != null)
            DoBindingView();
        else
            getNodeViewModel();
    }


    /** 从网络中获得内容 */
    private void getNodeViewModel() {
        if (viewModel == null)
            viewModel = new BookViewModel(source.title, m.bookUrl, m.dtype);
        viewModel.clear();

        source.getNodeViewModel(viewModel, true, m.bookUrl, source.book(m.bookUrl), (code) -> {
            if (code == 1) {
                if (viewModel.sectionList.size() > 0)
                    FileUtil.save(path, viewModel);
                DoBindingView();
            } else {
                gone(nowread2);  //隐藏加载界面
                toast("链接失败");
            }
        });
    }

    /** 内容加载到界面 */
    private void DoBindingView() {
        //生成InfBean
        bean = new BookInfBean(viewModel.sectionList, viewModel.name, m.bookUrl, m.logo, viewModel.bookKey);
        //隐藏加载界面
        gone(nowread2);
        //显示介绍卡片
        visible(book_card);
        //标题
        if (TextUtils.isEmpty(viewModel.name)) {
            viewModel.name = "null";
        }
        book_name.setText(viewModel.name);
        //作者
        if (TextUtils.isEmpty(viewModel.author)) {
            viewModel.author = "null";
        }
        book_author.setText(viewModel.author);
        //介绍
        if (!TextUtils.isEmpty(viewModel.intro)) {
            visible(book_intro);
            book_intro.setText(String.valueOf("\u3000\u3000" + viewModel.intro));
        }
        //图标
        if (TextUtils.isEmpty(viewModel.logo)) {
            viewModel.logo = m.logo;
            if (viewModel.book != null)
                viewModel.book.setLogo(m.logo);
        }
        ImageLoader.getDefault().display(this, book_logo, viewModel.logo, viewModel.name, m.bookUrl);
        //目录
        int dtype = source.section(m.bookUrl).dtype();
        glm.setSpanCount(mNum.SecNum(viewModel.sectionList, dtype));   //判断列数
        adapter.setEvent(source, bean);
        adapter.addAll(viewModel.sectionList);
        //读取记录
        getLastSection();
        //加到历史
        addHist();
        //加载完成
        isReadOk = true;
    }

    /* 读取记录 */
    private void getLastSection() {
        final int lastS = DbApi.getBookLastLookSection(viewModel.bookKey);
        if (lastS > 3) {
            recView.scrollToPosition(lastS - 3);//无动画，可以主动触发
        }

        final String last_surl = DbApi.getLastBookUrl(viewModel.bookKey);
        adapter.setLast_surl(last_surl);
    }

    /* 添加到历史 */
    private void addHist() {
        if (!TextUtils.isEmpty(viewModel.name)) {
            DbApi.addHistory(viewModel);    //添加记录
            RxBus.getDefault().post(new RxEvent(RxEvent.EVENT_MAIN_HIST, viewModel.book)); //同步到历史界面
        }
    }

    //===================================================================
    /** fab按钮组 */
    private void onButtons() {
        rxView(R.id.fab_download); /* 下载 */
        rxView(R.id.fab_refresh);  /* 刷新 */
        rxView(R.id.fab_favor);    /* 收藏 */
        rxView(R.id.fab_setting);  /* 设置 */

        rxView(R.id.book_intro);   /* 简介 */
        rxView(R.id.book_name);    /* 续看 */
        RxView.longClicks(fab_menu.get()).subscribe((Void a) -> {
            final Uri uri = Uri.parse(viewModel.bookUrl);
            final Intent it = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(it);
        });   /* 浏览器打开 */
    }

    private void rxView(int id) {
        RxView.clicks(findViewById(id)).subscribe((Void a) -> setFab(id));
    }

    private void setFab(int id) {
        if (!isReadOk) {
            toast("等待读取完成");
            return;
        }

        switch (id) {
            case R.id.fab_download:
                View down = inflate(R.layout.dialog_book_download, R.id.dialog);
                new DialogDown(down, viewModel, source);
                break;
            case R.id.fab_refresh:
                adapter.clear();
                adapter.notifyDataSetChanged();
                getNodeViewModel();
                break;
            case R.id.fab_favor:
                viewModel.book.setNumber(viewModel.sectionList.size());
                RxBus.getDefault().post(new RxEvent(RxEvent.EVENT_MAIN_LIKE, viewModel.book)); //同步到收藏界面
                break;
            case R.id.fab_setting:
                View setting = inflate(R.layout.dialog_book_setting, R.id.dialog);
                new DialogSetting(setting, viewModel.bookKey);
                break;
            case R.id.book_intro:
                new AlertDialog.Builder(mContext).setMessage(viewModel.intro)
                        .setPositiveButton("关闭", (DialogInterface dif, int j) -> dif.dismiss())  //通知最右按钮
                        .create()
                        .show();
                break;
            case R.id.book_name:
                int lastS = DbApi.getBookLastLookSection(viewModel.bookKey);
                if (lastS < 0) {
                    if (viewModel.sectionList.size() > 0)
                        lastS = viewModel.sectionList.size() - 1;
                    else
                        lastS = 0;
                }
                Book book = viewModel.sectionList.get(lastS);

                final int dtype = source.section(book.getSection_url()).dtype();
                mIntent.Intent_Book(mContext, book, dtype, lastS, bean);
                break;
        }
    }

    //===============================================
    @Override
    protected void onDestroy() {
        super.onDestroy();
        m = null;
    }
}