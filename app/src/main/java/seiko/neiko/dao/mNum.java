package seiko.neiko.dao;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.util.List;

import seiko.neiko.app.App;
import seiko.neiko.models.Book;

/**
 * Created by Seiko on 2016/12/23. Y
 */

public class mNum {

    private static final int COUNT_TWO = getCount(2);
    private static final int COUNT_THREE = getCount(3);
    private static final int COUNT_FOUR =  getCount(4);

    /* main */
    public static final int HOME_NUMBER = COUNT_FOUR;
    public static final int LIKE_NUMBER = COUNT_THREE;
    public static final int HIST_NUMBER = COUNT_THREE;

    /* home */
    public static final int HOTS_NUMBER = COUNT_TWO;
    public static final int TAGS_NUMBER = COUNT_THREE;



    private static int getCount(int count) {
        WindowManager wm = (WindowManager) App.getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        float scale = App.getContext().getResources().getDisplayMetrics().density;
        int widthDps = (int) (dm.widthPixels / scale + 0.5f);

        int num1;
        int num2;

        switch (count) {
            case 4:  num1 = 120; num2 = 60; break;
            case 3:  num1 = 120; num2 = 60; break;
            default: num1 = 180; num2 = 60; break;
        }

        while (widthDps / count > num1) count++;
        while (widthDps / count < num2) count--;

        return count;
    }

    /** 判断目录列数 :漫画 */
    public static int SecNum(List<Book> list, int dtype) {
        if (list.size() > 3 && dtype == 1) {
            int j = 0;
            int k = 0;
            for (int i=0;i<3;i++) {
                int length = list.get(i).getSection().length();
                if (length < 6) {
                    k++;
                    if (length < 4) {
                        j++;
                    }
                }
            }

            if (j == 3) {
                return 4;
            } else if (k == 3) {
                return 3;
            }
        }
        return 1;
    }
}
