package seiko.neiko.ui.search;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import seiko.neiko.R;
import seiko.neiko.dao.mIntent;
import seiko.neiko.glide.ImageLoader;
import seiko.neiko.ui.book.BookModel;
import seiko.neiko.widget.ScaleImageView;
import zlc.season.practicalrecyclerview.AbstractViewHolder;

import static seiko.neiko.dao.engine.DdApi.setLabelText;

/**
 * Created by Seiko on 2016/12/21. Y
 */

class SearchViewHolder extends AbstractViewHolder<BookModel> {
    @BindView(R.id.logo)
    ScaleImageView iv;
    @BindView(R.id.title)
    TextView tv;

    private Context mContext;
    private BookModel book;

    SearchViewHolder(ViewGroup parent) {
        super(parent, R.layout.item_home_hot);
        ButterKnife.bind(this, itemView);
        this.mContext = parent.getContext();
    }

    @Override
    public void setData(BookModel book) {
        this.book = book;
        tv.setText(book.name);
        ImageLoader.getDefault().display(mContext, iv, book.logo, book.name);
        setLabelText(book.source, book.dtype, iv);
    }

    @OnClick(R.id.layout)
    void OnClick() {
        mIntent.Intent_Tag(mContext, book);
    }
}
