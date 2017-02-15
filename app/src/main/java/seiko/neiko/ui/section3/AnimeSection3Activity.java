package seiko.neiko.ui.section3;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import com.dou361.ijkplayer.bean.VideoijkBean;

import org.noear.sited.SdNode;

import java.util.ArrayList;
import java.util.List;

import seiko.neiko.R;
import seiko.neiko.models.MediaModel;
import seiko.neiko.app.ActivityBase;
import seiko.neiko.viewModels.VideoViewModel;

/**
 * Created by Seiko on 2016/9/7. YiKu
 */
public class AnimeSection3Activity extends ActivityBase {

    public static Section3Model m;

    private VideoViewModel viewModel;
    private SdNode config;
    private List<VideoijkBean> list;

    public AnimeSection3Activity() {
        viewModel = new VideoViewModel();
        list = new ArrayList<>();
        if (m.dtype==7)
            config = source.book(m.url);
        else
            config = source.section(m.url);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setImmersiveFullscreen(true); //全屏
    }

    @Override
    public int getLayoutId() {return R.layout.activity_section3;}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        source.getNodeViewModel(viewModel, false, m.url, config, (code) -> {
            if (code == 1) {
                if (viewModel.items == null || viewModel.items.size() == 0) {
                    isNull();
                    return;
                }

                //多个播放链接
                if (viewModel.items.size() > 1) {
                    int i = 1;
                    for (MediaModel model:viewModel.items) {
                        VideoijkBean m = new VideoijkBean();
                        m.setStream("第"+ i +"段");
                        m.setUrl(model.url);
                        list.add(m);
                        i++;
                    }
                    openVideo();
                    return;
                }

                MediaModel model = viewModel.items.get(0);

                //非多链接时，判断是音乐还是视频
                if (TextUtils.isEmpty(model.mime)) {
                    isVideo(model.url);
                    return;
                }

                switch (model.mime) {
                    case "audio/x-mpeg":
                        isMusic(model.url);
                        break;
                    case "video/mp4":
                    default:
                        isVideo(model.url);
                        break;
                }

            }
        });
    }

    //===================================================
    /** 空值处理 */
    private void isNull() {
        new AlertDialog.Builder(this)
                .setMessage("解析错误")
                .setNegativeButton("浏览器打开", (DialogInterface dif, int j) -> {
                    final Uri uri = Uri.parse(m.url);
                    final Intent it = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(it);
                    finish();
                })      //通知中间按钮
                .setPositiveButton("关闭", (DialogInterface dif, int j) -> {
                    dif.dismiss();
                    finish();
                })  //通知最右按钮
                .create()
                .show();
    }

    //===================================================
    /** 音乐处理 */
    private void isMusic(final String url) {
        if (!TextUtils.isEmpty(viewModel.logo)) {
            m.logo = viewModel.logo;
        }
        Section3MusicFragment section3MusicFragment = new Section3MusicFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        bundle.putString("title", m.url);
        bundle.putString("logo", m.logo);
        section3MusicFragment.setArguments(bundle);
        shiftView(section3MusicFragment);
    }

    //===================================================
    /** 视频处理 */
    private void isVideo(final String url) {
        new AlertDialog.Builder(this)
                .setMessage("选择播放器")
                .setNegativeButton("外部", (DialogInterface dif, int j) -> {
                    Uri uri = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW).setDataAndType(uri, "video/*");
                    startActivity(intent);
                    finish();
                })      //通知中间按钮
                .setPositiveButton("本地", (DialogInterface dif, int j) -> {
                    VideoijkBean m = new VideoijkBean();
                    m.setStream("第1段");
                    m.setUrl(url);
                    list.add(m);
                    openVideo();
                })  //通知最右按钮
                .create()
                .show();
    }

    private void openVideo() {
        Section3VideoFragment.title = m.name;
        Section3VideoFragment.list  = list;
        shiftView(new Section3VideoFragment());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        m = null;
    }

}
