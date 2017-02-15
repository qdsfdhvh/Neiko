package seiko.neiko.ui.section1;

import android.view.ViewGroup;

import seiko.neiko.models.Book;
import zlc.season.practicalrecyclerview.AbstractAdapter;

/**
 * Created by Seiko on 2017/1/23. Y
 */

class Section1FragmentPAdapter extends AbstractAdapter<Book, Section1FragmentPViewHolder> {

    private String refererUrl;

    Section1FragmentPAdapter(String refererUrl) {this.refererUrl = refererUrl;}

    String getRefererUrl() {return refererUrl;}

    @Override
    protected Section1FragmentPViewHolder onNewCreateViewHolder(ViewGroup parent, int viewType) {
        return new Section1FragmentPViewHolder(parent, this);
    }

    @Override
    protected void onNewBindViewHolder(Section1FragmentPViewHolder holder, int position) {
        holder.setData(get(position));
    }
}
