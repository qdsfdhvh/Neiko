package seiko.neiko.models;

import zlc.season.practicalrecyclerview.ItemType;

/**
 * Created by Seiko on 2016/11/25. YiKu
 */

public class ImgUrlBean implements ItemType {

    private String url;
    private int index;

    public void setIndex(int index) {
        this.index = index;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getIndex() {
        return index;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public int itemType() {
        return 0;
    }

//    @Override
//    public int describeContents() {
//        return 0;
//    }

//    @Override
//    public void writeToParcel(Parcel parcel, int i) {
//        parcel.writeString(url);
//        parcel.writeInt(index);
//    }
//
//
//    public static final Creator<ImgUrlBean> CREATOR = new Creator<ImgUrlBean>() {
//        @Override
//        public ImgUrlBean createFromParcel(Parcel in) {
//
//            ImgUrlBean part = new ImgUrlBean();
//            part.setUrl(in.readString());
//            part.setIndex(in.readInt());
//
//            return part;
//        }
//
//        @Override
//        public ImgUrlBean[] newArray(int size) {
//            return new ImgUrlBean[size];
//        }
//    };
}
