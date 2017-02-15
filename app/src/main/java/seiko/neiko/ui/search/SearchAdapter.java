package seiko.neiko.ui.search;

import android.view.ViewGroup;

import seiko.neiko.dao.engine.DdSource;
import seiko.neiko.models.Book;
import zlc.season.practicalrecyclerview.AbstractAdapter;

/**
 * Created by Seiko on 2016/12/21. Y
 */

class SearchAdapter extends AbstractAdapter<Book, SearchViewHolder> {

    DdSource source;

    SearchAdapter(DdSource source) {this.source = source;}

    @Override
    protected SearchViewHolder onNewCreateViewHolder(ViewGroup parent, int viewType) {
        return new SearchViewHolder(parent, this);
    }

    @Override
    protected void onNewBindViewHolder(SearchViewHolder holder, int position) {
        holder.setData(get(position));
    }

}
