package seiko.neiko.view;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.util.ArrayList;
import java.util.List;

import seiko.neiko.models.Book;

/**
 * Created by Seiko on 2016/11/18. YiKu
 */

public interface SectionView extends BaseView {

    int dtype();

    String bookurl();

    List<Book> imgList();

    int saveIndex();  //保存的章节在list中的位置

    DiscreteSeekBar seekBar();

    RecyclerView recView();

    void initViews(Bundle State);

    void onScroll();

    void onScrollChanged(int newState);

    /** 触摸变量 */
    LinearLayout layout_control();

    void setScroll(int t);

    /** 控件ID */
    int title_batteryID();
    int title_timeID();
    int backID();
}
