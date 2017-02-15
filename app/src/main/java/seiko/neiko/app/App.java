package seiko.neiko.app;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.os.StrictMode;
import android.util.DisplayMetrics;

import seiko.neiko.dao.engine.DdApi;
import seiko.neiko.dao.engine.DdNodeFactory;

/**
 * Created by Seiko on 2016/8/28. YiKu
 */
public class App extends Application {

    private static App mCurrent;
    private String _PATH;

    public void onCreate() {
        super.onCreate();
        mCurrent = this;

        _PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/Neiko/";
        if (!_PATH.endsWith("/")) {
            _PATH += "/";
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        DdApi.tryInit(new DdNodeFactory(), null);
    }

    public static App getCurrent(){return mCurrent;}

    public static Context getContext() {return mCurrent.getApplicationContext();}

    public  static DisplayMetrics getDisplayMetrics(){
        return getContext().getResources().getDisplayMetrics();
    }

    public String getBasePath() {return _PATH;}

}
