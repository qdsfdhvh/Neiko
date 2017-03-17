package seiko.neiko.ui.search;

import android.view.ViewGroup;

import seiko.neiko.dao.engine.DdSource;
import seiko.neiko.models.Book;
import seiko.neiko.ui.book.BookModel;
import zlc.season.practicalrecyclerview.AbstractAdapter;

/**
 * Created by Seiko on 2016/12/21. Y
 */

class SearchAdapter extends AbstractAdapter<BookModel, SearchViewHolder> {

    @Override
    protected SearchViewHolder onNewCreateViewHolder(ViewGroup parent, int viewType) {
        return new SearchViewHolder(parent);
    }

    @Override
    protected void onNewBindViewHolder(SearchViewHolder holder, int position) {
        holder.setData(get(position));
    }

}
