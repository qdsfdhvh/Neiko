package seiko.neiko.ui.book;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import seiko.neiko.R;
import seiko.neiko.dao.db.DbApi;
import seiko.neiko.dao.mIntent;
import seiko.neiko.models.Book;
import zlc.season.practicalrecyclerview.AbstractViewHolder;

/**
 * Created by Seiko on 2016/12/21. Y
 */

class BookViewHolder extends AbstractViewHolder<Book> {

    @BindView(R.id.book_sections1)
    TextView tv1;
    @BindView(R.id.book_sections2)
    TextView tv2;
    @BindView(R.id.sections_view1)
    CardView card;
    @BindView(R.id.sections_view2)
    View sections_view2;

    private Context mContext;
    private BookAdapter adapter;
    private Book book;
    private int i; //出问题了
    private String url;

    BookViewHolder(ViewGroup parent, BookAdapter adapter) {
        super(parent, R.layout.item_book_sections);
        ButterKnife.bind(this, itemView);
        this.mContext = parent.getContext();
        this.adapter = adapter;
    }

    @Override
    public void setData(Book book) {
        this.book = book;
        i = getAdapterPosition();
        url = book.getSection_url();
        String Sec = book.getSection().replaceAll("\n","");

        if (TextUtils.isEmpty(url)) {
            card.setVisibility(View.GONE);
            sections_view2.setVisibility(View.VISIBLE);
            tv2.setText(Sec);
            tv2.getPaint().setFakeBoldText(true);
        } else {
            sections_view2.setVisibility(View.GONE);
            card.setVisibility(View.VISIBLE);
            tv1.setText(Sec);
            tv1.getPaint().setFakeBoldText(false);

            if (url.equals(adapter.last_surl))
                card.setCardBackgroundColor(Color.parseColor("#1abc9c"));
            else
                card.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
        }
    }

    @OnClick(R.id.layout)
    void Intent() {
        if (!TextUtils.isEmpty(url)) {
            final int dtype = adapter.source.section(url).dtype();
            if (dtype == 3) {
                DbApi.setBookLastLook(adapter.bean.getBkey(), url, i); //视频在此保存记录
            } else {
                DbApi.setBookLastLook(adapter.bean.getBkey(), i);
            }
            adapter.setLast_surl(url);
            mIntent.Intent_Book(mContext, book, dtype, i, adapter.bean);
        }
    }
}
