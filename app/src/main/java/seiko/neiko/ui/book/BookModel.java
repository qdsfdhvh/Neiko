package seiko.neiko.ui.book;

/**
 * Created by Seiko on 2017/1/24. Y
 */

public class BookModel {

    public int dtype;
    public String logo;
    public String bookUrl;

    public BookModel(int dtype, String bookUrl, String logo) {
        this.dtype = dtype;
        this.bookUrl = bookUrl;
        this.logo = logo;
    }
}
