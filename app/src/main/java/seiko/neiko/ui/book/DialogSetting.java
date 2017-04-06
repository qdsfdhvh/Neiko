package seiko.neiko.ui.book;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import seiko.neiko.R;
import seiko.neiko.dao.db.DbApi;
import seiko.neiko.models.ViewSetModel;

/**
 * Created by Seiko on 2016/12/21. Y
 */

class DialogSetting {

    @BindView(R.id.comic1)
    LinearLayout comic1;
    @BindView(R.id.comic2)
    LinearLayout comic2;
    @BindView(R.id.load_prev)
    LinearLayout load_prev;
    @BindView(R.id.video1)
    LinearLayout video1;

    @BindView(R.id.read_mode)
    Spinner read_mode;
    @BindView(R.id.cut_pic)
    Spinner cut_pic;
    @BindView(R.id.load_prev_check)
    CheckBox load_prev_check;
    @BindView(R.id.video_mode)
    Spinner video_mode;

    private Context mContext;
    private String url;
    private ViewSetModel viewSet;
    private int CutPic;

    private int videoMode;

    DialogSetting(View view, String url, int dtype) {
        ButterKnife.bind(this, view);
        mContext = view.getContext();
        this.url = url;

        switch (dtype) {
            case 1:
                visible(comic1, comic2, load_prev);
                break;
            case 2:
                visible(load_prev);
                break;
            case 3:
                visible(video1);
                break;
        }

        /* 阅读模式：创建spinner */
        String[] aaa = {
                    "流水模式",
                    "翻页模式",
                    "日漫模式"
                };
        read_mode.setAdapter(getAdapter(aaa));
        /* 图片切割：创建spinner */
        String[] bbb = {
                    "禁用:上->下",
                    "日漫:左<-右",
                    "国漫:左->右"
                };
        cut_pic.setAdapter(getAdapter(bbb));
        /* 选择博昂起：创建spinner */
        String[] ccc = {
                "默认",
                "内部",
                "外部"
        };
        video_mode.setAdapter(getAdapter(ccc));


        getSave(); //加载设置

        new AlertDialog.Builder(view.getContext())
                .setTitle("设置")
                .setView(view)
                .setPositiveButton("关闭", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })  //通知最右按钮
                .create()
                .show();
    }


    /* 加载保存设置 */
    private void getSave() {
        if (!TextUtils.isEmpty(url)) {
            viewSet = DbApi.getBookViewSet(url);
        } else {
            viewSet = new ViewSetModel();
        }

        CutPic = DbApi.getCutPic();
        videoMode = DbApi.getVideoMode();

        //控件设置
        cut_pic.setSelection(CutPic);
        read_mode.setSelection(viewSet.view_model);
        video_mode.setSelection(videoMode);
        switch (viewSet.view_direction) {
            case 0: load_prev_check.setChecked(false); break;
            case 1: load_prev_check.setChecked(true);  break;
        }
    }

    /* 阅读模式 */
    @OnItemSelected(R.id.read_mode)
    void spinner(int i) {
        if (i != viewSet.view_model) {
            viewSet.view_model = i;
            DbApi.setBookViewSet(url, viewSet);
        }
    }

    /* 图片切割 */
    @OnItemSelected(R.id.cut_pic)
    void spinner2(int i) {
        if (i != CutPic) {
            CutPic = i;
            DbApi.setCutPic(i);
        }
    }

    /* 选择播放器 */
    @OnItemSelected(R.id.video_mode)
    void spinner3(int i) {
        if (i != videoMode) {
            videoMode = i;
            DbApi.setVideoMode(videoMode);
        }
    }


    /* 反向加载 */
    @OnClick(R.id.load_prev)
    void load_prev() {
        if (load_prev_check.isChecked()) {
            load_prev_check.setChecked(false);
            viewSet.view_direction = 0;
        } else {
            load_prev_check.setChecked(true);
            viewSet.view_direction = 1;
        }
        DbApi.setBookViewSet(url, viewSet);
    }

    //===================================
    private ArrayAdapter<String> getAdapter(String[] data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, Arrays.asList(data));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    private void visible(View... views) {
        for (View view:views) {
            view.setVisibility(View.VISIBLE);
        }
    }
}
