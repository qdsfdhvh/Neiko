package seiko.neiko.ui.section1;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import seiko.neiko.R;
import seiko.neiko.dao.db.DbApi;
import seiko.neiko.models.Book;
import seiko.neiko.models.BookPage;
import seiko.neiko.viewModels.Section1ViewModel;

/**
 * Created by Seiko on 2016/12/22. Y
 * Section1翻页模式
 */

public class Section1FragmentP extends Section1FragmentBase {

    @BindView(R.id.recView)
    RecyclerView recView;

    private Section1ViewModel viewModel;
    private Section1FragmentPAdapter adapter;
    private boolean nowload;
    private LinearLayoutManager llm;

    @Override
    public int getLayoutId() {return R.layout.fragment_section1_page;}


    @Override
    public void initView() {
        setRecView();
        viewModel = new Section1ViewModel();
        getNodeViewModel(imgList.get(imgindex), imgindex, 0);
        setPtrHandler();
    }


    private void setRecView() {
        adapter = new Section1FragmentPAdapter(bookUrl);
        llm = new LinearLayoutManager(getContext());
        recView.setLayoutManager(llm);
        recView.setHasFixedSize(true);
        recView.setAdapter(adapter);
        section1View.onScroll(recView, llm);
    }

    //=================================================
    /** dtype1：加载更多 */
    private void setPtrHandler() {
        recView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (nowload) {
                        toast("加载中");
                        return;
                    }

                    int id = llm.findFirstVisibleItemPosition();

                    if (id == 0) {
                        top += t;
                        if (isBottom(imgindex + top)) {
                            nowload = false;
                            return;
                        }
                        toast("加载上一章");
                        getNodeViewModel(imgList.get(imgindex+top), imgindex+top, 1);
                    } else if (id == adapter.getItemCount() - 1) {
                        bot += -t;
                        if (isBottom(imgindex + bot)) {
                            nowload = false;
                            return;
                        }
                        toast("加载下一章");
                        getNodeViewModel(imgList.get(imgindex+bot), imgindex+bot, -1);
                    }

                }
            }
        });
    }

    //=================================================
    /** 先尝试本地加载，再从网络中加载数据 */
    private void getNodeViewModel(Book book, int imgindex, int isprev) {
        nowload = true;
        List<Book> sectionList = readPath(book.getSection());
        if (sectionList != null) {
            addAadapter(sectionList, book, imgindex, isprev);
        } else {
            viewModel.clear();
            source.getNodeViewModel(viewModel, true, book.getSection_url(), source.section(book.getSection_url()), (code) -> {
                if (code == 1) {
                    addAadapter(viewModel.sectionList, book, imgindex, isprev);
                    activity.readOk();
                }
            });
        }
    }

    private void addAadapter(List<Book> list, Book book, int imgindex, int isprev) {
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
                toast("加载完成");
                break;
            case -1:
                adapter.addAll(list);
                activity.bookPages.add(bookPage);
                toast("加载完成");
                break;
        }
        nowload = false;
    }
}
