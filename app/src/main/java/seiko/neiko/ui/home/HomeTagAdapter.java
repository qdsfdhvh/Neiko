package seiko.neiko.ui.home;

import android.view.ViewGroup;

import seiko.neiko.models.Book;
import zlc.season.practicalrecyclerview.AbstractAdapter;

/**
 * Created by Seiko on 2016/12/20. Y
 */

class HomeTagAdapter extends AbstractAdapter<Book, HomeTagViewHolder> {

    @Override
    protected HomeTagViewHolder onNewCreateViewHolder(ViewGroup parent, int viewType) {
        return new HomeTagViewHolder(parent);
    }

    @Override
    protected void onNewBindViewHolder(HomeTagViewHolder holder, int position) {
        holder.setData(get(position));
    }
}
