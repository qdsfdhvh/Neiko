package seiko.neiko.view;

import java.util.List;

import seiko.neiko.models.SourceModel;

/**
 * Created by Seiko on 2017/2/25. Y
 */

public interface SitedItemView {

    void onLoadSuccess(List<SourceModel> menu);

    void onLoadFailed();
}
