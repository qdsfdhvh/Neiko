package seiko.neiko.viewModels;

import android.text.TextUtils;
import android.util.Log;

import com.dou361.ijkplayer.bean.VideoijkBean;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.noear.sited.ISdViewModel;
import org.noear.sited.SdNode;

import java.util.ArrayList;
import java.util.List;

import seiko.neiko.models.MediaModel;


/**
 * Created by Seiko on 2016/9/7. YiKu
 */
public class VideoViewModel extends ViewModelBase implements ISdViewModel {

    public String name;
    public String logo;
    public List<VideoijkBean> list;
    private int i;

    public VideoViewModel() {
        list = new ArrayList<>();
        i = 1;
    }

    @Override
    public void loadByConfig(SdNode config) {

    }

    @Override
    public void loadByJson(SdNode config, String... jsons) {
        if(jsons == null || jsons.length==0)
            return;

        for (String json: jsons) {
            if(json.startsWith("{") || json.startsWith("[")){

                JsonElement data = new JsonParser().parse(json);
                JsonArray array;
                if (data.isJsonObject()) {
                    array = data.getAsJsonObject().get("list").getAsJsonArray();
                    if (TextUtils.isEmpty(name)) {
                        name = getString(data.getAsJsonObject(), "name");
                        logo = getString(data.getAsJsonObject(), "logo");
                    }
                } else {
                    array = data.getAsJsonArray();
                }

                for (JsonElement el:array) {
                    VideoijkBean bean = new VideoijkBean();
                    bean.setStream("第"+ i +"段");
                    bean.setUrl(getString(el.getAsJsonObject(), "url"));
                    bean.setType(getString(el.getAsJsonObject(), "type"));
                    bean.setMime(getString(el.getAsJsonObject(), "mime"));
                    bean.setLogo(getString(el.getAsJsonObject(), "logo"));
                    list.add(bean);
                    i++;
                }

            } else {
                for (String url:json.split(";")) {
                    if (url.length() > 6) {
                        VideoijkBean bean = new VideoijkBean();
                        bean.setStream("第"+ i +"段");
                        bean.setUrl(url);
                        list.add(bean);
                        i++;
                    }
                }
            }
        }
    }

}
