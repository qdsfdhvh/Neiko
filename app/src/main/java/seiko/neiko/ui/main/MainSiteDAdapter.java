package seiko.neiko.ui.main;

import android.view.ViewGroup;

import java.util.Collections;

import seiko.neiko.dao.db.SiteDbApi;
import seiko.neiko.dao.engine.DdSource;
import seiko.neiko.models.Book;
import seiko.neiko.models.SourceModel;
import seiko.neiko.widget.helper.ItemTouchHelperAdapter;
import zlc.season.practicalrecyclerview.AbstractAdapter;

/**
 * Created by Seiko on 2016/12/20. Y
 */

class MainSiteDAdapter extends AbstractAdapter<SourceModel, MainSiteDViewHolder> implements ItemTouchHelperAdapter {

    @Override
    protected MainSiteDViewHolder onNewCreateViewHolder(ViewGroup parent, int viewType) {
        return new MainSiteDViewHolder(parent, this);
    }

    @Override
    protected void onNewBindViewHolder(MainSiteDViewHolder holder, int position) {
        holder.setData(get(position));
    }

    //======================================
    /** 添加sited */
    void addSiteD(SourceModel m) {
        //判断是否已经包含
        for (SourceModel m1:getData()) {
            if (m1.title.contains(m.title))
                return;
        }
        add(m);
    }

    //======================================
    /** fragment相关 */
    private MainSiteDFragment fragment;

    MainSiteDAdapter(MainSiteDFragment fragment) {this.fragment = fragment;}

    MainSiteDFragment getFragment() {return fragment;}

    //======================================
    /** 是否显示选择框 */
    private boolean isDelete;

    void setDelete(boolean isdelete) {this.isDelete = isdelete;}

    boolean isDelete() {return isDelete;}

    //======================================
    /** 拖动相关处理 */
    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            //分别把中间所有的item的位置重新交换
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(getData(), i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(getData(), i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        getData().remove(position);
        notifyItemRemoved(position);
    }
}
