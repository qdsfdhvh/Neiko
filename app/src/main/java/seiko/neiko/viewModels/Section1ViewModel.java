package seiko.neiko.viewModels;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.noear.sited.ISdViewModel;
import org.noear.sited.SdNode;

import java.util.ArrayList;
import java.util.List;

import seiko.neiko.models.Book;
import seiko.neiko.models.ImgUrlBean;

/**
 * Created by Seiko on 2016/9/2.
 *
 */
public class Section1ViewModel extends ViewModelBase implements ISdViewModel {

    public final ArrayList<Book> sectionList;
    public final List<ImgUrlBean>  imgList;

    public Section1ViewModel() {
        sectionList = new ArrayList<>();
        imgList = new ArrayList<>();
    }
    public void clear(){
        sectionList.clear();
        imgList.clear();
    }


    @Override
    public void loadByConfig(SdNode config){}

    @Override
    public void loadByJson(SdNode config, String... jsons) {
        if(jsons == null || jsons.length==0)
            return;

        for (String json : jsons) {  //支持多个数据块加载
            loadByJsonData(config, json);
        }
    }


    private void loadByJsonData(SdNode config, String json) {
//        JsonArray data;
//        JsonElement element = new JsonParser().parse(json);
//        if (element.isJsonArray()) {
//            data = element.getAsJsonArray();
//        } else {
//            data = element.getAsJsonObject().get("list").getAsJsonArray();
//        }
        JsonElement element = getElement(json);
        JsonArray data = asAry(element);
        if (data == null) {
            data = asObj(element).get("list").getAsJsonArray();
        }

        int i= 0;
        for (JsonElement el:data) {
            String url = el.getAsString();

            Book book = new Book();
            book.setSection_url(url);
            book.setPath(null);
            book.setIndex(i);
            sectionList.add(book);

            ImgUrlBean bean = new ImgUrlBean();
            bean.setIndex(i);
            bean.setUrl(url);
            imgList.add(bean);
            i++;
        }

    }
}
