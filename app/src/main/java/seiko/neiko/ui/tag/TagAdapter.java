package seiko.neiko.ui.tag;

import android.view.ViewGroup;

import seiko.neiko.dao.engine.DdSource;
import seiko.neiko.models.Book;
import zlc.season.practicalrecyclerview.AbstractAdapter;

/**
 * Created by Seiko on 2016/12/20. Y
 */

class TagAdapter extends AbstractAdapter<Book, TagViewHolder> {
    DdSource source;

    TagAdapter(DdSource source) {this.source = source;}

    @Override
    protected TagViewHolder onNewCreateViewHolder(ViewGroup parent, int viewType) {
        return new TagViewHolder(parent, this);
    }

    @Override
    protected void onNewBindViewHolder(TagViewHolder holder, int position) {
        holder.setData(get(position));
    }
}
