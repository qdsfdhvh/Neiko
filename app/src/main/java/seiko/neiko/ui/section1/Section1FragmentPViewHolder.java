package seiko.neiko.ui.section1;

import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import seiko.neiko.R;
import seiko.neiko.glide.ImageLoader;
import seiko.neiko.models.Book;
import seiko.neiko.models.ImgUrlBean;
import zlc.season.practicalrecyclerview.AbstractViewHolder;

/**
 * Created by Seiko on 2017/1/23. Y
 */

class Section1FragmentPViewHolder extends AbstractViewHolder<ImgUrlBean> {
    @BindView(R.id.section_views)
    ImageView iv;

    private Section1FragmentPAdapter adapter;

    Section1FragmentPViewHolder(ViewGroup parent, Section1FragmentPAdapter adapter) {
        super(parent, R.layout.item_section1_page);
        ButterKnife.bind(this, itemView);
        this.adapter = adapter;
    }

    @Override
    public void setData(ImgUrlBean bean) {
        ImageLoader.getDefault().display6(itemView, iv, get(bean), adapter.ref());
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
