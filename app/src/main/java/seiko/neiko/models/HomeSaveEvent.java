package seiko.neiko.models;

import java.util.List;

/**
 * Created by Seiko on 2016/12/22. Y
 */

public class HomeSaveEvent {
    private List<Book> hotList;
    private List<Book> tagList;

    public HomeSaveEvent(List<Book> hotList, List<Book> tagList) {
        this.hotList = hotList;
        this.tagList = tagList;
    }

    public List<Book> getHotList() {
        return hotList;
    }

    public List<Book> getTagList() {
        return tagList;
    }
}
