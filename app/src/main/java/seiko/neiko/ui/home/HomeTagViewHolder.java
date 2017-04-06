package seiko.neiko.ui.home;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import seiko.neiko.R;
import seiko.neiko.dao.mIntent;
import seiko.neiko.models.Book;
import zlc.season.practicalrecyclerview.AbstractViewHolder;

/**
 * Created by Seiko on 2016/12/20. Y
 */

class HomeTagViewHolder extends AbstractViewHolder<Book> {
    @BindView(R.id.book_sections1)
    TextView tv;

    private Context mContext;
    private Book book;

    HomeTagViewHolder(ViewGroup parent) {
        super(parent, R.layout.item_book_sections);
        ButterKnife.bind(this, itemView);
        this.mContext = parent.getContext();
    }

    @Override
    public void setData(Book book) {
        this.book = book;
        tv.setText(book.getSection());
    }

    @OnClick(R.id.layout)
    void OnClick() {
        mIntent.Intent_Home_Tag(mContext, book);
    }

}
