package seiko.neiko.ui.section1;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.noear.sited.SdSourceCallback;

import java.util.List;

import butterknife.BindView;
import seiko.neiko.R;
import seiko.neiko.dao.db.DbApi;
import seiko.neiko.models.Book;
import seiko.neiko.models.BookPage;
import seiko.neiko.models.ImgUrlBean;
import seiko.neiko.viewModels.BookViewModel;
import seiko.neiko.viewModels.Section1ViewModel;
import seiko.neiko.widget.refresh.IPullToRefreshView;
import seiko.neiko.widget.refresh.OnRefreshListener;
import seiko.neiko.widget.refresh.PullToRefreshState;
import seiko.neiko.widget.refresh.VerticalPullToRefreshLayout;

/**
 * Created by Seiko on 2016/12/22. Y
 * Section1：流水模式
 */

public class Section1FragmentS extends Section1FragmentBase {

    private Section1ViewModel viewModel;
    private BookViewModel viewModel4;
    private Section1FragmentSAdapter adapter;

    @BindView(R.id.recView)
    RecyclerView recView;
    @BindView(R.id.refresh_view)
    VerticalPullToRefreshLayout refresh_view;


    @Override
    public int getLayoutId() {return R.layout.fragment_section1_stream;}

    @Override
    public void initViews(Bundle bundle) {
        setRecView();
        switch (dtype) {
            case 1:
                viewModel = new Section1ViewModel();
                getNodeViewModel(imgList.get(imgindex), imgindex, 0);
                setPtrHandler();  //加载更多
                break;
            case 4:
                viewModel4 = new BookViewModel(source.title, bookUrl, dtype);
                getNodeViewModel();
                break;
        }
    }

    private void setRecView() {
        adapter = new Section1FragmentSAdapter(bookUrl);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        recView.setLayoutManager(llm);
        recView.setHasFixedSize(true);
        recView.setAdapter(adapter);
        section1View.onScroll(recView, llm);
    }

    //=================================================
    /** dtype1：加载数据 */
    private void getNodeViewModel(final Book book, final int imgindex, final int isprev) {
        List<ImgUrlBean> list = readPath(book.getSection());
        if (list != null) {
            addAadapter(list, book, imgindex, isprev);
        } else {
            viewModel.clear();
            source.getNodeViewModel(viewModel, true, book.getSection_url(), source.section(book.getSection_url()), new SdSourceCallback() {
                @Override
                public void run(Integer code) {
                    if (code == 1) {
                        addAadapter(viewModel.imgList, book, imgindex, isprev);
                    }
                }
            });
        }
    }

    private void addAadapter(List<ImgUrlBean> list, Book book, int imgindex, int isprev) {
        BookPage bookPage = new BookPage();
        bookPage.setBook_pages(list.size());
        bookPage.setBook_title(book.getSection());
        bookPage.setBook_url(book.getSection_url());
        bookPage.setBook_index(imgindex);

        switch (isprev) {
            case 0:
                adapter.addAll(list);
                activity.bookPages.add(bookPage);
                if (dtype == 1) { //读取记录
                    int lastpage = DbApi.getLastBookPage(book.getSection_url());
                    recView.scrollToPosition(lastpage);
                }
                break;
            case 1:
                adapter.insertAll(0, list);
                activity.bookPages.add(0, bookPage);
                refresh_view.refreshFinish(PullToRefreshState.SUCCEED);
                break;
            case -1:
                adapter.insertAllBack(adapter.getDataSize() - 1, list);
                activity.bookPages.add(bookPage);
                refresh_view.loadmoreFinish(PullToRefreshState.SUCCEED);
                break;
        }
        activity.readOk();
    }

    //=================================================
    /** dtype1：加载更多 */
    private void setPtrHandler() {
        refresh_view.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(IPullToRefreshView pullToRefreshLayout) { //下拉加载 顶部
                top += t;
                if (isBottom(imgindex + top)) {
                    refresh_view.refreshFinish(PullToRefreshState.SUCCEED);
                    return;
                }
                getNodeViewModel(imgList.get(imgindex+top), imgindex+top, 1);
            }

            @Override
            public void onLoadMore(IPullToRefreshView pullToRefreshLayout) {  //上滑加载 底部
                bot += -t;
                if (isBottom(imgindex + bot)) {
                    refresh_view.loadmoreFinish(PullToRefreshState.SUCCEED);
                    return;
                }
                getNodeViewModel(imgList.get(imgindex+bot), imgindex+bot, -1);
            }
        });
    }

    //=====================================================
    /** dtype4：从网络中加载数据 */
    private void getNodeViewModel() {
        viewModel4.clear();
        source.getNodeViewModel(viewModel4, false, bookUrl, source.book(bookUrl), new SdSourceCallback() {
            @Override
            public void run(Integer code) {
                if (code == 1) {
                    BookPage book = new BookPage();
                    book.setBook_pages(viewModel4.list.size());
                    book.setBook_title(bookName);
                    activity.bookPages.add(book);
                    adapter.addAll(viewModel4.list);
                    activity.readOk();
                }
            }
        });
    }

}
