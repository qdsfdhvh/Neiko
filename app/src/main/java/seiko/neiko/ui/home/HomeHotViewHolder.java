package seiko.neiko.ui.home;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import seiko.neiko.R;
import seiko.neiko.dao.mIntent;
import seiko.neiko.glide.ImageLoader;
import seiko.neiko.models.Book;
import seiko.neiko.widget.ScaleImageView;
import zlc.season.practicalrecyclerview.AbstractViewHolder;

/**
 * Created by Seiko on 2016/12/20. Y
 */

class HomeHotViewHolder extends AbstractViewHolder<Book> {
    @BindView(R.id.logo)
    ScaleImageView iv;
    @BindView(R.id.title)
    TextView tv;

    private Context mContext;
    private HomeHotAdapter adapter;
    private Book book;

    HomeHotViewHolder(ViewGroup parent, HomeHotAdapter adapter) {
        super(parent, R.layout.item_home_hot);
        ButterKnife.bind(this, itemView);
        this.mContext = parent.getContext();
        this.adapter = adapter;
    }

    @Override
    public void setData(Book book) {
        this.book = book;
        tv.setText(book.getName());
        ImageLoader.getDefault().display(mContext, iv, book.getLogo(), book.getName(), adapter.ref());
        iv.requestLayout();
    }

    @OnClick(R.id.layout)
    void OnClick() {
        int dtype = adapter.Dtype(book.getUrl());
        boolean isTag = adapter.isTag(book.getUrl());
        mIntent.Intent_Home_Hot(mContext, book, dtype, isTag);
    }

}
