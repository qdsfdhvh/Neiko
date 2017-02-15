package seiko.neiko.models;

import zlc.season.practicalrecyclerview.ItemType;

/**
 * Created by Seiko on 2016/9/25. Y
 */

public class TxtModel extends ModelBase implements ItemType {
    public final String data;
    public final int type;
    public final String color;
    public final String url;

    public final String referer;

    public TxtModel(String referer,  String data, int type, String color){
        this.referer  = referer;

        this.data     = data;
        this.type     = type;
        this.color    = color;
        this.url      = null;
    }

    public TxtModel(String referer,  String data, int type, String color, String url){
        this.referer  = referer;

        this.data     = data;
        this.type     = type;
        this.color    = color;
        this.url      = url;
    }

    @Override
    public int itemType() {
        return 0;
    }
}
