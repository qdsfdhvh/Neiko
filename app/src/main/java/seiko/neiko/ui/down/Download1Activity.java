package seiko.neiko.ui.down;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import io.reactivex.functions.Consumer;
import seiko.neiko.R;
import seiko.neiko.dao.FabScroll;
import seiko.neiko.dao.db.DownDbApi;
import seiko.neiko.rx.RxEvent;
import seiko.neiko.utils.FileUtil;
import seiko.neiko.app.BaseSwipeLayout;
import seiko.neiko.widget.fab.FloatingActionMenu;
import zlc.season.practicalrecyclerview.PracticalRecyclerView;

import static seiko.neiko.dao.mPath.downPath;

/**
 * Created by Seiko on 2016/11/12. YiKu
 */

public class Download1Activity extends BaseSwipeLayout {

    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.recView)
    PracticalRecyclerView recView;
    @BindView(R.id.fab_menu)
    FloatingActionMenu fab_menu;

    private Download1Adapter mAdapter;
    private Context mContext;

    @Override
    public int getLayoutId() {return R.layout.activity_down1;}

    @Override
    public void initViews(Bundle bundle) {
        mContext = this;
        mTitle.setText("下载(未完成)");

        setRecView();
        RxAndroid();
        mAdapter.addAll(DownDbApi.getDownedQueue());
    }

    private void setRecView() {
        mAdapter = new Download1Adapter();
        recView.setLayoutManager(new LinearLayoutManager(this));
        recView.setAdapterWithLoading(mAdapter);
        FabScroll.showFab(recView.get(), fab_menu);
    }

    private void RxAndroid() {
        //随着下载刷新界面 （暂时）
        addSubscription(RxEvent.EVENT_DOWN1_PROCESS, new Consumer<RxEvent>() {
            @Override
            public void accept(RxEvent event) throws Exception {
                mAdapter.notifyDataSetChanged();
            }
        });
    }


    @OnClick(R.id.fab_delete)
    void onClick() {
        new AlertDialog.Builder(this)
                .setMessage("是否删除全部")
                .setNegativeButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        delAll();
                    }
                })      //通知中间按钮
                .setPositiveButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })        //通知最右按钮
                .create()
                .show();
    }

    private void delAll() {
        DownDbApi.delDownedQueueAll();
        DownDbApi.delDownedSectionAll();
        mAdapter.clearData();
        mAdapter.notifyDataSetChanged();

        new AlertDialog.Builder(mContext)
                .setMessage("是否删除本地文件")
                .setNegativeButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FileUtil.deleteFile(downPath);
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

    @OnClick(R.id.cardView)
    void cardView() {
        if (isVisible(fab_menu))
            fab_menu.hideMenu(true);
        else
            fab_menu.showMenu(true);
    }

    @OnClick(R.id.fab_setting)
    void onSetting() {
        LayoutInflater inflater = getLayoutInflater();
        View dialog = inflater.inflate(R.layout.dialog_down_setting, (ViewGroup) findViewById(R.id.dialog));
        new SettingDialog(dialog);
    }


    class SettingDialog {
        @BindView(R.id.spinner)
        Spinner spinner;

        private int MaxDownNumber;
        private SettingDialog(View view) {
            ButterKnife.bind(this, view);

            /* 最大下载数：创建spinner */
            String[] ddd = { "  1  ",
                    "  2  ", "  3  ",
                    "  4  ", "  5  "
            };
            spinner.setAdapter(getAdapter(ddd));

            //加载设置
            MaxDownNumber = DownDbApi.getMaxDownNumber();
            spinner.setSelection(MaxDownNumber - 1);

            new AlertDialog.Builder(mContext)
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

        //阅读模式
        @OnItemSelected(R.id.spinner)
        void spinner(int i) {
            if (i + 1 != MaxDownNumber)
                DownDbApi.setMaxDownNumber(i + 1);
        }

        private ArrayAdapter<String> getAdapter(String[] data) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, Arrays.asList(data));
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            return adapter;
        }
    }
}
