package seiko.neiko.ui.book;

import zlc.season.practicalrecyclerview.ItemType;

/**
 * Created by Seiko on 2017/1/24. Y
 */

public class BookModel implements ItemType {

    //==========================
    /** new */
    public int dtype;
    public String name;
    public String url;
    public String logo;
    public String author;
    public String source;

    public BookModel(int dtype, String url, String logo) {
        this.dtype = dtype;
        this.url = url;
        this.logo = logo;
    }

    public BookModel(int dtype, String name, String url, String logo, String author) {
        this.dtype = dtype;
        this.name = name;
        this.url = url;
        this.logo = logo;
        this.author = author;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setDtype(int dtype) {
        this.dtype = dtype;
    }

    @Override
    public int itemType() {
        return 0;
    }
}
