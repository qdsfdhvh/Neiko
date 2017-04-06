package seiko.neiko.app;

import android.os.Bundle;

/**
 * Created by Seiko on 2016/11/18. YiKu
 * activty对应的layout
 */

public interface BaseView {

    int getLayoutId();

    void initViews(Bundle bundle);

}
