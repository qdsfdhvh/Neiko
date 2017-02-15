package seiko.neiko.ui.sited;

import android.view.ViewGroup;

import seiko.neiko.models.SourceModel;
import zlc.season.practicalrecyclerview.AbstractAdapter;

/**
 * Created by Seiko on 2017/2/13. Y
 */

class SiteDAdapter extends AbstractAdapter<SourceModel, SiteDViewHolder> {
    @Override
    protected SiteDViewHolder onNewCreateViewHolder(ViewGroup parent, int viewType) {
        return new SiteDViewHolder(parent, this);
    }

    @Override
    protected void onNewBindViewHolder(SiteDViewHolder holder, int position) {
        holder.setData(get(position));
    }
}
