package seiko.neiko.widget.refresh;

/**
 * Created by yuety on 15/9/1. Y
 */
public class HorizontalPageloadStrings implements IStrings {
    public final static HorizontalPageloadStrings instance = new HorizontalPageloadStrings();

    @Override
    public String pull_to_refresh() {
        return "右拉加载上一话";
    }

    @Override
    public String release_to_refresh() {
        return "释放立即加载";
    }

    @Override
    public String refreshing() {
        return "正在加载...";
    }

    @Override
    public String refresh_succeed() {
        return "加载成功";
    }

    @Override
    public String refresh_fail() {
        return "加载失败";
    }

    @Override
    public String pullup_to_load() {
        return "左拉加载下一话";
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
