package seiko.neiko.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import org.noear.sited.SdSourceCallback;

import java.util.ArrayList;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import seiko.neiko.dao.SourceApi;
import seiko.neiko.dao.engine.DdSource;
import seiko.neiko.models.DownSectionBean;
import seiko.neiko.models.ImgUrlBean;
import seiko.neiko.rx.RxBus;
import seiko.neiko.rx.RxEvent;
import seiko.neiko.utils.FileUtil;
import seiko.neiko.viewModels.Section1ViewModel;

import static android.content.Context.BIND_AUTO_CREATE;
import static seiko.neiko.dao.mPath.getSectionBeanPath;

/**
 * Created by Seiko on 2017/1/9. Y
 */

public class DownloadManger {
    private final static String TAG = "DownloadManger";

    private static ServiceConnection conn;
    private static DownloadService.DownloadServiceBinder binder;
    private Context mContext;

    //====================================
    /** 创建Manger并绑定service */
    private DownloadManger(Context context) {this.mContext = context;}

    public static DownloadManger create(Context context) {
        if (binder == null) {
            try {
                conn = new ServiceConnection() {
                    public void onServiceConnected(ComponentName name, IBinder service) {
                        if (service instanceof DownloadService.DownloadServiceBinder) {
                            binder = (DownloadService.DownloadServiceBinder) service;
                            Log.d(TAG, "服务连接成功");
                        }
                    }

                    public void onServiceDisconnected(ComponentName name) {}
                };

                Intent intent = new Intent(context, DownloadService.class);
                context.bindService(intent, conn, BIND_AUTO_CREATE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new DownloadManger(context);
    }

    //========================================
    /** 下载相关操作 */
    //添加下载
    public void setDownload(DownSectionBean bean) {
        switch (bean.getStatus()) {
            case DownloadStatus.STATE_START:
            case DownloadStatus.STATE_PAUSED:
                addDownload(bean);
                break;
            case DownloadStatus.STATE_WAITING:
            case DownloadStatus.STATE_DOLOAD:
                pauseDownload(bean);
                break;
        }
    }

    public void addDownload(DownSectionBean bean) {
        if (bean.getStatus() != DownloadStatus.STATE_DOWNLOADED) {
            if (bean.getList() == null || bean.getList().size() == 0)
                DoLoadViewModel(bean);
            else
                toSerivce(bean);
        }
    }

    public void pauseDownload(DownSectionBean bean) {
        binder.getService().pauseDownload(bean.getIndex());
    }

    private void toSerivce(final DownSectionBean bean) {
        if (binder != null) {
            binder.getService().addDownload(bean);
        } else {
            Intent intent = DownloadService.createIntent(mContext, bean);
            mContext.startService(intent);
        }
    }

    //解析imgList
    private void DoLoadViewModel(final DownSectionBean bean) {
        RxBus.getDefault().post(new RxEvent(RxEvent.EVENT_DOWN2_STATUS, bean.getSectionurl(), DownloadStatus.STATE_DOLOAD));

//        String sec_url = bean.getSectionurl();
//        DdSource sd = SourceApi.getDefault().getByUrl(sec_url);
//        if (sd != null) {
//            final Section1ViewModel viewModel = new Section1ViewModel();
//            viewModel.clear();
//            sd.getNodeViewModel(viewModel, true, sec_url, sd.section(sec_url), new SdSourceCallback() {
//                @Override
//                public void run(Integer code) {
//                    if (code == 1) {
//                        bean.setList(viewModel.imgList);
//                        bean.setTotal(viewModel.imgList.size());
//                        FileUtil.save(getSectionBeanPath(bean), bean);
//                        toSerivce(bean);
//                    }
//                }
//            });
//        }
        Flowable.create(new FlowableOnSubscribe<DownSectionBean>() {
                    @Override
                    public void subscribe(final FlowableEmitter<DownSectionBean> e) throws Exception {
                        String sec_url = bean.getSectionurl();
                        DdSource sd = SourceApi.getDefault().getByUrl(sec_url);
                        if (sd != null) {
                            final Section1ViewModel viewModel = new Section1ViewModel();
                            viewModel.clear();
                            sd.getNodeViewModel(viewModel, true, sec_url, sd.section(sec_url), new SdSourceCallback() {
                                @Override
                                public void run(Integer code) {
                                    if (code == 1) {
                                        bean.setList(viewModel.imgList);
                                        bean.setTotal(viewModel.imgList.size());
                                        FileUtil.save(getSectionBeanPath(bean), bean);
                                        e.onNext(bean);
                                    }
                                    e.onComplete();
                                }
                            });
                        } else {
                            e.onComplete();
                        }
                    }
                }, BackpressureStrategy.ERROR)
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<DownSectionBean>() {
                    @Override
                    public void accept(DownSectionBean bean1) throws Exception {
                        toSerivce(bean1);
                    }
                });
    }

    //====================================
    /** 注销service */
    public void unbindService() {
        try {
            if (mContext != null) {
                mContext.unbindService(conn);
            }

            Log.d(TAG, "服务解绑");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
