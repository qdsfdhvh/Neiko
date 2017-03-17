package seiko.neiko.ui.book;

import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import seiko.neiko.R;
import seiko.neiko.dao.db.DownDbApi;
import seiko.neiko.dao.engine.DdSource;
import seiko.neiko.models.Book;
import seiko.neiko.models.DownSectionBean;
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
    private int alldown = 0;
    private BookViewModel viewModel;
    private DdSource source;


    DialogDown(View view, BookViewModel viewModel, DdSource source) {
        ButterKnife.bind(this, view);
        this.viewModel = viewModel;
        this.source = source;

        adapter = new DialogDownAdapter();
        recView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recView.setAdapterWithLoading(adapter);

        list = new ArrayList<>();
        path = getBookDownPath(viewModel.source, viewModel.name);



        for (Book book: viewModel.sectionList) {
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
        adapter.addAll(list);

        alertDialog = new AlertDialog.Builder(view.getContext())
                .setTitle("选择章节")
                .setView(view)
                .setCancelable(true)
                .create();
        alertDialog.show();
    }

    //全部
    @OnClick(R.id.sectionAll)
    void sectionAll() {
        switch (alldown) {
            case 0:
                for(DownSectionBean down:list) {
                    if (!down.getAdd())
                        down.setStatus(DownloadStatus.STATE_START);
                }
                alldown = 1;
                adapter.notifyDataSetChanged();
                break;
            case 1:
                for(DownSectionBean down:list) {
                    if (!down.getAdd())
                        down.setStatus(DownloadStatus.STATE_NONE);
                }
                alldown = 0;
                adapter.notifyDataSetChanged();
                break;
        }
    }

    //确认
    @OnClick(R.id.center)
    void center() {
        new Thread(new addDown()).start();
    }

    private class addDown implements Runnable {

        @Override
        public void run() {
            int i = 0;
            String bkey = viewModel.bookKey;


            for (DownSectionBean down : list) {
                if (down.getStatus() == DownloadStatus.STATE_START && !down.getAdd()) {

                    int dtype = source.section(down.getSectionurl()).dtype();

                    if (dtype != 1) {
                        HintUtil.show("只支持dtype1的漫画，错误起始章节：" + down.getSection());
                        return;
                    }

                    down.setType(dtype);
                    down.setBkey(bkey);
                    down.setSource(viewModel.source);
                    down.setBookName(viewModel.name);
                    down.setBookUrl(viewModel.bookUrl);
                    DownDbApi.addDownedSection(down);
                    i++;

                }
            }


            if (i > 0) {
                DownDbApi.addDownedQueue(viewModel, path);
                //更新所需要下载的章节总数
                final int total = DownDbApi.getDownedQueueTotal(bkey);
                DownDbApi.setDownedQueueTotal(bkey, total + i);
                alertDialog.dismiss();
            } else {
                HintUtil.show("请选择章节");
            }
        }
    }

    //关闭
    @OnClick(R.id.cancel)
    void cancel() {alertDialog.dismiss();}
}
