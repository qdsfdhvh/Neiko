package seiko.neiko.ui.down;

import android.view.ViewGroup;

import seiko.neiko.models.DownBookBean;
import zlc.season.practicalrecyclerview.AbstractAdapter;

/**
 * Created by Seiko on 2016/11/21. YiKu
 */

class Download1Adapter extends AbstractAdapter<DownBookBean, Download1ViewHolder> {

    @Override
    protected Download1ViewHolder onNewCreateViewHolder(ViewGroup parent, int viewType) {
        return new Download1ViewHolder(parent, this);
    }

    @Override
    protected void onNewBindViewHolder(Download1ViewHolder holder, int position) {
        holder.setData(get(position));
    }
}
