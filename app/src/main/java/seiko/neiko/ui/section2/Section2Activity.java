package seiko.neiko.ui.section2;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding.support.v7.widget.RecyclerViewScrollEvent;
import com.jakewharton.rxbinding.support.v7.widget.RxRecyclerView;
import com.jakewharton.rxbinding.view.RxView;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.noear.sited.SdNode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;
import seiko.neiko.R;
import seiko.neiko.dao.db.DbApi;
import seiko.neiko.dao.mTouch;
import seiko.neiko.models.TxtModel;
import seiko.neiko.models.ViewSetModel;
import seiko.neiko.rx.RxBus;
import seiko.neiko.rx.RxEvent;
import seiko.neiko.dao.mReceiver;
import seiko.neiko.utils.EncryptUtil;
import seiko.neiko.utils.HintUtil;
import seiko.neiko.viewModels.Section2ViewModel;
import seiko.neiko.app.ActivityBase;
import seiko.neiko.widget.TextDrawable;

/**
 * Created by Seiko on 2017/1/22. Y
 */

public class Section2Activity extends ActivityBase implements Section2View, mTouch.TouchListener {

    public static Section2Model m;

    @BindView(R.id.recView)
    RecyclerView recView;
    @BindView(R.id.seekbar)
    DiscreteSeekBar seekBar;
    @BindView(R.id.control)
    LinearLayout control;
    @BindView(R.id.section_back)
    LinearLayout back;
    @BindView(R.id.section_indexs)
    TextView title_indexs;  //名称

    private SdNode config = null;
    private Section2ViewModel viewModel;
    private Section2Adapter adapter;
    private LinearLayoutManager llm;
    private mReceiver receiver;

    private boolean nowload = false;
    private int cp = 0;
    private int id = 0;
    private int t  = 1;

    @Override
    protected void onResume() {
        super.onResume();
        setImmersiveFullscreen(true); //全屏
    }

    @Override
    public int getLayoutId() {return R.layout.activity_section2;}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setModel();
        setRecView();
        DoLoadViewModel(m.sec_name, m.sec_url, true); //首次加载
        initDetector();
        setSeekBar();
        setTextSize();
    }

    private void setModel() {
        switch (m.dtype) {
            case 2:
                config = source.section(m.sec_url);

                ViewSetModel viewSet = DbApi.getBookViewSet(EncryptUtil.md5(m.bookUrl));
                /* 反向加载 */
                switch (viewSet.view_direction) {
                    default:
                    case 0: t = 1; break;
                    case 1: t = -1; break;
                }
                break;
            case 6:
                config = source.book(m.sec_url);
                break;
        }

        viewModel = new Section2ViewModel(m.sec_url);
        //加载电池、时间服务
        receiver = new mReceiver(this);
    }

    private void setRecView() {
        adapter = new Section2Adapter();
        adapter.dtype = m.dtype;
        adapter.setbtClickListener(this);
        llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recView.setLayoutManager(llm);
        recView.setHasFixedSize(true);
        recView.setAdapter(adapter);
        RxRecyclerView.scrollEvents(recView).subscribe((RecyclerViewScrollEvent event) -> {
            int id = llm.findFirstVisibleItemPosition();
            seekBar.setProgress(id + 1);
        });
    }

    private void DoLoadViewModel(String name, String url, boolean loadsave) {
        viewModel.clear();
        viewModel.addTitleItem(name, 2);

        if (name.length()>20) {
            name = name.substring(0, 21);
        }
        title_indexs.setText(name);

        source.getNodeViewModel(viewModel, false, url, config, (code) -> {
            if (code == 1) {
                initRec(viewModel.items, loadsave);
            }
        });

        nowload = false;
    }

    private void initRec(List<TxtModel> items, boolean loadsave) {
        //设置网格布局管理器
        adapter.addAll(items);
        //修改进度条
        seekBar.setMax(items.size());
        //读取进度 只读一次
        if (loadsave) {
            seekBar.setMin(1);
            int lastpage = DbApi.getLastBookPage(m.sec_url);
            recView.scrollToPosition(lastpage);
        }
    }
    //=============================================================
    /** 上一章&下一章 */
    @Override
    public void onItemClick(int index) {
        switch (index) {
            case -1: toast("加载下一章"); break;
            case  1: toast("加载上一章"); break;
        }

        if (nowload) {
            HintUtil.show("加载中");
            return;
        }

        nowload = true;
        new Handler().postDelayed(() -> {
            cp += index * t;

            if (isBottom(m.imgindex + cp)) {
                cp -= index * t;
                nowload = false;
                return;
            }

            adapter.clear();
            adapter.notifyDataSetChanged();
            DoLoadViewModel(getName(), getUrl(), false);
        }, 500);
    }

    public String getUrl() {return m.imgList.get(m.imgindex+cp).getSection_url();}
    public String getName() {return m.imgList.get(m.imgindex+cp).getSection();}

    //判断是否到list底部
    private boolean isBottom(int index) {
        if (index < 0 || index > m.imgList.size() - 1) {
            toast("没有了");
            return true;
        }
        return false;
    }


    //=============================================================
    /** 触摸操作 */
    private GestureDetector gestureScanner;
    private void initDetector() {
        gestureScanner = new GestureDetector(this, new mTouch(this));
        RxView.clicks(back).subscribe((Void aVoid) -> finish());
        RxRecyclerView.scrollEvents(recView).subscribe((RecyclerViewScrollEvent event) -> {
            id = llm.findFirstVisibleItemPosition();
            seekBar.setProgress(id + 1);
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        gestureScanner.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void changeVisible() {
        changeVisible(control); //显示按钮
        changeVisible(back);
    }

    @Override
    public boolean control() {
        return isVisible(control);
    }

    @Override
    public void setScroll(int move, int t) {
        recView.scrollBy(0, move * t);
        int index;
        if (move * t < 0) {
            index = llm.findFirstVisibleItemPosition();
        } else {
            index = llm.findLastVisibleItemPosition();
        }
        recView.scrollToPosition(index);
    }

    //=============================
    /** 滚动条操作 */
    private void setSeekBar() {
        seekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {}

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar2) {
                if (seekBar2 == seekBar) {
                    int pg = seekBar.getProgress();
                    recView.scrollToPosition(pg - 1);
                }
            }
        });
    }

    //=======================================
    /** 设置 */
    @BindView(R.id.setting)
    LinearLayout setting;
    @BindView(R.id.textSize)
    DiscreteSeekBar textSize;
    @BindView(R.id.textTheme)
    LinearLayout textTheme;

    /* 显示&隐藏设置 */
    @OnClick(R.id.change_setting)
    void change_setting() {changeVisible(setting);}

    private void setTextSize() {
        adapter.textSize = DbApi.getTextSize();

        textSize.setMax(30);
        textSize.setMin(10);
        textSize.setProgress(adapter.textSize);
        textSize.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int i, boolean fromUser) {
                adapter.textSize = i;
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
                DbApi.setTextSize(adapter.textSize); //保存字体大小
            }
        });

        List<String[]> theme = new ArrayList<>();
        String[] b0 = {"A", "#000000", "#f8f8f8"};
        String[] b1 = {"B", "#383129", "#cdc9be"};
        String[] b2 = {"C", "#a19e99", "#3a3531"};
        String[] b3 = {"D", "#333331", "#ccebcc"};
        theme.add(b0);
        theme.add(b1);
        theme.add(b2);
        theme.add(b3);

        int num = DbApi.getTextTheme();
        setTheme(theme.get(num));

        for (int i=0;i<theme.size();i++) {
            addButton(i, theme.get(i));
        }
    }

    private void addButton(int i, String[] bg) {
        Button bt = new Button(this);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(100, 100);
        lp.leftMargin = 50;

        TextDrawable drawable = TextDrawable.builder()
                .beginConfig()
                .textColor(Color.parseColor(bg[1]))
                .fontSize(30)
                .endConfig()
                .buildRound(bg[0], Color.parseColor(bg[2]));
        bt.setBackground(drawable);

        RxView.clicks(bt).subscribe((Void aVoid) -> {
            setTheme(bg);
            DbApi.setTextTheme(i);
        });
        textTheme.addView(bt, lp);
    }

    private void setTheme(String[] bg) {
        recView.setBackgroundColor(Color.parseColor(bg[2]));
        adapter.textColor = Color.parseColor(bg[1]);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isTrue(m.imgList)) {
            if (m.dtype == 2 && id >= 0) {
                /* 保存 */
                DbApi.setLastBookPage2(m.bookUrl, getUrl(), id, m.imgindex+cp);
                /* book界面、下载同步进度 */
                RxBus.getDefault().post(new RxEvent(RxEvent.EVENT_SECTION1_SAVE, getUrl(), m.imgindex+cp));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        receiver.unSub();
        m = null;
    }


}
