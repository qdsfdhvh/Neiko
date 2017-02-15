package seiko.neiko.models;

/**
 * Created by Seiko on 2016/10/2.YiKu
 */

public class BookPage {

    private int book_pages;      //章节_总页数
    private String book_title;   //章节_标题

    private int book_index;   //章节_位置
    private String book_url;     //章节_链接

    public BookPage() {
    }

    public void setBook_pages(int book_pages) {
        this.book_pages = book_pages;
    }

    public void setBook_title(String book_title) {
        this.book_title = book_title;
    }

    public void setBook_index(int book_index) {
        this.book_index = book_index;
    }

    public void setBook_url(String book_url) {
        this.book_url = book_url;
    }

    public int getBook_pages() {
        return book_pages;
    }

    public String getBook_title() {
        return book_title;
    }

    public int getBook_index() {
        return book_index;
    }

    public String getBook_url() {
        return book_url;
    }

}
