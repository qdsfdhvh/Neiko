package seiko.neiko.ui.book;

import android.view.ViewGroup;

import seiko.neiko.dao.engine.DdSource;
import seiko.neiko.models.Book;
import seiko.neiko.models.BookInfBean;
import zlc.season.practicalrecyclerview.AbstractAdapter;

/**
 * Created by Seiko on 2016/12/21. Y
 */

class BookAdapter extends AbstractAdapter<Book, BookViewHolder> {
    DdSource source;
    BookInfBean bean;
    String last_surl;

    @Override
    protected BookViewHolder onNewCreateViewHolder(ViewGroup parent, int viewType) {
        return new BookViewHolder(parent, this);
    }

    @Override
    protected void onNewBindViewHolder(BookViewHolder holder, int position) {
        holder.setData(get(position));
    }

    void setLast_surl(final String last_surl) {
        this.last_surl = last_surl;
        notifyDataSetChanged();
    }

    void setEvent(DdSource source, BookInfBean bean) {
        this.source = source;
        this.bean = bean;
    }

}
