package seiko.neiko.ui.section2;

import java.util.List;

import seiko.neiko.models.Book;

/**
 * Created by Seiko on 2017/1/24. Y
 */

public class Section2Model {
    int dtype;
    String sec_name;
    String sec_url;
    String bookUrl;
    List<Book> imgList;
    int imgindex;

    public Section2Model(int dtype, String sec_name, String sec_url) {
        this.dtype = dtype;
        this.sec_name = sec_name;
        this.sec_url = sec_url;

    }

    public void setList(String bookUrl, List<Book> imgList, int imgindex) {
        this.bookUrl = bookUrl;
        this.imgList = imgList;
        this.imgindex = imgindex;
    }

}
