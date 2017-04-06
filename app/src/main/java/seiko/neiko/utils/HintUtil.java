package seiko.neiko.utils;

import android.os.Handler;
import android.widget.Toast;

import seiko.neiko.app.App;

/**
 * Created by Seiko on 2016/9/6.
 *
 */
public class HintUtil {

    public static void show(String msg) {
        final Toast toast= Toast.makeText(App.getContext(), msg, Toast.LENGTH_SHORT);

        toast.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, 1000);
    }

}
