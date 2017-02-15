package seiko.neiko.widget.refresh;

/**
 * 刷新加载回调接口
 *
 * @author chenjing
 *
 */
public interface OnRefreshListener
{
    /**
     * 刷新操作
     */
    void onRefresh(IPullToRefreshView pullToRefreshLayout);

    /**
     * 加载操作
     */
    void onLoadMore(IPullToRefreshView pullToRefreshLayout);
}