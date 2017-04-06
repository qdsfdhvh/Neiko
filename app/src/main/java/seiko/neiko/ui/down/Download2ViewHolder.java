package seiko.neiko.ui.down;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import seiko.neiko.R;
import seiko.neiko.dao.db.DownDbApi;
import seiko.neiko.dao.mIntent;
import seiko.neiko.models.Book;
import seiko.neiko.rx.RxBus;
import seiko.neiko.rx.RxEvent;
import seiko.neiko.service.DownloadStatus;
import seiko.neiko.ui.section1.Section1FragmentBase;
import seiko.neiko.models.DownBookBean;
import seiko.neiko.models.DownSectionBean;
import seiko.neiko.utils.FileUtil;
import seiko.neiko.utils.HintUtil;
import seiko.neiko.viewModels.BookViewModel;
import zlc.season.practicalrecyclerview.AbstractViewHolder;

import static seiko.neiko.dao.mPath.getBookCachePath;
import static seiko.neiko.dao.mPath.getSectionBeanPath;
import static seiko.neiko.dao.mPath.getSectionDownPath;

/**
 * Created by Seiko on 2016/11/23. YiKu
 */

class Download2ViewHolder extends AbstractViewHolder<DownSectionBean> {

    @BindView(R.id.title)
    TextView mtitle;
    @BindView(R.id.size)
    TextView mSize;
    @BindView(R.id.progress)
    ProgressBar mProgress;
    @BindView(R.id.callback)
    TextView mCallback;
    @BindView(R.id.status)
    Button mStatus;

    private Download2Adapter mAdapter;
    private DownSectionBean mData;
    private Context mContext;

    private String path;      //保存路径

    Download2ViewHolder(ViewGroup parent, Download2Adapter adapter) {
        super(parent, R.layout.item_down2);
        ButterKnife.bind(this,itemView);
        this.mAdapter = adapter;
        this.mContext = parent.getContext();
    }

    @Override
    public void setData(DownSectionBean data) {
        this.mData = data;
        //初始化
        loadData();
        //加载rx
        addRxAndroid();
    }

    /** 初始化 */
    private void loadData() {
        //读取阅读记录
        if (mData.getSectionurl().equals(mAdapter.getSurl()))
            mtitle.setTextColor(ContextCompat.getColor(mContext, R.color.red));
        else
            mtitle.setTextColor(ContextCompat.getColor(mContext, R.color.black));
        //本章节的路径
        path = getSectionDownPath(mData);
        //尝试本地加载图片链接
        DownSectionBean bean = FileUtil.get(getSectionBeanPath(mData), DownSectionBean.class);
        if (bean != null) {
            mData.setList(bean.getList());  //暂时只加载list
        }
        //标题
        mtitle.setText(mData.getSection());
        // 10/18
        mSize.setText(String.valueOf(mData.getSuccess() + "/" + mData.getTotal()));
        //进度条
        mProgress.setMax(mData.getTotal());
        mProgress.setProgress(mData.getSuccess());
        // 00成功00失败
        mCallback.setText(String.valueOf(mData.getSuccess() + "成功" + mData.getFailed() + "失败"));
        //状态判断（暂时）
        mStatus.setClickable(true);
        switch (mData.getStatus()) {
            case DownloadStatus.STATE_DOWNLOADED:
                mStatus.setText("下载完成");
                mStatus.setClickable(false);
                return;
            case DownloadStatus.STATE_PROCRESS:
                mStatus.setText("停止");
                return;
            case DownloadStatus.STATE_WAITING:
                mStatus.setText("排队");
                return;
            case DownloadStatus.STATE_DOLOAD:
                mStatus.setText("解析中");
                return;
            case DownloadStatus.STATE_PAUSED:
                mStatus.setText("继续");
                return;
            default:
                //尝试本地加载图片链接
                if (mData.getList() != null)
                    mStatus.setText("已解析");
                else
                    mStatus.setText("下载");
                break;
        }
    }

    private void addRxAndroid() {
        Disposable rx1 = RxBus.getDefault().toObservable(RxEvent.EVENT_DOWN2_STATUS)
            .filter(new Predicate<RxEvent>() {
                @Override
                public boolean test(RxEvent event) throws Exception {
                    return mData.getSectionurl().equals(event.getData());
                }
            })
            .subscribe(new Consumer<RxEvent>() {
                @Override
                public void accept(RxEvent event) throws Exception {
                    int status = (int) event.getData(1);
                    switch (status) {
                        case DownloadStatus.STATE_PROCRESS:
                            int success = (int) event.getData(2);
                            int failed  = (int) event.getData(3);
                            int size    = (int) event.getData(4);
                            //刷新界面进度
                            mData.setTotal(size);
                            mProgress.setMax(size);
                            mStatus.setText("停止");

                            mData.setSuccess(success);
                            mData.setFailed(failed);
                            mProgress.setProgress(success);
                            mCallback.setText(String.valueOf(success + "成功" + failed + "失败"));
                            mSize.setText(String.valueOf((success + failed) + "/" + size));
                            return;
                        case DownloadStatus.STATE_DOLOAD:
                            mData.setStatus(status);
                            mStatus.setText("解析中");
                            return;
                        case DownloadStatus.STATE_WAITING:
                            mData.setStatus(status);
                            mStatus.setText("排队");
                            return;
//                                case DownloadStatus.STATE_DOWNLOADING:
//                                    mData.setStatus(status);
//                                    mStatus.setText("停止");
//                                    int size = (int) event.getData(2);
//                                    mData.setTotal(size);
//                                    mProgress.setMax(size);
//                                    return;
                        case DownloadStatus.STATE_PAUSED:
                            mData.setStatus(status);
                            mStatus.setText("继续");
                            int success2 = (int) event.getData(2);
                            int failed2  = (int) event.getData(3);
                            mData.setSuccess(success2);
                            mData.setFailed(failed2);
                            return;
                        case DownloadStatus.STATE_DOWNLOADED:
                            mData.setStatus(status);
                            mStatus.setText("下载完成");
                            mData.setSuccess(mData.getTotal());
                            return;
                        case DownloadStatus.STATE_START:
                            mData.setStatus(status);
                            mStatus.setText("重新尝试");
                            mData.setSuccess(0);
                            break;
                    }
                }
            });
        mAdapter.getmSubscriptions().add(rx1);
    }

    //=========================================
    /** 下载按钮 */
    @OnClick(R.id.status)
    void onClick() {
        mAdapter.getManager().setDownload(mData);
    }

    //=========================================
    /** 长按操作类 */
    @OnLongClick(R.id.layout)
    boolean onLongClick() {
        new AlertDialog.Builder(mContext)
                .setMessage("是否删除漫画："+ mData.getSection())
                .setNegativeButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        delSection(mData.getSectionurl());
                    }
                })   //通知中间按钮
                .setPositiveButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })      //通知最右按钮
                .setNeutralButton("初始化", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mData.setList(null);
                        mData.setSuccess(0);
                        mData.setFailed(0);
                        mData.setStatus(DownloadStatus.STATE_START);
                        mStatus.setText("下载");
                        mStatus.setClickable(true);
                        loadData();
                    }
                })  //通知最左按钮
                .create()
                .show();

        return true;
    }

    //删除指定章节
    private void delSection(String url) {
        DownDbApi.delDownedSection(url);
        //更新漫画已下载的章节数
        int progress = DownDbApi.getDownedQueueProgress(mData.getBkey());
        int total    = DownDbApi.getDownedQueueTotal(mData.getBkey());

        if (total > 0) {
            DownDbApi.setDownedQueueTotal(mData.getBkey(), total - 1);
        }

        if (progress > 0 && mData.getStatus() ==DownloadStatus.STATE_DOWNLOADED) {
            DownDbApi.setDownedQueueProgress(mData.getBkey(), progress - 1);

            if (progress + 1 == total) {
                DownDbApi.setDownedQueueState(mData.getBkey(), DownBookBean.START);
            }
        }

        mAdapter.remove(getAdapterPosition());
        new AlertDialog.Builder(mContext)
                .setMessage("是否删除本地文件")
                .setNegativeButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FileUtil.deleteFile(path);
                    }
                })   //通知中间按钮
                .setPositiveButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })      //通知最右按钮
                .create()
                .show();
    }

    //==========================================
    /** 下载 -> Section1 */
    @OnClick(R.id.layout)
    void onIntent() {
        if (mData.getStatus() == DownloadStatus.STATE_DOWNLOADED) {
            //读取保存的图片链接文件
            List<Book> imgList2 = new ArrayList<>();
            String filename = getBookCachePath(mData.getBookUrl());
            BookViewModel model = FileUtil.get(filename, BookViewModel.class);
            if (model != null) {
                imgList2 = model.sectionList;
            }

            if (imgList2 == null || imgList2.size() <= 0) {
                HintUtil.show("目录文件丢失");
                return;
            }

            Section1FragmentBase.imgList = imgList2;
            mIntent.Intent_Down(mContext, mData);

        }
    }
}
