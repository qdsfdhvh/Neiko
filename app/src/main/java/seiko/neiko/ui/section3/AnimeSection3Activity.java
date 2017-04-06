package seiko.neiko.ui.section3;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;

import com.dou361.ijkplayer.bean.VideoijkBean;

import org.noear.sited.SdNode;
import org.noear.sited.SdSourceCallback;

import java.util.ArrayList;
import java.util.List;

import seiko.neiko.R;
import seiko.neiko.dao.db.DbApi;
import seiko.neiko.dao.engine.DdNode;
import seiko.neiko.app.BaseActivity;
import seiko.neiko.dao.mIntent;
import seiko.neiko.viewModels.VideoViewModel;

/**
 * Created by Seiko on 2016/9/7. YiKu
 */

public class AnimeSection3Activity extends BaseActivity {

    public static Section3Model m;

    @Override
    protected void onResume() {
        super.onResume();
        setImmersiveFullscreen(); //全屏
    }

    @Override
    public int getLayoutId() {return R.layout.activity_section3;}

    @Override
    public void initViews(Bundle bundle) {
        final VideoViewModel viewModel = new VideoViewModel();
        SdNode config;
        if (m.dtype==7)
            config = source.book(m.url);
        else
            config = source.section(m.url);

        source.getNodeViewModel(viewModel, false, m.url, config, new SdSourceCallback() {
            @Override
            public void run(Integer code) {
                if (code == 1) {
                    if (viewModel.list == null || viewModel.list.size() == 0) {
                        isNull();
                        return;
                    }

                    //多个播放链接
                    if (viewModel.list.size() > 1) {
                        openVideo(viewModel.list);
                        return;
                    }

                    VideoijkBean model = viewModel.list.get(0);

                    //非多链接时，判断是音乐还是视频
                    if (TextUtils.isEmpty(model.getMime())) {
                        isVideo(model);
                        return;
                    }

                    switch (model.getMime()) {
                        case "audio/x-mpeg":
                            if (!TextUtils.isEmpty(viewModel.logo)) {
                                m.logo = viewModel.logo;
                            }
                            isMusic(model);
                            break;
                        case "video/mp4":
                        default:
                            isVideo(model);
                            break;
                    }

                }
            }
        });
    }

    //===================================================
    /** 空值处理 */
    private void isNull() {
        new AlertDialog.Builder(this)
                .setMessage("解析错误")
                .setNegativeButton("浏览器打开", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mIntent.Intent_Web(AnimeSection3Activity.this, m.url);
                    }
                })
                .setPositiveButton("关闭", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        onBackPressed();
                    }
                })  //通知最右按钮
                .create()
                .show();
    }

    //===================================================
    /** 音乐处理 */
    private void isMusic(final VideoijkBean model) {
        Section3MusicFragment section3MusicFragment = new Section3MusicFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", model.getUrl());
        bundle.putString("title", m.url);
        bundle.putString("logo", m.logo);
        section3MusicFragment.setArguments(bundle);
        shiftView(section3MusicFragment);
    }

    //===================================================
    /** 视频处理 */
    private void isVideo(final VideoijkBean model) {
        switch (DbApi.getVideoMode()) {
            default:
            case 0:
                new AlertDialog.Builder(this)
                        .setMessage("选择播放器")
                        .setNegativeButton("外部", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                outPlayer(model);
                            }
                        })
                        .setPositiveButton("本地", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                inPlayer(model);
                            }
                        })
                        .create()
                        .show();
                break;
            case 1:
                inPlayer(model);
                break;
            case 2:
                outPlayer(model);
                break;
        }
    }

    private void inPlayer(final VideoijkBean model) {
        DdNode objCfg = source.objectSlf(model.getUrl());
        if(!objCfg.isEmpty()) {
            model.setHeaders(objCfg.getFullHeader(model.getUrl()));
        }
        openVideo(model);
    }

    private void outPlayer(final VideoijkBean model) {
        mIntent.Intent_MX(this, model.getUrl());
        finish();
    }


    //==========================
    private void openVideo(VideoijkBean bean) {
        List<VideoijkBean> list = new ArrayList<>(0);
        list.add(bean);
        openVideo(list);
    }

    private void openVideo(List<VideoijkBean> list) {
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
