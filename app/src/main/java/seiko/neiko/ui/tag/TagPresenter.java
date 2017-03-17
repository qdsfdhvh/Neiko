package seiko.neiko.ui.tag;

import seiko.neiko.app.BasePresenter;
import seiko.neiko.dao.engine.DdSource;

/**
 * Created by Seiko on 2017/2/26. Y
 */

class TagPresenter extends BasePresenter<TagView> {

    private TagViewModel viewModel;
    private String tagUrl;
    private DdSource source;

    TagPresenter(DdSource source, String tagUrl) {
        viewModel = new TagViewModel();
        this.source = source;
        this.tagUrl = tagUrl;
    }

    void loadData(int page) {
        viewModel.currentPage = page;
        loadData(true);
    }

    void loadData(boolean isRef) {
        if (!isRef) {
            viewModel.currentPage++;
        }

        viewModel.clear();
        source.getNodeViewModel(viewModel, false, viewModel.currentPage, tagUrl, source.tag(tagUrl), (code) -> {
            if (code == 1) {
                mBaseView.onLoadSuccess(viewModel.list);
            }
        });
    }

    int getPage() {return viewModel.currentPage;}

}
