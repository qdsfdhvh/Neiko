package seiko.neiko.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.LongSparseArray;

import org.noear.sited.SdSourceCallback;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp.OkHttpProxy;
import okhttp3.CacheControl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import seiko.neiko.dao.SourceApi;
import seiko.neiko.dao.db.DownDbApi;
import seiko.neiko.dao.Extra;
import seiko.neiko.dao.engine.DdSource;
import seiko.neiko.models.DownSectionBean;
import seiko.neiko.models.ImgUrlBean;
import seiko.neiko.models.Pair;
import seiko.neiko.rx.RxBus;
import seiko.neiko.rx.RxEvent;
import seiko.neiko.utils.FileUtil;
import seiko.neiko.viewModels.Section1ViewModel;

import static seiko.neiko.dao.mPath.getSectionBeanPath;
import static seiko.neiko.dao.mPath.getSectionDownPath;
import static seiko.neiko.dao.mPath.getSectionImgPath;

/**
 * Created by Seiko on 2017/1/8. Y
 */

public class DownloadService extends Service {
    private final String TAG = "DownloadService";

    private LongSparseArray<Pair<DownloadTask, Future>> mWorkerArray;
    private ExecutorService executor;
    private OkHttpClient mHttpClient;
    private int max;

    private DownloadServiceBinder binder;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (binder == null)
            binder = new DownloadServiceBinder();
        return binder;
    }

    class DownloadServiceBinder extends Binder {
        DownloadService getService() {return DownloadService.this;}
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mWorkerArray = new LongSparseArray<>();
        mHttpClient = new OkHttpClient();
        //设置最大线程：1表示单线程
        max = DownDbApi.getMaxDownNumber();
    }

    public ExecutorService getExecutor() {
        if (executor == null) {
            executor = Executors.newFixedThreadPool(max);
        }
        return executor;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            List<DownSectionBean> list =  intent.getParcelableArrayListExtra(Extra.EXTRA_TASK);
            for (DownSectionBean bean : list) {
                addDownload(bean);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mWorkerArray != null && mWorkerArray.size() == 0 && executor != null) {
            executor.shutdownNow();
        }
    }

    //====================================
    /** 对下载线程的处理 */
    public synchronized void addDownload(DownSectionBean bean) {
        long id = bean.getIndex();

        if (mWorkerArray.get(id) == null) {
            DownloadTask task = new DownloadTask(bean);
            final Future future = getExecutor().submit(task);
            mWorkerArray.put(id, Pair.create(task, future));
        }
    }

    public synchronized void pauseDownload(long id) {
        Pair<DownloadTask, Future> pair = mWorkerArray.get(id);
        if (pair != null) {
            pair.first.stop(); //退出Runable
            pair.first.RxStatus(DownloadStatus.STATE_PAUSED); //并把进度返回
            pair.second.cancel(true);
            mWorkerArray.remove(id);
        }
    }

    public synchronized void completeDownload(long id) {
        Pair<DownloadTask, Future> pair = mWorkerArray.get(id);
        if (pair != null) {
            pair.second.cancel(true);
            mWorkerArray.remove(id);
        }
    }

    //====================================
    /** 下载线程 */
    private class DownloadTask implements Runnable {

        private DownSectionBean task;
        private int success;
        private int failed;
        private int size;
        private boolean isrunning;

        DownloadTask(DownSectionBean task) {
            this.task = task;
            isrunning = false;
            success = task.getSuccess();
            failed = 0;     //重新开始统计错误
            RxStatus(DownloadStatus.STATE_WAITING);
        }

        //停止
        void stop() {
            isrunning = false;
        }

        /**
         * 图片下载
         */
        @Override
        public void run() {
            StartDown();
        }

        private void StartDown() {
            Log.d(TAG, "开始下载:" + task.getSection() + task.getList().size());
            try {
                isrunning = true;
                size = task.getList().size();
//                RxStatus(DownloadStatus.STATE_DOWNLOADING, size);
                String path = getSectionDownPath(task);  //本章节的路径

                //开始下载
                for (int i = success; i < size; ++i) {
                    ImgUrlBean bean = task.getList().get(i);

                    int count = 0;    // 单页下载错误次数
                    boolean isOk = false; // 是否下载成功

                    //对一个链接进行下载
                    while (count++ <= 3 && !isOk) {
                        String url = bean.getUrl();
                        if (url != null) {
                            Request request = buildRequest(url);
                            isOk = RequestAndWrite(path, request, bean);
                        }
                        //链接下载错误
                        if (count > 3 && !isOk) {
                            failed++;
                            RxStatus(DownloadStatus.STATE_PROCRESS);
                        }
                        //退出本章下载
                        if (!isrunning) {
                            return;
                        }
                    }
                }
            } catch (InterruptedIOException e) {
                e.printStackTrace();
            }
            isOk();
        }

        private void isOk() {
            if (success + failed == size) {
                //保存进度
                DownDbApi.setDownedSectionIndex(task.getSectionurl(), success, failed, size);
                if (failed > 0) {
                    RxStatus(DownloadStatus.STATE_START);
                } else {
                    RxStatus(DownloadStatus.STATE_DOWNLOADED);
                    setQueue();
                }
                completeDownload(task.getIndex());
            }
        }

        //尝试下载图片并保存
        private boolean RequestAndWrite(String path, Request request, ImgUrlBean bean) throws InterruptedIOException {
            Response response = null;
            try {
                response = mHttpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    FileUtil.saveText2Sdcard(getSectionImgPath(path, bean), response.body().byteStream());
                    Log.d(TAG, "下载成功index：" + bean.getIndex() + " url:" + bean.getUrl());

                    success++;
                    RxStatus(DownloadStatus.STATE_PROCRESS);
                    return true;
                }
            } catch (InterruptedIOException e) {
                RxStatus(DownloadStatus.STATE_PAUSED);
                // 由暂停下载引发，需要抛出以便退出外层循环，结束任务
                throw e;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (response != null) {
                    response.close();
                }
            }
            return false;
        }

        //创建请求
        private Request buildRequest(String url) {
            return new Request.Builder()
                    .cacheControl(new CacheControl.Builder().noStore().build())
                    .url(url)
                    .get()
                    .build();
        }

        //漫画：更新状态(多线程时会少加)
        private void setQueue() {
            int progress = DownDbApi.getDownedQueueProgress(task.getBkey());
            DownDbApi.setDownedQueueProgress(task.getBkey(), progress + 1);
            Log.d(TAG, "下载完成：" + task.getSection() +"\n--------");
        }

        //发送状态&&保存状态
        private void RxStatus(int status) {
            DownDbApi.setDownedSectionState(task.getSectionurl(), status); //修改状态
            RxBus.getDefault().post(new RxEvent(RxEvent.EVENT_DOWN2_STATUS, task.getSectionurl(), status, success, failed, size));
        }
    }

    //=========================================
    public static Intent createIntent(Context context, DownSectionBean bean) {
        ArrayList<DownSectionBean> list = new ArrayList<>(1);
        list.add(bean);
        return createIntent(context, list);
    }

    private static Intent createIntent(Context context, ArrayList<DownSectionBean> list) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.putParcelableArrayListExtra(Extra.EXTRA_TASK, list);
        return intent;
    }
}
