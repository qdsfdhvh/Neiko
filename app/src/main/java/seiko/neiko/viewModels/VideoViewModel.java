package seiko.neiko.viewModels;

import android.text.TextUtils;
import android.util.Log;

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
    public List<MediaModel> items = new ArrayList<>();

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
                    items.add(new MediaModel(
                            getString(el.getAsJsonObject(), "url"),
                            getString(el.getAsJsonObject(), "type"),
                            getString(el.getAsJsonObject(), "mime"),
                            getString(el.getAsJsonObject(), "logo")
                    ));
                }

            } else {
                for (String url:json.split(";")) {
                    if (url.length() > 6) {
                        items.add(new MediaModel(url));
                    }
                }
            }
        }
    }

}
