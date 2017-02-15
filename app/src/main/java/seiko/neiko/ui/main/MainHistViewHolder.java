package seiko.neiko.ui.main;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lid.lib.LabelImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import seiko.neiko.R;
import seiko.neiko.dao.mIntent;
import seiko.neiko.dao.db.DbApi;
import seiko.neiko.dao.ImageLoader;
import seiko.neiko.models.Book;
import seiko.neiko.utils.HintUtil;
import zlc.season.practicalrecyclerview.AbstractViewHolder;

import static seiko.neiko.dao.engine.DdApi.setLabelText;

/**
 * Created by Seiko on 2016/12/20. Y
 */

class MainHistViewHolder extends AbstractViewHolder<Book> {

    @BindView(R.id.logo)
    LabelImageView iv;
    @BindView(R.id.title)
    TextView tv;

    private Context mContext;
    private MainHistAdapter adapter;
    private Book book;

    MainHistViewHolder(ViewGroup parent, MainHistAdapter adapter) {
        super(parent, R.layout.item_main);
        ButterKnife.bind(this, itemView);
        this.mContext = parent.getContext();
        this.adapter = adapter;
    }

    @Override
    public void setData(Book book) {
        this.book = book;
        tv.setText(book.getName());
        ImageLoader.getDefault().display(mContext, iv, book.getLogo(), book.getName(), book.getUrl());
        setLabelText(book.getSource(), book.getType(), iv);
    }

    @OnClick(R.id.layout)
    void OnClick() {
        mIntent.Intent_Main_Book(mContext, book);
    }

    @OnLongClick(R.id.layout)
    boolean OnLongClick() {
        new AlertDialog.Builder(mContext)
                .setMessage("是否删除：" + book.getName())
                .setNegativeButton("是", (DialogInterface dif, int j)-> del_hist())      //通知中间按钮
                .setPositiveButton("否", (DialogInterface dif, int j) -> dif.dismiss())        //通知最右按钮
                .setNeutralButton("清空历史", (DialogInterface dif, int j) -> del_hist_all())  //通知最左按钮
                .create()
                .show();
        return true;
    }

    /* 删除数据 */
    private void del_hist() {
        DbApi.delHistory(book.getBkey());            //数据库中删除漫画
        HintUtil.show(book.getName() + "：已删除");  //通知
        Log.d("aaa", getAdapterPosition()+"");
        adapter.remove(getAdapterPosition());    //删除界面和list中的插件数据
    }

    /* 删除数据All */
    private void del_hist_all() {
        DbApi.delAllHistory();
        HintUtil.show("历史已清空");
        adapter.clear();
        adapter.notifyDataSetChanged();
    }
}
