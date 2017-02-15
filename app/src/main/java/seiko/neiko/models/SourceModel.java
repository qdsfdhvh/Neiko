package seiko.neiko.models;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import zlc.season.practicalrecyclerview.ItemType;

/**
 * Created by Seiko on 2016/8/29. Y
 */
public class SourceModel implements ItemType {
    public int id;

    public String key;
    public String url;
    public String expr;

    public int ver;
    public String title;
    public String author;
    public String intro;
    public String logo;
    public String sited;

    public int type;

    public String cookies;

    public String src;

    public long subTime;

    /* expr */
    public boolean isMe(String url){

        if(TextUtils.isEmpty(expr))
            return false;

        Pattern pattern = Pattern.compile(expr);
        Matcher m = pattern.matcher(url);

        return m.find();
    }

    @Override
    public int itemType() {
        return 0;
    }
}
