package seiko.neiko.widget.helper;

/**
 * Created by Seiko on 2017/2/1. Y
 */

public interface ItemTouchHelperAdapter {

    boolean onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);

}
