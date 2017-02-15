package seiko.neiko.ui.section1;

import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import seiko.neiko.R;
import seiko.neiko.dao.ImageLoader;
import seiko.neiko.models.Book;
import seiko.neiko.models.SizeModel;
import seiko.neiko.widget.ScaleImageView;
import zlc.season.practicalrecyclerview.AbstractViewHolder;

/**
 * Created by Seiko on 2017/1/22. Y
 */

class Section1FragmentSViewHolder extends AbstractViewHolder<Book> {

    @BindView(R.id.section_views)
    ScaleImageView iv;

    private Section1FragmentSAdapter adapter;

    Section1FragmentSViewHolder(ViewGroup parent, Section1FragmentSAdapter adapter) {
        super(parent, R.layout.item_section1_stream);
        ButterKnife.bind(this, itemView);
        this.adapter = adapter;
    }

    @Override
    public void setData(Book book) {
        ImageLoader.getDefault().display9(itemView, iv, get(book), adapter.getRefererUrl());
    }

    private String get(Book book) {
        String url;
        String path = book.getPath();
        if (path != null && new File(path).exists()) {
            url = path;
            Log.d("AdapterSection1", path);
        } else {
            url = book.getSection_url();
        }
        return url;
    }
}
