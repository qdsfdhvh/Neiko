package org.noear.sited;

import android.text.TextUtils;
import android.util.Log;
import android.widget.Switch;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuety on 16/9/9. Y
 */

public class HttpMessage {
    public Map<String, String> header = new HashMap<>();
    public Map<String, String> form = new HashMap<>();
    public String url;

    public int tag;

    public HttpCallback callback;

    public SdNode config;


    //可由cfg实始化
    public String encode;
    public String ua;
    public String method;

    HttpMessage() {
    }

    HttpMessage(SdNode cfg, String url) {
        this.config = cfg;
        this.url = url;
        rebuild(null);
    }

    HttpMessage(SdNode cfg, String url, int tag, Map<String, String> args) {
        this.config = cfg;
        this.url = url;
        this.tag = tag;
        if (args != null) form = args;
        rebuild(null);
    }


    void rebuild(SdNode cfg) {
        if (cfg != null) {
            this.config = cfg;
        }

        ua = config.ua();
        encode = config.encode();
        method = config.method;


        if (config.isInCookie()) {
            String cookies = config.cookies(url);
            if (cookies != null) {
                header.put("Cookie", cookies);
            }
        }

        if (config.isInReferer()) {
            header.put("Referer", config.getReferer(url));
        }

        if (!config.isEmptyHeader()) {
            for (String kv : config.getHeader(url).split(";")) {
                String[] kv2 = kv.split("=");
                if (kv2.length == 2) {
                    header.put(kv2[0], kv2[1]);
                }
            }
        }
    }


    void rebuildForm(Map<String,String> data) {doBuildForm(true, 0, null, data);}

    void rebuildForm(int page, String key) {doBuildForm(false, page, key, null);}

    private void doBuildForm(boolean isData, int page, String key, Map<String,String> data){
        if ("post".equals(config.method)) {
            String _strArgs;

            if(!isData) {
                if (key != null) {
                    _strArgs = config.getArgs(url, key, page+"");
                } else {
                    _strArgs = config.getArgs(url, page+"");
                }
            }else{
                _strArgs = config.getArgs(url, Util.toJson(data));
            }

            if (!TextUtils.isEmpty(_strArgs)) {

                Log.v("Post.Args", _strArgs);

                for (String kv : _strArgs.split(";")) {
                    Log.d("Post", kv);
                    if (kv.length() > 3) {
                        String name = kv.split("=")[0];
                        String value = kv.split("=")[1];

                        switch (value) {
                            case "@key":
                                form.put(name, key);
                                break;
                            case "@page":
                                form.put(name, page + "");
                                break;
                            default:
                                form.put(name, value);
                                break;
                        }
                    }
                }

            }
        }
    }
}
