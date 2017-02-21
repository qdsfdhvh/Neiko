package okhttp;

/**
 * Created by Seiko on 2017/2/16. Y
 */

import android.os.Handler;
import android.os.Looper;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Seiko on 2016/12/15.
 * OkHttp回调
 */

public abstract class OkCallback<T> implements Callback {

    private static Handler mHandler = new Handler(Looper.getMainLooper());

    private OkBaseParser<T> mParser;

    protected OkCallback(OkBaseParser<T> mParser) {
        if (mParser == null) {
            throw new IllegalArgumentException("Parser can't be null");
        }
        this.mParser = mParser;
    }

    @Override
    public void onFailure(Call call, IOException e) {mHandler.post(() -> onFailure(e));}

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        final int code = mParser.getCode();
        try {
            final T t = mParser.parseResponse(response);
            if (response.isSuccessful() && t != null) {
                mHandler.post(() -> {
                    List<String> cookies = response.headers("Set-Cookie");
                    String cookie = "";
                    for (int i=cookies.size()-1; i>=0; i--) {
                        cookie = cookie + cookies.get(i).replace("path=/", "") + " ";
                    }
                    onSuccess(cookie, t);
                });
            } else {
                mHandler.post(() -> onFailure(new Exception(Integer.toString(code)) ));
            }
        } catch (final Exception e) {
            mHandler.post(() -> onFailure(e));
        }
    }


    public abstract void onSuccess(String cookies, T t);

    public abstract void onFailure(Throwable e);

    void onStart() {}

}
