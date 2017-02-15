package seiko.neiko.models;

import java.util.List;

/**
 * Created by Seiko on 2016/12/21. Y
 */

public class BookInfBean {
    private List<Book> list;
    private String bookUrl;
    private String bookName;
    private String logo;
    private String bkey;

    public BookInfBean(List<Book> list,String bookName, String bookUrl, String logo, String bkey) {
        this.list = list;
        this.bookName = bookName;
        this.bookUrl = bookUrl;
        this.logo = logo;
        this.bkey = bkey;
    }

    public String getBookName() {
        return bookName;
    }

    public String getBookUrl() {
        return bookUrl;
    }

    public String getLogo() {
        return logo;
    }

    public List<Book> getList() {
        return list;
    }

    public String getBkey() {
        return bkey;
    }
}
