package seiko.neiko.ui.tag;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.noear.sited.ISdViewModel;
import org.noear.sited.SdNode;
import org.noear.sited.SdSourceCallback;

import java.util.ArrayList;
import java.util.List;

import seiko.neiko.app.BasePresenter;
import seiko.neiko.dao.engine.DdNode;
import seiko.neiko.dao.engine.DdSource;
import seiko.neiko.ui.book.BookModel;
import seiko.neiko.view.TagView;

/**
 * Created by Seiko on 2017/2/26. Y
 */

class TagPresenter extends BasePresenter<TagView> implements ISdViewModel {

    private String tagUrl;
    private DdSource source;
    private List<BookModel> list;
    private int page;

    TagPresenter(DdSource source, String tagUrl) {
        this.source = source;
        this.tagUrl = tagUrl;
        list = new ArrayList<>();
        page = 1;
    }

    void loadData(int page) {
        this.page = page;
        loadData(true);
    }

    void loadData(boolean isRef) {
        if (!isRef) {
            page++;
        }

        list.clear();
        source.getNodeViewModel(this, false, page, tagUrl, source.tag(tagUrl), new SdSourceCallback() {
            @Override
            public void run(Integer code) {
                if (code == 1) {
                    mBaseView.onLoadSuccess(list);
                }
            }
        });
    }

    int getPage() {return page;}

    @Override
    public void loadByConfig(SdNode config) {

    }

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
