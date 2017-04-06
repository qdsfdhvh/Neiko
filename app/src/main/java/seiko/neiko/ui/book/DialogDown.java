package seiko.neiko.ui.book;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Flowable;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.ResourceSubscriber;
import seiko.neiko.R;
import seiko.neiko.dao.db.DownDbApi;
import seiko.neiko.dao.engine.DdSource;
import seiko.neiko.models.Book;
import seiko.neiko.models.DownSectionBean;
import seiko.neiko.service.DownloadManger;
import seiko.neiko.service.DownloadStatus;
import seiko.neiko.utils.HintUtil;
import seiko.neiko.viewModels.BookViewModel;
import zlc.season.practicalrecyclerview.PracticalRecyclerView;

import static seiko.neiko.dao.mPath.getBookDownPath;

/**
 * Created by Seiko on 2016/12/21. Y
 */

class DialogDown {
    @BindView(R.id.recView)
    PracticalRecyclerView recView;

    private final List<DownSectionBean> list;
    private DialogDownAdapter adapter;
    private AlertDialog alertDialog;
    private String path;
    private boolean alldown = false;
    private BookViewModel viewModel;
    private DdSource source;
    private DownloadManger manger;


    DialogDown(Context context, View view, BookViewModel viewModel, DdSource source) {
        ButterKnife.bind(this, view);
        this.viewModel = viewModel;
        this.source = source;

        if (manger == null) {
            manger = DownloadManger.create(context);
        }
        adapter = new DialogDownAdapter();
        recView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recView.setAdapterWithLoading(adapter);

        list = new ArrayList<>();
        path = getBookDownPath(viewModel.source, viewModel.name);


        Flowable.just(viewModel.sectionList)
                .concatMap(new Function<List<Book>, Publisher<Book>>() {
                    @Override
                    public Publisher<Book> apply(List<Book> books) throws Exception {
                        return Flowable.fromIterable(books);
                    }
                })
                .subscribe(new Consumer<Book>() {
                    @Override
                    public void accept(Book book) throws Exception {
                        DownSectionBean down = new DownSectionBean();
                        down.setSection(book.getSection());

                        String url = book.getSection_url();
                        down.setSectionurl(url);
                        down.setStatus(DownDbApi.readDownedSectionState(url));

                        down.setPath(path);
                        down.setIndex(book.getIndex());

                        //是否已经添加
                        switch (down.getStatus()) {
                            default:
                            case DownloadStatus.STATE_NONE:
                                down.setAdd(false);
                                break;
                            case DownloadStatus.STATE_START:
                            case DownloadStatus.STATE_DOWNLOADED:
                                down.setAdd(true);
                                break;
                        }
                        list.add(down);
                    }
                });

        adapter.addAll(list);

        alertDialog = new AlertDialog.Builder(context)
                .setTitle("选择章节")
                .setView(view)
                .setCancelable(true)
                .create();

        alertDialog.show();
    }

    //全部
    @OnClick(R.id.sectionAll)
    void sectionAll() {
        if (alldown) {
            setStatus(DownloadStatus.STATE_START);
        } else {
            setStatus(DownloadStatus.STATE_NONE);
        }
        alldown = !alldown;
        adapter.notifyDataSetChanged();
    }

    private void setStatus(final int status) {
        for(DownSectionBean down:list) {
            if (!down.getAdd())
                down.setStatus(status);
        }
    }


    //确认
    @OnClick(R.id.center)
    void center() {
        addDown();
    }

    private int i;
    private void addDown() {
        i = 0;

        Flowable.just(list)
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<List<DownSectionBean>, Publisher<DownSectionBean>>() {
                    @Override
                    public Publisher<DownSectionBean> apply(List<DownSectionBean> downSectionBeans) throws Exception {
                        return Flowable.fromIterable(downSectionBeans);
                    }
                })
                .filter(new Predicate<DownSectionBean>() {
                    @Override
                    public boolean test(DownSectionBean down) throws Exception {
                        return down.getStatus() == DownloadStatus.STATE_START && !down.getAdd();
                    }
                })
                .filter(new Predicate<DownSectionBean>() {
                    @Override
                    public boolean test(DownSectionBean down) throws Exception {
                        int type = source.section(down.getSectionurl()).dtype();
                        if (type == 1) {
                            down.setType(type);
                            return true;
                        } else {
                            return false;
                        }
                    }
                })
                .subscribe(new ResourceSubscriber<DownSectionBean>() {
                    @Override
                    public void onNext(DownSectionBean down) {
                        down.setBkey(viewModel.bookKey);
                        down.setSource(viewModel.source);
                        down.setBookName(viewModel.name);
                        down.setBookUrl(viewModel.bookUrl);
                        DownDbApi.addDownedSection(down);

                        manger.addDownload(down);
                        i++;
                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onComplete() {
                        if (i > 0) {
                            DownDbApi.addDownedQueue(viewModel, path);
                            //更新所需要下载的章节总数
                            final int total = DownDbApi.getDownedQueueTotal(viewModel.bookKey);
                            DownDbApi.setDownedQueueTotal(viewModel.bookKey, total + i);
                            disView();
                        } else {
                            HintUtil.show("请选择章节");
                        }
                    }
                });
    }

    //关闭
    @OnClick(R.id.cancel)
    void cancel() {disView();}

    private void disView() {
        if (manger != null) {
            manger.unbindService();
        }
        alertDialog.dismiss();
    }
}
