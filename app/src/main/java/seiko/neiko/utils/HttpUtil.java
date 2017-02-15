package seiko.neiko.utils;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.Map;

/**
 * Created by Seiko on 2016/12/15. Y
 */

public class HttpUtil {

    /*发起get请求，并返回String结果*/
    public static void get(String url, final Act2<Integer,String> callback) {
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(null, url, null, new TextHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String s, Throwable throwable) {
                callback.run(-2,null);
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, String s) {
                if (s == null)
                    callback.run(-1, null);
                else
                    callback.run(1, s);
            }
        });
    }

    /*发起post请求，并返回String结果*/
    public static void post(String url, Map<String,String> params, final Act2<Integer, String> callback) {
        RequestParams postData = new RequestParams(params);

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(null, url, postData, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String s, Throwable throwable) {
                callback.run(-2,null);
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, String s) {
                if (s == null)
                    callback.run(-1, null);
                else
                    callback.run(1, s);
            }
        });
    }


    public interface Act2<T1, T2> {
        void run(T1 t1, T2 t2);
    }
}
