package org.noear.sited;

/**
 * Created by yuety on 15/10/10. Y
 */
public interface HttpCallback {
    void run(Integer code, HttpMessage msg, String text, String url302);
}
