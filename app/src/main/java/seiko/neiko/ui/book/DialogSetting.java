package seiko.neiko.ui.book;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
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
    @BindView(R.id.read_mode)
    Spinner read_mode;
    @BindView(R.id.cut_pic)
    Spinner cut_pic;
    @BindView(R.id.load_prev_check)
    CheckBox load_prev_check;

    private Context mContext;
    private String bkey;
    private ViewSetModel viewSet;
    private int CutPic;

    DialogSetting(View view, String bkey) {
        ButterKnife.bind(this, view);
        mContext = view.getContext();
        this.bkey = bkey;

        /* 阅读模式：创建spinner */
        String[] aaa = {
                    "流水模式",
                    "翻页模式",
                    "日漫模式"
                };
        read_mode.setAdapter(getAdapter(aaa));
        /* 图片切割：创建spinner */
        String[] ddd = {
                    "禁用:上->下",
                    "日漫:左<-右",
                    "国漫:左->右"
                };
        cut_pic.setAdapter(getAdapter(ddd));

        getSave(); //加载设置

        new AlertDialog.Builder(view.getContext())
                .setTitle("设置")
                .setView(view)
                .setPositiveButton("关闭", (DialogInterface dif, int j) -> dif.dismiss())  //通知最右按钮
                .create()
                .show();
    }

    /* 加载保存设置 */
    private void getSave() {
        if (!TextUtils.isEmpty(bkey)) {
            viewSet = DbApi.getBookViewSet(bkey);
        } else {
            viewSet = new ViewSetModel();
        }

        CutPic = DbApi.getCutPic();

        //控件设置
        cut_pic.setSelection(CutPic);
        read_mode.setSelection(viewSet.view_model);
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
            DbApi.setBookViewSet(bkey, viewSet);
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
        DbApi.setBookViewSet(bkey, viewSet);
    }

    //===================================
    private ArrayAdapter<String> getAdapter(String[] data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, Arrays.asList(data));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }
}
