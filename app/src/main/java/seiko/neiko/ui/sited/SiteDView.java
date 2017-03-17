package seiko.neiko.ui.sited;

import java.util.List;

import seiko.neiko.models.SourceModel;

/**
 * Created by Seiko on 2017/2/25. Y
 */

interface SitedView {
    void onLoadSuccess(List<SourceModel> menu);

    void onLoadFailed();
}
