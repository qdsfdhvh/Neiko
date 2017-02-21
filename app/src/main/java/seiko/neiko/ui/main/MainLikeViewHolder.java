package seiko.neiko.ui.main;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import seiko.neiko.R;
import seiko.neiko.dao.mIntent;
import seiko.neiko.dao.db.DbApi;
import seiko.neiko.glide.ImageLoader;
import seiko.neiko.models.Book;
import seiko.neiko.utils.HintUtil;
import seiko.neiko.widget.ScaleImageView;
import zlc.season.practicalrecyclerview.AbstractViewHolder;

import static seiko.neiko.dao.engine.DdApi.setLabelText;

/**
 * Created by Seiko on 2016/12/20. Y
 */

class MainLikeViewHolder extends AbstractViewHolder<Book> {
    @BindView(R.id.logo)
    ScaleImageView iv;
    @BindView(R.id.title)
    TextView tv;

    private Context mContext;
    private MainLikeAdapter adapter;
    private Book book;

    MainLikeViewHolder(ViewGroup parent, MainLikeAdapter adapter) {
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
        if (book.isNew()) {
            iv.setLabelText("new");
            iv.setLabelBackgroundColor(Color.parseColor("#F44336"));
        } else {
            setLabelText(book.getSource(), book.getType(), iv);
        }
    }

    @OnClick(R.id.layout)
    void OnClick() {
        mIntent.Intent_Main_Book(mContext, book);

        if (book.isNew()) {
            book.setNew(false);
            adapter.notifyItemChanged(getAdapterPosition());
        }
    }

    @OnLongClick(R.id.layout)
    boolean OnLongClick() {
        new AlertDialog.Builder(mContext)
                .setMessage("是否删除：" + book.getName())
                .setNegativeButton("是", (DialogInterface dif, int j) -> del_like())      //通知中间按钮
                .setPositiveButton("否", (DialogInterface dif, int j) -> dif.dismiss())   //通知最右按钮
                .create()
                .show();
        return true;
    }

    private void del_like() {
        DbApi.delLike(book.getBkey());               //数据库中删除漫画
        HintUtil.show(book.getName() + "：已删除");   //通知
        adapter.remove(getAdapterPosition());        //删除界面和list中的插件数据
    }
}
