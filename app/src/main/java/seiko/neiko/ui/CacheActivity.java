package seiko.neiko.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;

import java.io.File;
import java.text.DecimalFormat;

import butterknife.BindView;
import io.reactivex.functions.Consumer;
import seiko.neiko.R;
import seiko.neiko.utils.FileUtil;
import seiko.neiko.app.BaseSwipeLayout;

import static seiko.neiko.dao.mPath.cachePath;

/**
 * Created by Seiko on 2017/1/12. Y
 */

public class CacheActivity extends BaseSwipeLayout {

    @BindView(R.id.title)
    TextView tv;

    @Override
    public int getLayoutId() {return R.layout.activity_cache;}

    @Override
    public void initViews(Bundle bundle) {
        tv.setText("缓存");

        rxView(R.id.book_cache);
        rxView(R.id.home_cache);
        rxView(R.id.img_cache);
    }

    private void rxView(int id) {
        final String name;
        final String filename;
        int text_id;
        int size_id;
        switch (id) {
            case R.id.book_cache:
                name = "目录";
                filename = cachePath + "book";
                text_id = R.id.book_text;
                size_id = R.id.book_size;
                break;
            case R.id.home_cache:
                name = "首页";
                filename = cachePath + "home";
                text_id = R.id.home_text;
                size_id = R.id.home_size;
                break;
            case R.id.img_cache:
                name = "图片";
                filename = cachePath + "img";
                text_id = R.id.img_text;
                size_id = R.id.img_size;
                break;
            default:
                return;
        }
        final LinearLayout tv = (LinearLayout) findViewById(id);
        final TextView text = (TextView) findViewById(text_id);
        final TextView size = (TextView) findViewById(size_id);
        text.setText(String.valueOf("路径：" + filename));
        loadTextSize(size, filename);
        RxView.clicks(tv).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                getDialog(size, name, filename);
            }
        });
    }

    private void loadTextSize(TextView tv, String filename) {
        tv.setText(String.valueOf("大小：" + filesize(filename)));
    }

    private void getDialog(final TextView tv, String name, final String filename) {
        new AlertDialog.Builder(this)
                .setMessage("是否清空：" + name + " 缓存")
                .setNegativeButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FileUtil.deleteFile(filename);
                        loadTextSize(tv, filename);
                    }
                })      //通知中间按钮
                .setPositiveButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })        //通知最右按钮
                .create()
                .show();
    }


    @Nullable
    private String filesize(String filename) {
        try{
            File file = new File(filename);
            long size = FileUtil.getFileSize(file);
            return FormatFileSize(size);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /*** 转换文件大小单位(b/kb/mb/gb) ***/
    private String FormatFileSize(long fileS) {// 转换文件大小
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString;
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        }
        else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        }
        else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        }
        else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }

}
