package seiko.neiko.ui.book;

import android.view.ViewGroup;

import seiko.neiko.models.DownSectionBean;
import zlc.season.practicalrecyclerview.AbstractAdapter;

/**
 * Created by Seiko on 2016/11/22. YiKu
 */

class DialogDownAdapter extends AbstractAdapter<DownSectionBean, DialogDownHolder> {
    @Override
    protected DialogDownHolder onNewCreateViewHolder(ViewGroup parent, int viewType) {
        return new DialogDownHolder(parent);
    }

    @Override
    protected void onNewBindViewHolder(DialogDownHolder holder, int position) {
        holder.setData(get(position));
    }
}
