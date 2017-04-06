package seiko.neiko.ui.section1;

import android.util.Log;
import android.view.ViewGroup;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import seiko.neiko.R;
import seiko.neiko.glide.ImageLoader;
import seiko.neiko.models.Book;
import seiko.neiko.models.ImgUrlBean;
import seiko.neiko.widget.ScaleImageView;
import zlc.season.practicalrecyclerview.AbstractViewHolder;

/**
 * Created by Seiko on 2017/1/22. Y
 */

class Section1FragmentSViewHolder extends AbstractViewHolder<ImgUrlBean> {

    @BindView(R.id.section_views)
    ScaleImageView iv;

    private Section1FragmentSAdapter adapter;

    Section1FragmentSViewHolder(ViewGroup parent, Section1FragmentSAdapter adapter) {
        super(parent, R.layout.item_section1_stream);
        ButterKnife.bind(this, itemView);
        this.adapter = adapter;
    }

    @Override
    public void setData(ImgUrlBean bean) {
        ImageLoader.getDefault().display9(itemView, iv, get(bean), adapter.ref());
    }

    private String get(ImgUrlBean bean) {
        String url;
        String path = bean.getPath();
        if (path != null && new File(path).exists()) {
            url = path;
            Log.d("AdapterSection1", path);
        } else {
            url = bean.getUrl();
        }
        return url;
    }
}
