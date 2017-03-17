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

import seiko.neiko.models.Book;
import seiko.neiko.dao.engine.DdSource;

/**
 * Created by Seiko on 2016/8/30.
 *
 */
@SuppressWarnings("All")
public class MainViewModel extends ViewModelBase implements ISdViewModel {
    public final List<Book> hotList;
    public final List<Book> tagList;

    public MainViewModel() {
        hotList = new ArrayList<>();
        tagList = new ArrayList<>();
    }

    public void clear() {
        tagList.clear();
        hotList.clear();
    }

    @Override
    public void loadByConfig(SdNode config) {
        if (DdSource.isHots(config)) {
            hotList.clear();
            for (SdNode t1 : config.items()) {
                hotList.add(new Book(t1.title, t1.logo, t1.url));
            }
        }

        if (DdSource.isTags(config)) {
            tagList.clear();
            for (SdNode t1 : config.items()) {
                tagList.add(new Book(t1.title, t1.url));
            }
        }


    }

    @Override
    public void loadByJson(SdNode config, String... jsons) {
        if(jsons == null || jsons.length==0)
            return;

        for(String json : jsons){ //支持多个数据块加载
            loadByJsonData(config, json);
        }
    }

    private void loadByJsonData(SdNode config, String json) {
        JsonElement element = new JsonParser().parse(json);

        if (!element.isJsonArray())
            return;

        JsonArray data = element.getAsJsonArray();

        if (DdSource.isHots(config)) {
            for (JsonElement el:data) {
                JsonObject n = el.getAsJsonObject();
                String name = getString(n, "name");
                String logo = getString(n, "logo");
                String url  = getString(n, "url");
                hotList.add(new Book(name, logo, url));
            }
        }

        if (DdSource.isUpdates(config)) {
            for (JsonElement el:data) {
                JsonObject n = el.getAsJsonObject();
                String name = getString(n, "name");
                String logo = getString(n, "logo");
                String url  = getString(n, "url");
                if (!TextUtils.isEmpty(logo))
                    hotList.add(new Book(name, logo, url));
            }
        }

        if(DdSource.isTags(config)){
            for (JsonElement el:data) {
                JsonObject n = el.getAsJsonObject();
                String title = getString(n, "title");
                String url   = getString(n, "url");
                tagList.add(new Book(title, url));
            }
        }
    }

}
