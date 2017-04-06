package seiko.neiko.models;

import android.os.Parcel;
import android.os.Parcelable;

import zlc.season.practicalrecyclerview.ItemType;

/**
 * Created by Seiko on 2016/11/25. YiKu
 */

public class ImgUrlBean implements ItemType, Parcelable {

    private String url;
    private int index;
    private String path;

    public ImgUrlBean() {

    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    @Override
    public int itemType() {
        return 0;
    }

    //===================================

    private ImgUrlBean(Parcel in) {
        url = in.readString();
        index = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeInt(index);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ImgUrlBean> CREATOR = new Creator<ImgUrlBean>() {
        @Override
        public ImgUrlBean createFromParcel(Parcel in) {
            return new ImgUrlBean(in);
        }

        @Override
        public ImgUrlBean[] newArray(int size) {
            return new ImgUrlBean[size];
        }
    };

}
