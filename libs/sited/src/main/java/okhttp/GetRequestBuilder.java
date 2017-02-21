package okhttp;

import android.text.TextUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Seiko on 2016/12/15. Y
 */

public class GetRequestBuilder<T> extends RequestBuilder<T> {

    public GetRequestBuilder url(String url) {
        this.url = url;
        return this;
    }

    public GetRequestBuilder setParams(Map<String, String> params) {
        this.params = params;
        return this;
    }

    public GetRequestBuilder setHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public GetRequestBuilder addParams(String key, String value) {
        if (params == null) {
            params = new HashMap<>();
        }
        params.put(key, value);
        return this;
    }

    public GetRequestBuilder tag(Object tag) {
        this.tag = tag;
        return this;
    }

    @Override
    public Call enqueue(Callback callback) {

        if (TextUtils.isEmpty(url)) {
            throw new IllegalArgumentException("url can not be null !");
        }

        Request.Builder builder = new Request.Builder().url(url);

        if (tag != null) {
            builder.tag(tag);
        }


        if (params != null && params.size() > 0) {
            url = appendParams(url, params);
        }

        if(headers!=null && headers.size()>0){
            for (String key :headers.keySet()){
                builder.addHeader(key, headers.get(key));
            }
        }

        Request request = builder.build();

        if (callback instanceof OkCallback) {
            ((OkCallback) callback).onStart();
        }

        Call call = OkHttpProxy.getInstance().newCall(request);
        call.enqueue(callback);
        return call;
    }

    @Override
    public Response execute() throws IOException {

        if (TextUtils.isEmpty(url)) {
            throw new IllegalArgumentException("url can not be null !");
        }

        Request.Builder builder = new Request.Builder().url(url);

        if (tag != null) {
            builder.tag(tag);
        }

        if (params != null && params.size() > 0) {
            url = appendParams(url, params);
        }


        Request request = builder.build();

        Call call = OkHttpProxy.getInstance().newCall(request);
        return call.execute();
    }

}

