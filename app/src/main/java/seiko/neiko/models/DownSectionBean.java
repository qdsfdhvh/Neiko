package seiko.neiko.models;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import zlc.season.practicalrecyclerview.ItemType;

/**
 * Created by Seiko on 2016/11/22. YiKu
 */

public class DownSectionBean implements ItemType, Parcelable {

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

    private ArrayList<ImgUrlBean> list;

    public DownSectionBean() {

    }

    //========================================

    public void setList(ArrayList<ImgUrlBean> list) {
        this.list = list;
    }

    public ArrayList<ImgUrlBean> getList() {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(type);
        dest.writeString(bkey);
        dest.writeString(source);
        dest.writeString(bookUrl);
        dest.writeString(bookName);
        dest.writeString(section);
        dest.writeString(sectionurl);
        dest.writeInt(success);
        dest.writeInt(failed);
        dest.writeInt(total);
        dest.writeInt(status);
        dest.writeString(path);
        dest.writeInt(index);
        dest.writeByte((byte) (isAdd ? 1 : 0));
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(DATA_KEY, list);
        dest.writeBundle(bundle);
    }

    private String DATA_KEY = "a";

    private DownSectionBean(Parcel in) {
        type = in.readInt();
        bkey = in.readString();
        source = in.readString();
        bookUrl = in.readString();
        bookName = in.readString();
        section = in.readString();
        sectionurl = in.readString();
        success = in.readInt();
        failed = in.readInt();
        total = in.readInt();
        status = in.readInt();
        path = in.readString();
        index = in.readInt();
        isAdd = in.readByte() != 0;
        list = in.readBundle(getClass().getClassLoader()).getParcelableArrayList(DATA_KEY);
    }

    public static final Creator<DownSectionBean> CREATOR = new Creator<DownSectionBean>() {
        @Override
        public DownSectionBean createFromParcel(Parcel in) {
            return new DownSectionBean(in);
        }

        @Override
        public DownSectionBean[] newArray(int size) {
            return new DownSectionBean[size];
        }
    };

    //==============================
}
