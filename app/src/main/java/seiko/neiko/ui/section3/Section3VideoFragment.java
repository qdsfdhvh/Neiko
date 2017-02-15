package seiko.neiko.ui.section3;

import android.content.res.Configuration;
import android.view.WindowManager;

import com.dou361.ijkplayer.bean.VideoijkBean;
import com.dou361.ijkplayer.widget.PlayerView;

import java.util.List;

import seiko.neiko.R;
import seiko.neiko.app.FragmentBase;
import seiko.neiko.utils.MediaUtil;

/**
 * Created by Seiko on 2016/12/21. Y
 */

public class Section3VideoFragment extends FragmentBase {

//    private final String TAG = "Section3VideoFragment";

    static List<VideoijkBean> list;
    static String title;

    private PlayerView player;

    @Override
    public int getLayoutId() {return R.layout.fragment_section3_video;}

    @Override
    public void initView() {
        getActivity().setContentView(R.layout.simple_player_view_player);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        player = new PlayerView(getActivity())
                .setTitle(title)
                .hideMenu(true)
                .setOnlyFullScreen(true)
                .forbidTouch(false)
                .hideCenterPlayer(true)
                .hideRotation(true)
                .setNetWorkTypeTie(true)
                .setPlaySource(list)
                .startPlay();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (player != null) {
            player.onPause();
        }
        /*demo的内容，恢复系统其它媒体的状态*/
        MediaUtil.muteAudioFocus(getActivity(), true);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (player != null) {
            player.onResume();
        }
        /*demo的内容，暂停系统其它媒体的状态*/
        MediaUtil.muteAudioFocus(getActivity(), false);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.onDestroy();
        }
        list = null;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (player != null) {
            player.onConfigurationChanged(newConfig);
        }
    }

}
