package seiko.neiko.widget.refresh;

/**
 * Created by yuety on 15/9/1. Y
 */
public class DefaultStrings implements IStrings {

    public final static DefaultStrings instance = new DefaultStrings();

    @Override
    public String pull_to_refresh() {
        return "下拉刷新";
    }

    @Override
    public String release_to_refresh() {
        return "释放立即刷新";
    }

    @Override
    public String refreshing() {
        return "正在刷新...";
    }

    @Override
    public String refresh_succeed() {
        return "刷新成功";
    }

    @Override
    public String refresh_fail() {
        return "刷新失败";
    }

    @Override
    public String pullup_to_load() {
        return "上拉加载更多";
    }

    @Override
    public String release_to_load() {
        return "释放立即加载";
    }

    @Override
    public String loading() {
        return "正在加载...";
    }

    @Override
    public String load_succeed() {
        return "加载成功";
    }

    @Override
    public String load_fail() {
        return "加载失败";
    }
}
