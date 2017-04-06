package seiko.neiko.dao;

import android.support.v7.widget.RecyclerView;


import com.jakewharton.rxbinding2.support.v7.widget.RecyclerViewScrollEvent;
import com.jakewharton.rxbinding2.support.v7.widget.RxRecyclerView;

import io.reactivex.functions.Consumer;
import seiko.neiko.widget.fab.FloatingActionButton;
import seiko.neiko.widget.fab.FloatingActionMenu;

/**
 * Created by Seiko on 2017/2/7.
 * recView滑动时，隐藏或显示fab
 */

public class FabScroll {

    private FloatingActionMenu menu;
    private FloatingActionButton fab;

    private FabScroll(FloatingActionMenu menu) {this.menu = menu;}
    private FabScroll(FloatingActionButton fab) {this.fab = fab;}

    private void rxView(RecyclerView recView) {
        RxRecyclerView.scrollEvents(recView).subscribe(new Consumer<RecyclerViewScrollEvent>() {
            @Override
            public void accept(RecyclerViewScrollEvent event) throws Exception {
                boolean safe = Math.abs(event.dy()) > 10;
                if (safe) {
                    if (event.dy() > 0) {
                        if (menu != null) menu.hideMenu(true);
                        if (fab  != null) fab.hide(true);
                    } else {
                        if (menu != null) menu.showMenu(true);
                        if (fab  != null) fab.show(true);
                    }
                }
            }
        });
    }

    //====================================
    public static void showFab(RecyclerView recView, FloatingActionButton fab) {
        new FabScroll(fab).rxView(recView);
    }

    public static void showFab(RecyclerView recView, FloatingActionMenu fab) {
        new FabScroll(fab).rxView(recView);
    }

}
