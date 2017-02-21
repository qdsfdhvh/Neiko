package seiko.neiko.ui.section1;

import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import seiko.neiko.models.Book;
import seiko.neiko.models.SizeModel;
import zlc.season.practicalrecyclerview.AbstractAdapter;

/**
 * Created by Seiko on 2016/12/26. Y
 */

class Section1FragmentSAdapter extends AbstractAdapter<Book, Section1FragmentSViewHolder> {

    private String refererUrl;

    Section1FragmentSAdapter(String refererUrl) {
        this.refererUrl = refererUrl;
    }

    String getRefererUrl() {return refererUrl;}

    //=====================================
    @Override
    protected Section1FragmentSViewHolder onNewCreateViewHolder(ViewGroup parent, int viewType) {
        return new Section1FragmentSViewHolder(parent, this);
    }

    @Override
    protected void onNewBindViewHolder(Section1FragmentSViewHolder holder, int position) {
        holder.setData(get(position));
    }

}
