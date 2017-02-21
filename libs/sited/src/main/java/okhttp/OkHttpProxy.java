package okhttp;

import okhttp3.OkHttpClient;

public class OkHttpProxy {

    private static OkHttpClient mHttpClient;

    private static OkHttpClient init() {
        synchronized (OkHttpProxy.class) {
            if (mHttpClient == null) {
                mHttpClient = new OkHttpClient();
            }
        }
        return mHttpClient;
    }

    public static OkHttpClient getInstance() {return mHttpClient == null ? init() : mHttpClient;}

    public static void setInstance(OkHttpClient okHttpClient) {
        OkHttpProxy.mHttpClient = okHttpClient;
    }

    public static GetRequestBuilder get() {return new GetRequestBuilder();}

    public static PostRequestBuilder post() {return new PostRequestBuilder();}
}
