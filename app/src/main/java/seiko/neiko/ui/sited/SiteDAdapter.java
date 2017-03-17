package seiko.neiko.ui.sited;

import android.view.ViewGroup;

import seiko.neiko.models.SourceModel;
import zlc.season.practicalrecyclerview.AbstractAdapter;

/**
 * Created by Seiko on 2017/2/13. Y
 */

class SitedAdapter extends AbstractAdapter<SourceModel, SitedViewHolder> {
    @Override
    protected SitedViewHolder onNewCreateViewHolder(ViewGroup parent, int viewType) {
        return new SitedViewHolder(parent, this);
    }

    @Override
    protected void onNewBindViewHolder(SitedViewHolder holder, int position) {
        holder.setData(get(position));
    }
}
