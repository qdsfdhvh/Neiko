package seiko.neiko.ui.tag;

import java.util.List;

import seiko.neiko.ui.book.BookModel;

/**
 * Created by Seiko on 2017/2/26. Y
 */

interface TagView {
    void onLoadSuccess(List<BookModel> list);

    void onLoadFailed();
}
