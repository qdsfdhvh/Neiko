package okhttp;

import java.io.IOException;

import okhttp3.Response;

/**
 * Created by Seiko on 2016/12/15.
 * 返回html文本
 */

public class OkTextParser extends OkBaseParser<String> {

    private String code;

    public OkTextParser() {this.code = "UTF-8";}

    public OkTextParser(String code) {this.code = code;}

    @Override
    public String parse(Response response) throws IOException {

        if (response.isSuccessful()) {
            byte[] b = response.body().bytes();
            return new String(b, code);
        }

        return null;
    }
}

