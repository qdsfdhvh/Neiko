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
import zlc.season.practicalrecyclerview.AbstractViewHolder;

/**
 * Created by Seiko on 2017/1/23. Y
 */

class Section1FragmentPViewHolder extends AbstractViewHolder<Book> {
    @BindView(R.id.section_views)
    ImageView iv;

    private Section1FragmentPAdapter adapter;

    Section1FragmentPViewHolder(ViewGroup parent, Section1FragmentPAdapter adapter) {
        super(parent, R.layout.item_section1_page);
        ButterKnife.bind(this, itemView);
        this.adapter = adapter;
    }

    @Override
    public void setData(Book book) {
        ImageLoader.getDefault().display(itemView.getContext(), iv, get(book), adapter.getRefererUrl());
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
