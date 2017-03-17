package seiko.neiko.ui.tag;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.noear.sited.ISdViewModel;
import org.noear.sited.SdNode;

import java.util.ArrayList;
import java.util.List;

import seiko.neiko.dao.engine.DdNode;
import seiko.neiko.models.Book;
import seiko.neiko.ui.book.BookModel;
import seiko.neiko.utils.HintUtil;
import seiko.neiko.viewModels.ViewModelBase;

/**
 * Created by Seiko on 2016/9/10. YiKu
 */

class TagViewModel extends ViewModelBase implements ISdViewModel {

    int currentPage;
    List<BookModel> list;

    TagViewModel() {
        list = new ArrayList<>();
        currentPage = 1;
    }

    void clear() {list.clear();}

    @Override
    public void loadByConfig(SdNode config) {}

    @Override
    public void loadByJson(SdNode config, String... jsons) {
        if(jsons == null || jsons.length == 0)
            return;

        for(String json : jsons) { //支持多个数据块加载
            loadByJsonData(config, json);
        }
    }

    private void loadByJsonData(SdNode config, String json) {
        JsonElement data = new JsonParser().parse(json);

        if (data.isJsonArray()) {
            for (JsonElement el:data.getAsJsonArray()) {
                JsonObject n = el.getAsJsonObject();
                String name  = getString(n,"name");
                String url   = getString(n,"url");
                String logo  = getString(n,"logo");
                String author = getString(n,"author");
                list.add(new BookModel(((DdNode) config).s().book(url).dtype(), name, url, logo, author));
            }
        }
    }

}
