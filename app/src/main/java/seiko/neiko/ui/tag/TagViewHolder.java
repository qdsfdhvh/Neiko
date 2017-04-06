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
import seiko.neiko.ui.book.BookModel;
import zlc.season.practicalrecyclerview.AbstractViewHolder;

/**
 * Created by Seiko on 2016/12/20. Y
 */

class TagViewHolder extends AbstractViewHolder<BookModel> {
    @BindView(R.id.book_logo)
    ImageView iv;
    @BindView(R.id.book_name)
    TextView tv;
    @BindView(R.id.book_author)
    TextView au;

    private Context mContext;
    private BookModel book;

    TagViewHolder(ViewGroup parent) {
        super(parent, R.layout.item_tag);
        ButterKnife.bind(this, itemView);
        this.mContext = parent.getContext();
    }

    @Override
    public void setData(BookModel book) {
        this.book = book;
        String name = book.name;
        if (TextUtils.isEmpty(name))
            name = " ";

        tv.setText(name);
        au.setText(book.author);
        ImageLoader.getDefault().display(mContext, iv, book.logo, name, book.url);
    }

    @OnClick(R.id.layout)
    void OnClick() {
        mIntent.Intent_Tag(mContext, book);
    }

}
