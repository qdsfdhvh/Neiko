package seiko.neiko.models;

import java.util.List;

import rx.subscriptions.CompositeSubscription;
import zlc.season.practicalrecyclerview.ItemType;

/**
 * Created by Seiko on 2016/11/22. YiKu
 */

public class DownSectionBean implements ItemType {

    private int type;
    private String bkey;
    private String source;

    private String bookUrl;
    private String bookName;

    private String section;
    private String sectionurl;

    private int success;
    private int failed;

    private int total;
    private int status;
    private String path;

    private int index;
    private boolean isAdd;

    private List<ImgUrlBean> list;

    //========================================

    public void setList(List<ImgUrlBean> list) {
        this.list = list;
    }

    public List<ImgUrlBean> getList() {
        return list;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSource() {
        return source;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookName() {
        return bookName;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public void setSectionurl(String sectionurl) {
        this.sectionurl = sectionurl;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getSection() {
        return section;
    }

    public String getSectionurl() {
        return sectionurl;
    }

    public int getStatus() {
        return status;
    }

    //==============================


    public void setType(int type) {
        this.type = type;
    }

    public void setBkey(String bkey) {
        this.bkey = bkey;
    }

    public void setBookUrl(String bookUrl) {
        this.bookUrl = bookUrl;
    }

    public int getType() {
        return type;
    }

    public String getBkey() {
        return bkey;
    }

    public String getBookUrl() {
        return bookUrl;
    }
    //==============================


    public void setSuccess(int success) {
        this.success = success;
    }

    public void setFailed(int failed) {
        this.failed = failed;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getSuccess() {
        return success;
    }

    public int getFailed() {
        return failed;
    }

    public int getTotal() {
        return total;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    //==============================


    public void setAdd(boolean add) {
        isAdd = add;
    }

    public boolean getAdd() {
        return isAdd;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public int itemType() {
        return 0;
    }

    //==============================
}
