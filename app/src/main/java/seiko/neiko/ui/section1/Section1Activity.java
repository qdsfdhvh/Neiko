package seiko.neiko.ui.section1;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding.support.v7.widget.RecyclerViewScrollEvent;
import com.jakewharton.rxbinding.support.v7.widget.RxRecyclerView;
import com.jakewharton.rxbinding.view.RxView;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import butterknife.BindView;
import butterknife.OnClick;
import seiko.neiko.R;
import seiko.neiko.glide.ImageLoader;
import seiko.neiko.dao.db.DbApi;
import seiko.neiko.dao.mTouch;
import seiko.neiko.models.ViewSetModel;
import seiko.neiko.rx.RxBus;
import seiko.neiko.rx.RxEvent;
import seiko.neiko.app.ActivityBase;
import seiko.neiko.dao.mReceiver;
import seiko.neiko.utils.EncryptUtil;

import static seiko.neiko.ui.section1.Section1FragmentBase.bookPages;
import static seiko.neiko.ui.section1.Section1FragmentBase.bookUrl;
import static seiko.neiko.ui.section1.Section1FragmentBase.dtype;
import static seiko.neiko.ui.section1.Section1FragmentBase.imgList;
import static seiko.neiko.ui.section1.Section1FragmentBase.t;

/**
 * Created by Seiko on 2016/12/22. Y
 */

public class Section1Activity extends ActivityBase implements Section1View, mTouch.TouchListener {

    @BindView(R.id.section_indexs)
    TextView title;
    @BindView(R.id.seekbar)
    DiscreteSeekBar seekBar;
    @BindView(R.id.section_battery)
    TextView battery;
    @BindView(R.id.section_time)
    TextView time;
    @BindView(R.id.control)
    LinearLayout control;
    @BindView(R.id.section_back)
    LinearLayout back;

    private ViewSetModel viewSet;
    private mReceiver receiver;
    private RecyclerView recView;

    @Override
    protected void onResume() {
        super.onResume();
        setImmersiveFullscreen(true); //全屏
        ImageLoader.getDefault().loadSave();
    }

    @Override
    public int getLayoutId() {return R.layout.activity_section1;}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        switch (dtype) {
            case 4:
                setFragment(new Section1FragmentS());
                break;
            case 1:
                viewSet = DbApi.getBookViewSet(EncryptUtil.md5(bookUrl));
                loadViewSet(viewSet);
                break;
        }
        //加载电池、时间服务
        receiver = new mReceiver(this, battery, time);
        //加载触摸手势
        initDetector();
        //seekBar拖动
        setSeekBar();
    }

    private void loadViewSet(ViewSetModel viewSet) {
        /* 反向加载 */
        switch (viewSet.view_direction) {
            default:
            case 0: t = 1; break;
            case 1: t = -1; break;
        }
        /* 阅读模式 */
        switch (viewSet.view_model) {
            default:
            case 0:
                setFragment(new Section1FragmentS());
                break;
            case 1:
            case 2:
                setFragment(new Section1FragmentP());
                break;
        }
    }

    private void setFragment(Section1FragmentBase fragment) {
        fragment.setSection1View(this);
        shiftView(fragment);
    }


    //==============================================
    /** 上下滑动时，修改标题栏 例如：xxx 5/10 */
    private int ct = 0;
    private int oldpages = 0;
    private int id;
    /*=========标题栏用========*/
    //获得当前页数
    private int getpages() {return bookPages.get(ct).getBook_pages();}
    //获得当前标题
    private String gettitle() {return bookPages.get(ct).getBook_title();}
    /*=========保存用=========*/
    //获得当前在imgList中的对应位置
    private int getIndex() {return bookPages.get(ct).getBook_index();}
    //获得当前链接
    private String getUrl() {return bookPages.get(ct).getBook_url();}

    @Override
    public void onScroll(RecyclerView recView, LinearLayoutManager llm) {
        this.recView = recView;
        RxRecyclerView.scrollEvents(recView).subscribe((RecyclerViewScrollEvent event) -> {
            if (bookPages.size() == 0) return;

            id = llm.findFirstVisibleItemPosition();
            if ( (id-oldpages) > getpages() - 1) {
                oldpages += getpages();
                ct += 1;
            } else if ((id-oldpages) < 0) {
                ct -= 1;
                if (ct<0) ct=0;
                oldpages -= getpages();  //数据不能及时传到
            }

            if (ct < bookPages.size()) {
                setTitle(gettitle(), id - oldpages, getpages());
            }
        });

    }

    private int cpages; // 用于判断是否需要修改seekbar
    private void setTitle(String name, int index, int pages) {
        if (name != null && name.length() > 20)
            name = name.substring(0, 21);
        title.setText(String.valueOf(name +" " + (index + 1) +"/" + pages));
        seekBar.setProgress(index + 1);
        if (cpages != pages) {
            seekBar.setMax(pages);
            seekBar.setMin(1);
            cpages = pages;
        }
    }

    //==============================================
    /** 触摸操作 */
    private GestureDetector gestureScanner;
    private void initDetector() {
        gestureScanner = new GestureDetector(this, new mTouch(this));
        RxView.clicks(back).subscribe((Void aVoid) -> finish());
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
        switch (viewSet.view_model) {
            case 0:
                recView.smoothScrollBy(0, move * t);
                break;
            case 1:
                recView.smoothScrollToPosition(id + t);
                break;
            case 2:
                recView.smoothScrollToPosition(id - t);
                break;
        }
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
                    recView.scrollToPosition(pg - 1 + oldpages);
                }
            }
        });
    }
    //==============================================
    /** 横屏&竖屏 */
    @OnClick(R.id.change_Orientation) void change_Orientation() {changeOri();}

    //==============================================
    /** 保存 */
    @Override
    protected void onStop() {
        super.onStop();
        if (isTrue(imgList, bookPages)) {
            if (dtype == 1 && id >= 0) {
                /* 保存 */
                DbApi.setLastBookPage2(bookUrl, getUrl(), id-oldpages, getIndex());
                /* book界面、下载同步进度 */
                RxBus.getDefault().post(new RxEvent(RxEvent.EVENT_SECTION1_SAVE, getUrl(), getIndex()));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        receiver.unSub();
    }


}
