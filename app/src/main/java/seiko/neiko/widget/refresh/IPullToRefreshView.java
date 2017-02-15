package seiko.neiko.widget.refresh;

/**
 * Created by yuety on 15/9/1. Y
 */
public interface IPullToRefreshView {
    void refreshFinish(int refreshResult);
    void loadmoreFinish(int refreshResult);
}
