package seiko.neiko.models;

/**
 * Created by Seiko on 2016/9/2. Y
 */

public class ViewSetModel {
    public int view_model;      //阅读模式  old
    public int view_direction;  //反向加载  old
    public int view_scale;      //图片缩放 X  old
    public int view_orientation;//屏幕缩放 X  old

    public ViewSetModel() {
        view_model       = 0;    // 阅读模式 0:流水 1:点击 2:日漫
        view_direction   = 0;    //反向加载 0:否 1:是
        view_scale       = 0;
        view_orientation = 0;
    }

    public int getView_model() {
        return view_model;
    }

    public int getView_direction() {
        return view_direction;
    }

    public int getView_scale() {
        return view_scale;
    }

    public int getView_orientation() {
        return view_orientation;
    }
}
