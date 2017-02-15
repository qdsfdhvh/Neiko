package seiko.neiko.ui.home;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import seiko.neiko.dao.engine.DdSource;
import seiko.neiko.models.Book;
import zlc.season.practicalrecyclerview.AbstractAdapter;

/**
 * Created by Seiko on 2016/12/20. Y
 */

class HomeHotAdapter extends AbstractAdapter<Book, HomeHotViewHolder> {

    DdSource source;
    String refererUrl;

    HomeHotAdapter(DdSource source, String refererUrl) {
        this.source = source;
        this.refererUrl = refererUrl;
    }

    @Override
    protected HomeHotViewHolder onNewCreateViewHolder(ViewGroup parent, int viewType) {
        return new HomeHotViewHolder(parent, this);
    }

    @Override
    protected void onNewBindViewHolder(HomeHotViewHolder holder, int position) {
        holder.setData(get(position));
    }

}
