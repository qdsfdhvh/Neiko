package seiko.neiko.models;

/**
 * Created by Seiko on 2017/2/14. Y
 */

public class SizeModel {
    private String url;
    private int height;
    private int width;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public boolean isNull() {
        return height == 0 || width == 0;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }
}
