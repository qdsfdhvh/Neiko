package seiko.neiko.ui.down;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lid.lib.LabelImageView;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import seiko.neiko.R;
import seiko.neiko.dao.SourceApi;
import seiko.neiko.dao.db.DownDbApi;
import seiko.neiko.glide.ImageLoader;
import seiko.neiko.models.DownBookBean;
import seiko.neiko.ui.book.AnimeBookActivity;
import seiko.neiko.ui.book.BookModel;
import seiko.neiko.utils.FileUtil;
import zlc.season.practicalrecyclerview.AbstractViewHolder;

import static seiko.neiko.dao.mPath.getBookDownPath;
import static seiko.neiko.dao.engine.DdApi.setLabelText;

/**
 * Created by Seiko on 2016/11/21. YiKu
 */

class Download1ViewHolder extends AbstractViewHolder<DownBookBean> {

    @BindView(R.id.img)
    LabelImageView mImg;
    @BindView(R.id.title)
    TextView mtitle;
    @BindView(R.id.progress)
    ProgressBar mProgress;
    @BindView(R.id.size)
    TextView mSize;
    @BindView(R.id.status)
    Button mStatus;

    private Download1Adapter mAdapter;
    private Context mContext;
    private DownBookBean data;
    private String path;

    Download1ViewHolder(ViewGroup parent, Download1Adapter adapter) {
        super(parent, R.layout.item_down1);
        ButterKnife.bind(this, itemView);

        this.mAdapter = adapter;
        mContext = parent.getContext();
    }

    @Override
    public void setData(DownBookBean param) {
        this.data = param;

        //进度很状态需要重新读取
        int progress = DownDbApi.getDownedQueueProgress(data.getBkey());
        data.setProgress(progress);
        int state = DownDbApi.getDownedQueueState(data.getBkey());
        data.setState(state);
        int total = DownDbApi.getDownedQueueTotal(data.getBkey());
        data.setTotal(total);

        //封面
        ImageLoader.getDefault().display(mContext, mImg, data.getImage(), data.getTitle(), data.getUrl());
        setLabelText(data.getSource(), data.getDtype(), mImg);
        //漫画名
        mtitle.setText(data.getTitle());
        //进度条
        mProgress.setMax(data.getTotal());
        mProgress.setProgress(data.getProgress());
        //例：5/10
        mSize.setText(data.getProgress() + "/" + data.getTotal());

        mStatus.setText("暂时关闭");
        mStatus.setClickable(false);

        path = getBookDownPath(data.getSource(), data.getTitle());

        if (data.getState() == DownBookBean.DONE) {

//            String appDir = FileUtil.getRootPath() + data.getSource() + "/" + data.getTitle().replaceAll("/", "_");
            File file = new File(path);
            //下载完成，但是路径不存在
            if (!file.exists()) {
                itemView.setClickable(false);
                mStatus.setText("已删除");
                return;
            }
            mStatus.setText("已完成");
        }
    }



    @OnClick(R.id.status)
    void onClick() {
        switch (data.getState()) {
            case DownBookBean.START:
                break;
            case DownBookBean.PAUSE:
                break;
        }
    }

    @OnLongClick(R.id.layout)
    boolean onLongClick() {
        new AlertDialog.Builder(mContext)
                .setMessage("是否删除漫画："+ data.getTitle())
                .setNegativeButton("是", (DialogInterface dif, int j) -> delDown(data.getBkey()))   //通知中间按钮
                .setPositiveButton("否", (DialogInterface dif, int j) -> dif.dismiss() )      //通知最右按钮
                .setNeutralButton("初始化", (DialogInterface dif, int j) -> {
                    DownDbApi.setDownedQueueState(data.getBkey(), DownBookBean.START);
                    setDown(data);
                })  //通知最左按钮
                .create()
                .show();
        return true;
    }

    //初始化
    private void setDown(DownBookBean data) {
        data.setProgress(0);
        data.setState(DownBookBean.START);
        mStatus.setText("开始");
        mProgress.setProgress(0);
        mSize.setText(data.getProgress() + "/" + data.getTotal());
    }

    //删除下载
    private void delDown(String bkey) {
        DownDbApi.delDownedQueue(bkey);
        DownDbApi.delDownedSections(bkey);
        mAdapter.remove(getAdapterPosition());

        new AlertDialog.Builder(mContext)
                .setMessage("是否删除本地文件")
                .setNegativeButton("是", (DialogInterface dif, int j) -> {
                    FileUtil.deleteFile(path);
                })   //通知中间按钮
                .setPositiveButton("否", (DialogInterface dif, int j) -> dif.dismiss())      //通知最右按钮
                .create()
                .show();
    }

    //=============================================
    @OnClick(R.id.img)
    void intentBook() {
        if (SourceApi.getDefault().readSited(mContext, data.getSource())) {
            AnimeBookActivity.m = new BookModel(-1, data.getUrl(), data.getImage());
            Intent intent = new Intent(mContext, AnimeBookActivity.class);
            mContext.startActivity(intent);
        }
    }

    @OnClick(R.id.layout)
    void intentDownSection() {
        if (SourceApi.getDefault().readSited(mContext, data.getSource())) {
            Download2Activity.data = data;
            Intent intent = new Intent(mContext, Download2Activity.class);
            mContext.startActivity(intent);
        }
    }
}
