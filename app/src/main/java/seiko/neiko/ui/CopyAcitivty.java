package seiko.neiko.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import seiko.neiko.R;
import seiko.neiko.dao.db.DbApi;
import seiko.neiko.dao.db.SiteDbApi;
import seiko.neiko.models.Book;
import seiko.neiko.models.SourceModel;
import seiko.neiko.rx.RxBus;
import seiko.neiko.rx.RxEvent;
import seiko.neiko.utils.Base64Util;
import seiko.neiko.utils.FileUtil;
import seiko.neiko.app.SwipeLayoutBase;

import static seiko.neiko.dao.mPath.backPath;

/**
 * Created by Seiko on 2016/12/16. YiKu
 * 收藏、下载、插件等备份到本地
 */

public class CopyAcitivty extends SwipeLayoutBase {
    private String back_path;
    private String sited_path;

    @BindView(R.id.title)
    TextView tv;

    @Override
    public int getLayoutId() {return R.layout.activity_copy;}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tv.setText("备份");

        back_path = backPath + "Backup";
        sited_path = backPath + "Sited_Backup";
    }

    Handler handler = new Handler();

    //================================================================
    /** 收藏 */
    /* 收藏备份 */
    @OnClick(R.id.like_backup)
    void Backup() {
        new AlertDialog.Builder(this)
                .setTitle("是否备份收藏")
                .setNegativeButton("是", (DialogInterface dif, int j) -> likesBackup())      //通知中间按钮
                .setPositiveButton("否", (DialogInterface dif, int j) -> dif.dismiss())      //通知最右按钮
                .create()
                .show();
    }

    private void likesBackup() {
        handler.postDelayed(() -> {
            ArrayList<Book> list_like = DbApi.getLike();
            if (list_like != null && list_like.size() != 0) {
                String json = new Gson().toJson(list_like);
                FileUtil.saveText2Sdcard(back_path, json);
                toast("收藏：备份成功");
            } else {
                toast("收藏：没有收藏");
            }
        }, 500);
    }

    /* 收藏恢复 */
    @OnClick(R.id.like_recover)
    void Recover() {
        handler.postDelayed(() -> {
            String json = FileUtil.readTextFromSDcard(back_path);
            if (!TextUtils.isEmpty(json)) {
                Type type = new TypeToken<List<Book>>(){}.getType();
                ArrayList<Book> list_like = new Gson().fromJson(json, type);
                if (list_like != null && list_like.size() != 0) {
                    for (Book book : list_like) {
                        book.setIsref(true);
                        DbApi.addLike(book);
                    }
                }
                RxBus.getDefault().post(new RxEvent(RxEvent.EVENT_COPY_LIKE)); //刷新收藏界面
                toast("收藏：恢复成功");
            } else {
                toast("收藏：没有找到文件");
            }
        }, 500);
    }

    //================================================================
    /** 插件 */
    /* 插件备份 */
    @OnClick(R.id.sited_backup)
    void sited_backup() {
        new AlertDialog.Builder(this)
                .setTitle("是否备份插件")
                .setNegativeButton("是", (DialogInterface dif, int j) -> sitedBackup())      //通知中间按钮
                .setPositiveButton("否", (DialogInterface dif, int j) -> dif.dismiss())      //通知最右按钮
                .create()
                .show();
    }

    private void sitedBackup() {
        handler.postDelayed(() -> {
            List<SourceModel> list = SiteDbApi.getSources();
            if (list != null && list.size() != 0) {
                String json = new Gson().toJson(list);
                String base64 = Base64Util.encode(json);
                FileUtil.saveText2Sdcard(sited_path, base64);
                toast("SiteD：备份成功");
            } else {
                toast("SiteD：没有插件");
            }
        }, 500);
    }

    /* 插件恢复 */
    @OnClick(R.id.sited_recover)
    void sited_recover() {
        handler.postDelayed(() -> {
            String base64 = FileUtil.readTextFromSDcard(sited_path);
            if (!TextUtils.isEmpty(base64)) {
                String json = Base64Util.decode(base64);
                Type type = new TypeToken<List<SourceModel>>(){}.getType();
                List<SourceModel> list = new Gson().fromJson(json, type);
                if (list != null && list.size() != 0) {
                    for (SourceModel m : list) {
                        SiteDbApi.addSource(m);
                    }
                }
                RxBus.getDefault().post(new RxEvent(RxEvent.EVENT_COPY_SITED)); //刷新收藏界面
                toast("SiteD：恢复成功");
            } else {
                toast("SiteD：没有找到文件");
            }
        }, 500);
    }


}
