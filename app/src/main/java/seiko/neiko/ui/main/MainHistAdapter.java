package seiko.neiko.ui.main;

import android.view.ViewGroup;

import seiko.neiko.models.Book;
import zlc.season.practicalrecyclerview.AbstractAdapter;

/**
 * Created by Seiko on 2016/12/20. Y
 */

class MainHistAdapter extends AbstractAdapter<Book, MainHistViewHolder> {

    @Override
    protected MainHistViewHolder onNewCreateViewHolder(ViewGroup parent, int viewType) {
        return new MainHistViewHolder(parent, this);
    }

    @Override
    protected void onNewBindViewHolder(MainHistViewHolder holder, int position) {
        holder.setData(get(position));
    }

}
