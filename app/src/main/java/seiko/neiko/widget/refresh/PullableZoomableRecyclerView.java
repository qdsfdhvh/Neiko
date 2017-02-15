package seiko.neiko.widget.refresh;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;

/**
 * Created by yuety on 15/9/1. Y
 */
public class PullableZoomableRecyclerView extends ZoomableRecyclerView implements Pullable
{
    public PullableZoomableRecyclerView(Context context)
    {
        super(context);
    }

    public PullableZoomableRecyclerView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PullableZoomableRecyclerView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean canPullDown()
    {
        LinearLayoutManager lm = (LinearLayoutManager)getLayoutManager();
        if (lm.getItemCount() == 0)
        {
            // 没有item的时候也可以下拉刷新
            return true;
        } else if (lm.findFirstVisibleItemPosition() == 0
                && getChildAt(0).getTop() >= 0)
        {
            // 滑到ListView的顶部了
            return true;
        } else
            return false;
    }
    @Override
    public boolean canPullUp()

    {
        LinearLayoutManager lm = (LinearLayoutManager)getLayoutManager();

        if (lm.getItemCount() == 0)
        {
            // 没有item的时候也可以上拉加载
            return true;
        } else if (lm.findLastVisibleItemPosition() == (lm.getItemCount() - 1))
        {
            // 滑到底部了
            if (getChildAt(lm.findLastVisibleItemPosition() - lm.findFirstVisibleItemPosition()) != null
                    && getChildAt(
                    lm.findLastVisibleItemPosition()
                            - lm.findFirstVisibleItemPosition()).getBottom() <= getMeasuredHeight())
                return true;
        }



        return false;
    }
}
