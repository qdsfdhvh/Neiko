package seiko.neiko.ui.main;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import butterknife.BindView;
import butterknife.ButterKnife;
import seiko.neiko.R;
import seiko.neiko.dao.mIntent;
import seiko.neiko.dao.mPath;
import seiko.neiko.models.SourceModel;
import seiko.neiko.utils.ColorUtil;
import seiko.neiko.utils.FileUtil;
import seiko.neiko.utils.HintUtil;
import zlc.season.practicalrecyclerview.AbstractViewHolder;

/**
 * Created by Seiko on 2016/12/20. Y
 */

class MainSiteDViewHolder extends AbstractViewHolder<SourceModel> implements ShowView {
    @BindView(R.id.layout)
    CardView card;
    @BindView(R.id.title)
    TextView tv;
    @BindView(R.id.status)
    AppCompatCheckBox check;

    private Context mContext;
    private MainSiteDAdapter adapter;
    private SourceModel model;

    MainSiteDViewHolder(ViewGroup parent, MainSiteDAdapter adapter) {
        super(parent, R.layout.item_main_sited);
        ButterKnife.bind(this, itemView);
        this.mContext = parent.getContext();
        this.adapter = adapter;
        adapter.getFragment().addShowView(this);
    }

    @Override
    public void setData(SourceModel m) {
        this.model = m;
        tv.setText(model.title);
        if (!TextUtils.isEmpty(model.title)) {
            int color = ColorUtil.MATERIAL.getColor(model.title);
            card.setCardBackgroundColor(color);
            card.setRadius(5);
        }

        RxView.clicks(card).subscribe((Void aVoid) -> {
            if (!adapter.isDelete()) {
                mIntent.Intent_Main_Home(mContext, model);
            } else {
                new AlertDialog.Builder(mContext)
                        .setMessage("是否删除插件："+ model.title)
                        .setNegativeButton("是", (DialogInterface dif, int j) -> {
                            del_home();
                            HintUtil.show(model.title + "：已删除");  //通知
                        })       //通知中间按钮
                        .setPositiveButton("否", (DialogInterface dif, int j) -> dif.dismiss())    //通知最右按钮
                        .create()
                        .show();
            }
        });
    }

    /* 显示选择框 */
    @Override
    public void ShowCheck(boolean isShow) {
        if (isShow) {
            check.setVisibility(View.VISIBLE);
        } else {
            check.setVisibility(View.GONE);
            if (check.isChecked()) {
                del_home();
                check.setChecked(false);
            }
        }
    }

    /* 删除数据 */
    private void del_home() {
//        SiteDbApi.delSource(model.key);              //数据库中删除插件
        adapter.remove(getAdapterPosition());        //删除界面和list中的插件数据
        String path = mPath.getSitedPath(model.title);
        if (!TextUtils.isEmpty(path)) {
            FileUtil.deleteFile(path);
        }
    }
}
