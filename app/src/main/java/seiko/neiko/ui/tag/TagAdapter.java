package seiko.neiko.ui.tag;

import android.view.ViewGroup;

import seiko.neiko.dao.engine.DdSource;
import seiko.neiko.ui.book.BookModel;
import zlc.season.practicalrecyclerview.AbstractAdapter;

/**
 * Created by Seiko on 2016/12/20. Y
 */

class TagAdapter extends AbstractAdapter<BookModel, TagViewHolder> {

    @Override
    protected TagViewHolder onNewCreateViewHolder(ViewGroup parent, int viewType) {
        return new TagViewHolder(parent, this);
    }

    @Override
    protected void onNewBindViewHolder(TagViewHolder holder, int position) {
        holder.setData(get(position));
    }
}
