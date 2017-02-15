package seiko.neiko.ui.main;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;

import butterknife.BindView;
import seiko.neiko.R;
import seiko.neiko.dao.db.DbApi;
import seiko.neiko.models.Book;
import seiko.neiko.rx.RxEvent;
import seiko.neiko.app.FragmentBase;

import static seiko.neiko.dao.mNum.HIST_NUMBER;

/**
 * Created by Seiko on 2016/11/9. YiKu
 */

public class MainHistFragment extends FragmentBase {

    @BindView(R.id.recView)
    RecyclerView recView;

    private MainHistAdapter adapter;

    @Override
    public int getLayoutId() {return R.layout.fragment_main_hist;}

    /** 从数据库加载数据 */
    @Override
    public void initView() {
        adapter = new MainHistAdapter();
        recView.setLayoutManager(new StaggeredGridLayoutManager(HIST_NUMBER, StaggeredGridLayoutManager.VERTICAL));
        recView.setHasFixedSize(true);
        recView.setAdapter(adapter);
        adapter.addAll(DbApi.getHistory());
        RxAndroid();
    }

    //=====================================
    /** RxBus */
    private void RxAndroid() {
        //从book界面获得记录
        addSubscription(RxEvent.EVENT_MAIN_HIST, (RxEvent event) -> {
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
        });
    }
}
