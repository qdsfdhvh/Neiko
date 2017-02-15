package seiko.neiko.models;

import android.net.Uri;

/**
 * Created by Seiko on 2016/12/21.
 * 音频视频模型
 */

public class MediaModel extends ModelBase {
    public String url;
    public String type;//用于下载
    public String mime;//用于下载
    public String logo;//用于下载

    public MediaModel(String url){
        this.url = url;
    }

    public MediaModel(String url, String type, String mime, String logo) {
        this.url  = url;
        this.type = type;
        this.mime = mime;
        this.logo = logo;
    }

    public Uri getUri(){
        return Uri.parse(url);
    }

    //用于下载
    public String fileFullName(String fileName) {
        return fileName + type;
    }
}
