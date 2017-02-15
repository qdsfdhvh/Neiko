package seiko.neiko.viewModels;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.noear.sited.ISdViewModel;
import org.noear.sited.SdNode;

import java.util.ArrayList;
import java.util.List;

import seiko.neiko.models.Book;
import seiko.neiko.dao.engine.DdNode;

/**
 * Created by Seiko on 2016/9/14.
 *
 */

public class SearchViewModel extends ViewModelBase implements ISdViewModel {
    public SearchViewModel() {
        currentPage = 1;
        mDatas = new ArrayList<>();
    }

    public int currentPage;
    public List<Book> mDatas;
    private int dtype;


    public  void clear() {
        mDatas.clear();
    }

    @Override
    public void loadByConfig(SdNode c) {

    }

    @Override
    public void loadByJson(SdNode c, String... jsons) {
        if (jsons == null || jsons.length == 0)
            return;
        DdNode config = (DdNode) c;
        dtype = config.s().main.dtype();

        for (String json : jsons) { //支持多个数据块加载
            loadByJsonData(config, json);
        }
    }

    private void loadByJsonData(SdNode config, String json) {
        JsonArray data = new JsonParser().parse(json).getAsJsonArray();

        if (data.isJsonArray()) {
            for (JsonElement el:data) {
                JsonObject n = el.getAsJsonObject();
                String name = getString(n, "name");
                String url = getString(n,"url");
                String logo = getString(n, "logo");
                String author = getString(n, "author");
                String source = config.source.title;
                mDatas.add(new Book(name, logo, url, author, dtype, source));
            }
        }
    }

}
