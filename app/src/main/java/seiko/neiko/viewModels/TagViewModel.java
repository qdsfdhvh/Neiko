package seiko.neiko.viewModels;

import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import org.noear.sited.ISdViewModel;
import org.noear.sited.SdNode;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import seiko.neiko.models.Book;
import seiko.neiko.utils.HintUtil;

/**
 * Created by Seiko on 2016/9/10. YiKu
 */

public class TagViewModel extends ViewModelBase implements ISdViewModel {

    public TagViewModel() {
        mDatas = new ArrayList<>();
        currentPage = 1;
    }

    public int currentPage;
    public ArrayList<Book> mDatas;

    @Override
    public void loadByConfig(SdNode config){}

    @Override
    public void loadByJson(SdNode config, String... jsons) {
        if(jsons == null || jsons.length == 0)
            return;

        for(String json : jsons) { //支持多个数据块加载
            loadByJsonData(config, json);
        }
    }

    private void loadByJsonData(SdNode config, String json) {
        try {
            JsonElement data = new JsonParser().parse(json);

            if (data.isJsonArray()) {
                mDatas.clear();
                for (JsonElement el:data.getAsJsonArray()) {
                    JsonObject n = el.getAsJsonObject();
                    String name  = getString(n,"name");
                    String url   = getString(n,"url");
                    String logo  = getString(n,"logo");
                    String author = getString(n,"author");
                    mDatas.add(new Book(name, logo, url, author));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            HintUtil.show("解析出错");
        }
    }

}
