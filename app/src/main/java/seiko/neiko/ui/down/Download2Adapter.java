package seiko.neiko.ui.down;

import android.view.ViewGroup;

import rx.subscriptions.CompositeSubscription;
import seiko.neiko.models.DownSectionBean;
import seiko.neiko.service.DownloadManger;
import zlc.season.practicalrecyclerview.AbstractAdapter;

/**
 * Created by Seiko on 2016/11/23. YiKu
 */

class Download2Adapter extends AbstractAdapter<DownSectionBean, Download2ViewHolder> {

    private String last_surl;
    private DownloadManger manager;
    private CompositeSubscription mSubscriptions;

    Download2Adapter(DownloadManger manager) {this.manager = manager;}

    DownloadManger getManager() {return manager;}

    @Override
    protected Download2ViewHolder onNewCreateViewHolder(ViewGroup parent, int viewType) {
        return new Download2ViewHolder(parent, this);
    }

    @Override
    protected void onNewBindViewHolder(Download2ViewHolder holder, int position) {
        holder.setData(get(position));
    }

    //=======================================
    /** 取消ViewHolder中Rx订阅 */
    CompositeSubscription getmSubscriptions() {
        if (mSubscriptions == null)
            mSubscriptions = new CompositeSubscription();
        return mSubscriptions;
    }

    void unsubscribe() {
        if (mSubscriptions != null && !mSubscriptions.isUnsubscribed()) {
            mSubscriptions.unsubscribe();
            mSubscriptions.clear();
        }
    }

    //=======================================
    /** 阅读记录 */
    void setLast_surl(String last_surl) {
        this.last_surl = last_surl;
        notifyDataSetChanged();
    }

    String getSurl() {
        return last_surl;
    }

}
