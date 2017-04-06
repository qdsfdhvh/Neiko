package seiko.neiko.ui.section1;

import android.view.ViewGroup;

import seiko.neiko.models.ImgUrlBean;
import zlc.season.practicalrecyclerview.AbstractAdapter;

/**
 * Created by Seiko on 2016/12/26. Y
 */

class Section1FragmentSAdapter extends AbstractAdapter<ImgUrlBean, Section1FragmentSViewHolder> {

    private String refererUrl;

    Section1FragmentSAdapter(String refererUrl) {
        this.refererUrl = refererUrl;
    }

    //=====================================
    @Override
    protected Section1FragmentSViewHolder onNewCreateViewHolder(ViewGroup parent, int viewType) {
        return new Section1FragmentSViewHolder(parent, this);
    }

    @Override
    protected void onNewBindViewHolder(Section1FragmentSViewHolder holder, int position) {
        holder.setData(get(position));
    }

    String ref() {return refererUrl;}

}
