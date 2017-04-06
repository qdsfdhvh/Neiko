package seiko.neiko.viewModels;

import android.text.TextUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.noear.sited.ISdViewModel;
import org.noear.sited.SdNode;

import java.util.ArrayList;
import java.util.List;

import seiko.neiko.models.Book;
import seiko.neiko.dao.db.DbApi;
import seiko.neiko.dao.engine.DdSource;
import seiko.neiko.models.ImgUrlBean;
import seiko.neiko.utils.EncryptUtil;

/**
 * Created by Seiko on 2016/8/31.
 *
 */

public class BookViewModel extends ViewModelBase implements ISdViewModel {

    public List<Book> sectionList;
    public List<ImgUrlBean> list;
    public Book book;

    public BookViewModel(String source, String url, int dtype) {
        sectionList = new ArrayList<>();
        list = new ArrayList<>();
        this._dtype = dtype;
        this.source = source;

        bookUrl = url;
        bookKey = EncryptUtil.md5(url);

        intro="";

        DbApi.logBID(source, bookKey, bookUrl);
    }

    public void clear() {
        if (list == null) {
            list = new ArrayList<>();
        }
        if (sectionList == null) {
            sectionList = new ArrayList<>();
        }
        list.clear();
        sectionList.clear();
        i = 0;
    }

    public final String bookKey;//根据bookUrl生成
    public final String bookUrl;

    public String source;
    public String name;
    public String author;
    public String intro;
    public String logo;

    private boolean isSectionsAsc = false;//输出的section是不是顺排的

    private int _dtype = -1;
    public int dtype() {
        return _dtype;
    }


    @Override
    public void loadByConfig(SdNode config){}

    @Override
    public void loadByJson(SdNode config, String... jsons) {
        if(jsons == null || jsons.length==0)
            return;

        //加载无目录图片组会出错。暂时不用Gson解析图片组
        for (String json : jsons) {
            loadByJsonData(config, json);
        }
    }

    private int i;
    private void loadByJsonData(SdNode config, String json) {
        JsonObject data;
        JsonElement element = new JsonParser().parse(json);

        if (element.isJsonObject()) {
            data = element.getAsJsonObject();

            if (DdSource.isBook(config)) {
                if (TextUtils.isEmpty(name)) {
                    name   = getString(data, "name");
                    author = getString(data, "author");
                    intro  = getString(data, "intro");
                    logo   = getString(data, "logo");
                    isSectionsAsc = getInt(data, "isSectionsAsc") > 0;

                    book = new Book(name, logo, bookUrl, dtype(), source, bookKey);
                }
            }

            JsonArray s2 = data.getAsJsonArray("sections");
            for (JsonElement el:s2) {
                JsonObject n = el.getAsJsonObject();
                String url = getString(n, "url");
                String name = getString(n, "name");
                if (isSectionsAsc) {
                    sectionList.add(0, new Book(name, url, i));
                } else {
                    sectionList.add(new Book(name, url, i));
                }
                i++;
            }

        } else if (element.isJsonArray()) {
            for (JsonElement el:element.getAsJsonArray()) {
//                sectionList.add(new Book("", el.getAsString(), i));
                ImgUrlBean bean = new ImgUrlBean();
                bean.setUrl(el.getAsString());
                bean.setIndex(i);
                list.add(bean);
                i++;
            }
        }
    }


}
