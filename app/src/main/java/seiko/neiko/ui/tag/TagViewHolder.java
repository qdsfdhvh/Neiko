package seiko.neiko.ui.tag;

import android.content.Context;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import seiko.neiko.R;
import seiko.neiko.dao.mIntent;
import seiko.neiko.glide.ImageLoader;
import seiko.neiko.models.Book;
import zlc.season.practicalrecyclerview.AbstractViewHolder;

/**
 * Created by Seiko on 2016/12/20. Y
 */

class TagViewHolder extends AbstractViewHolder<Book> {
    @BindView(R.id.book_logo)
    ImageView iv;
    @BindView(R.id.book_name)
    TextView tv;
    @BindView(R.id.book_author)
    TextView au;

    private Context mContext;
    private TagAdapter adapter;
    private Book book;

    TagViewHolder(ViewGroup parent, TagAdapter adapter) {
        super(parent, R.layout.item_tag);
        ButterKnife.bind(this, itemView);
        this.mContext = parent.getContext();
        this.adapter = adapter;
    }

    @Override
    public void setData(Book book) {
        this.book = book;
        String name = book.getName();
        if (TextUtils.isEmpty(name))
            name = " ";

        tv.setText(name);
        au.setText(book.getAuthor());

        ImageLoader.getDefault().display(mContext, iv, book.getLogo(), name, book.getUrl());
    }

    @OnClick(R.id.layout)
    void OnClick() {
        final int dtype = adapter.source.book(book.getUrl()).dtype();
        mIntent.Intent_Tag(mContext, book, dtype);
    }

}
