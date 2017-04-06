package seiko.neiko.view;

import java.util.List;

import seiko.neiko.ui.book.BookModel;

/**
 * Created by Seiko on 2017/4/5. Y
 */

public interface SearchItemView {

    void onSuccess(List<BookModel> list);

    void onFailed();

}
