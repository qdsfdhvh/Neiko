package seiko.neiko.ui.search;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lid.lib.LabelImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import seiko.neiko.R;
import seiko.neiko.dao.SourceApi;
import seiko.neiko.dao.engine.DdSource;
import seiko.neiko.dao.mIntent;
import seiko.neiko.dao.ImageLoader;
import seiko.neiko.models.Book;
import seiko.neiko.utils.HintUtil;
import zlc.season.practicalrecyclerview.AbstractViewHolder;

import static seiko.neiko.dao.engine.DdApi.setLabelText;

/**
 * Created by Seiko on 2016/12/21. Y
 */

class SearchViewHolder extends AbstractViewHolder<Book> {
    @BindView(R.id.logo)
    LabelImageView iv;
    @BindView(R.id.title)
    TextView tv;

    private Context mContext;
    private Book book;
    private SearchAdapter adapter;

    SearchViewHolder(ViewGroup parent, SearchAdapter adapter) {
        super(parent, R.layout.item_main);
        ButterKnife.bind(this, itemView);
        this.mContext = parent.getContext();
        this.adapter = adapter;
    }

    @Override
    public void setData(Book book) {
        this.book = book;
        tv.setText(book.getName());
        ImageLoader.getDefault().display(mContext, iv, book.getLogo(), book.getName());
        setLabelText(book.getSource(), book.getType(), iv);
    }

    @OnClick(R.id.layout)
    void OnClick() {
        DdSource source = SourceApi.getDefault().getByUrl(book.getUrl());
        if (source != null) {
            int dtype = source.book(book.getUrl()).dtype();
            mIntent.Intent_Tag(mContext, book, dtype);
        } else if (adapter.source != null) {
            int dtype = adapter.source.getBody().dtype();
            mIntent.Intent_Tag(mContext, book, dtype);
        } else {
            HintUtil.show("未获得相关插件参数");
        }
    }
}
