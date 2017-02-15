package seiko.neiko.models;

import zlc.season.practicalrecyclerview.ItemType;

/**
 * Created by Seiko on 2016/11/12. YiKu
 */

public class DownBookBean implements ItemType {
    public static final int START = 0;
    public static final int PAUSE = 1;
    public static final int DONE = 2;

    private String title;
    private String url;
    private String image;
    private String bkey;
    private String source;
    private int dtype;
    private int progress;
    private int total;
    private int state;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setBkey(String bkey) {
        this.bkey = bkey;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setDtype(int dtype) {
        this.dtype = dtype;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getImage() {
        return image;
    }

    public String getBkey() {
        return bkey;
    }

    public String getSource() {
        return source;
    }

    public int getDtype() {
        return dtype;
    }

    public int getProgress() {
        return progress;
    }

    public int getTotal() {
        return total;
    }

    public int getState() {
        return state;
    }

    @Override
    public int itemType() {return 0;}
}

