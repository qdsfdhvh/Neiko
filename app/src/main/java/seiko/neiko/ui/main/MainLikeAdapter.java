package seiko.neiko.ui.main;

import android.view.ViewGroup;

import seiko.neiko.models.Book;
import zlc.season.practicalrecyclerview.AbstractAdapter;

/**
 * Created by Seiko on 2016/12/20. Y
 */

class MainLikeAdapter extends AbstractAdapter<Book, MainLikeViewHolder> {

    @Override
    protected MainLikeViewHolder onNewCreateViewHolder(ViewGroup parent, int viewType) {
        return new MainLikeViewHolder(parent, this);
    }

    @Override
    protected void onNewBindViewHolder(MainLikeViewHolder holder, int position) {
        holder.setData(get(position));
    }
}
