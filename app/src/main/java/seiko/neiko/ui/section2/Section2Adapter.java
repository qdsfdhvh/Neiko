package seiko.neiko.ui.section2;

import android.view.ViewGroup;

import seiko.neiko.models.TxtModel;
import seiko.neiko.view.Section2View;
import zlc.season.practicalrecyclerview.AbstractAdapter;

/**
 * Created by Seiko on 2017/1/22. Y
 */

class Section2Adapter extends AbstractAdapter<TxtModel, Section2ViewHolder> {

    int textSize;
    int dtype;
    int textColor;

    @Override
    protected Section2ViewHolder onNewCreateViewHolder(ViewGroup parent, int viewType) {
        return new Section2ViewHolder(parent, this);
    }

    @Override
    protected void onNewBindViewHolder(Section2ViewHolder holder, int position) {
        holder.setData(get(position));
    }

    //===============================
    /** 开放加载章节按钮 */
    private Section2View mbtClick;

    Section2View getMbtClick() {return mbtClick;}

    void setbtClickListener(Section2View listener) {this.mbtClick = listener;}

}
