package seiko.neiko.ui.sited;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.TextView;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import seiko.neiko.R;
import seiko.neiko.dao.mPath;
import seiko.neiko.models.SourceModel;
import seiko.neiko.utils.FileUtil;
import zlc.season.practicalrecyclerview.AbstractViewHolder;

/**
 * Created by Seiko on 2017/2/13. Y
 */

class SiteDViewHolder extends AbstractViewHolder<SourceModel> {

    @BindView(R.id.title)
    TextView tv;

    private SourceModel m;
    private Context mContext;
    private SiteDAdapter adapter;

    SiteDViewHolder(ViewGroup parent, SiteDAdapter adapter) {
        super(parent, R.layout.item_sited);
        ButterKnife.bind(this, itemView);
        this.adapter = adapter;
        mContext = parent.getContext();
    }

    @Override
    public void setData(SourceModel m) {
        this.m = m;
        tv.setText(m.title);
    }

    @OnClick(R.id.delete)
    void delete() {
        new AlertDialog.Builder(mContext)
                .setMessage("是否删除：" + m.title)
                .setNegativeButton("是", (DialogInterface dif, int j) -> deleteSited())      //通知中间按钮
                .setPositiveButton("否", (DialogInterface dif, int j) -> dif.dismiss())      //通知最右按钮
                .create()
                .show();
    }

    private void deleteSited() {
        adapter.remove(getAdapterPosition());
        String path = mPath.getSitedPath(m.title);
        if (!TextUtils.isEmpty(path)) {
            FileUtil.deleteFile(path);
        }
    }
}
