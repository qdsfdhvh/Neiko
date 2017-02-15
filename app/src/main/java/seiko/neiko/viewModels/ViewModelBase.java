package seiko.neiko.viewModels;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import seiko.neiko.models.ModelBase;

/**
 * Created by Seiko on 2016/8/30. Y
 */
public class ViewModelBase extends ModelBase {
    public int tag;

    protected String getString(JsonObject data, String key) {
        return data.get(key) != null ? data.get(key).getAsString():"";
    }

    protected int getInt(JsonObject data, String key) {
        return data.get(key) != null ? data.get(key).getAsInt():0;
    }

    //====================================================
    protected JsonElement getElement(String json) {
        return new JsonParser().parse(json);
    }

    protected JsonArray asAry(JsonElement element) {
        if (!element.isJsonArray()) {
            return null;
        }
        return element.getAsJsonArray();
    }

    protected JsonObject asObj(JsonElement element) {
        if (!element.isJsonObject()) {
            return null;
        }
        return element.getAsJsonObject();
    }


}
