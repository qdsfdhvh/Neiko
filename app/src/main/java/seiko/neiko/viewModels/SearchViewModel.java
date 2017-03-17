package seiko.neiko.viewModels;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.noear.sited.ISdViewModel;
import org.noear.sited.SdNode;

import java.util.ArrayList;
import java.util.List;

import seiko.neiko.dao.engine.DdNode;
import seiko.neiko.ui.book.BookModel;

/**
 * Created by Seiko on 2016/9/14.
 *
 */

public class SearchViewModel extends ViewModelBase implements ISdViewModel {

    public List<BookModel> list;
    private int dtype;
    private String source;


    public SearchViewModel() {
        list = new ArrayList<>();
    }

    public  void clear() {
        list.clear();
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
        source = config.source.title;

        for (String json : jsons) { //支持多个数据块加载
            loadByJsonData(json);
        }
    }

    private void loadByJsonData(String json) {
        JsonArray data = new JsonParser().parse(json).getAsJsonArray();

        if (data.isJsonArray()) {
            for (JsonElement el:data) {
                JsonObject n = el.getAsJsonObject();
                String name = getString(n, "name");
                String url = getString(n,"url");
                String logo = getString(n, "logo");
                String author = getString(n, "author");

                BookModel book = new BookModel(dtype, name, url, logo, author);
                book.setSource(source);
                list.add(book);
            }
        }
    }

}
