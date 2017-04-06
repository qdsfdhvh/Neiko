package seiko.neiko.ui.main;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import seiko.neiko.R;
import seiko.neiko.dao.db.DbApi;
import seiko.neiko.models.Book;
import seiko.neiko.rx.RxEvent;
import seiko.neiko.app.BaseFragment;
import zlc.season.practicalrecyclerview.PracticalRecyclerView;

import static seiko.neiko.dao.mNum.HIST_NUMBER;

/**
 * Created by Seiko on 2016/11/9. YiKu
 */

public class MainHistFragment extends BaseFragment {

    @BindView(R.id.recView)
    PracticalRecyclerView recView;

    private MainHistAdapter adapter;

    @Override
    public int getLayoutId() {return R.layout.fragment_main_hist;}

    @Override
    public void initViews(Bundle bundle) {
        setRec();
        loadList();
        RxAndroid();
    }

    private void setRec() {
        adapter = new MainHistAdapter();
        GridLayoutManager glm = new GridLayoutManager(getContext(), HIST_NUMBER);
        recView.setLayoutManager(glm);
        recView.setHasFixedSize(true);
        recView.setAdapterWithLoading(adapter);
    }

    private void loadList() {
        Flowable.just(DbApi.getHistory())
                .subscribeOn(Schedulers.io())
                .delay(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Book>>() {
                    @Override
                    public void accept(List<Book> books) throws Exception {
                        adapter.addAll(books);
                    }
                });
    }

    private void RxAndroid() {
        //从book界面获得记录
        addSubscription(RxEvent.EVENT_MAIN_HIST, new Consumer<RxEvent>() {
            @Override
            public void accept(RxEvent event) throws Exception {
                Book book = (Book) event.getData();

                if (!TextUtils.isEmpty(book.getBkey())) {
                    int num = contain(adapter.getData(), book.getBkey());
                    if (num >= 0) {
                        adapter.remove(num);
                    }
                    if (adapter.getData().size() == 0) {
                        adapter.add(book);
                    } else {
                        adapter.insert(0, book);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }
}
