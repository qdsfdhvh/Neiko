package seiko.neiko.viewModels;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.noear.sited.ISdViewModel;
import org.noear.sited.SdNode;

import java.util.ArrayList;

import seiko.neiko.models.TxtModel;

/**
 * Created by Seiko on 2016/9/25. YiKu
 */

public class Section2ViewModel extends ViewModelBase implements ISdViewModel {

    public final ArrayList<TxtModel> items;
    public String url;

    public void clear(){
        items.clear();
    }


    public Section2ViewModel(String url) {
        this.url = url;
        items = new ArrayList<>();
    }


    public void addTitleItem(String d, int t) {
        TxtModel txt = new TxtModel(url, d, t, null);
        items.add(txt);
    }


    @Override
    public void loadByConfig(SdNode config){}

    @Override
    public void loadByJson(SdNode config, String... jsons) {
        if (jsons == null || jsons.length == 0)
            return;

        for (String json : jsons) {   //支持多个数据块加载
            loadByJsonData(config, json);
        }
    }

    private void loadByJsonData(SdNode config, String json) {
        JsonArray list;
        JsonElement data = new JsonParser().parse(json);

        if(data.isJsonObject())
            list = data.getAsJsonObject().get("list").getAsJsonArray();
        else
            list = data.getAsJsonArray();

        for (JsonElement el:list) {
            JsonObject n = el.getAsJsonObject();
            TxtModel txt = new TxtModel(url,
                    getString(n,"d"),
                    getInt(n,"t"),
                    getString(n, "c"),
                    getString(n, "url"));
            items.add(txt);
        }
    }

}
