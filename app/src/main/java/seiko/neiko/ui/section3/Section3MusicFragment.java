package seiko.neiko.ui.section3;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.IOException;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;
import seiko.neiko.R;
import seiko.neiko.app.FragmentBase;
import seiko.neiko.utils.ImgBlurUtil;
import seiko.neiko.widget.AlbumCoverView;

/**
 * Created by Seiko on 2016/12/22. Y
 */

public class Section3MusicFragment extends FragmentBase implements
        SeekBar.OnSeekBarChangeListener, AudioManager.OnAudioFocusChangeListener {

    @BindView(R.id.app_music_box)
    FrameLayout music_box;
    @BindView(R.id.album_cover_view)
    AlbumCoverView mAlbumCoverView;
    @BindView(R.id.iv_play_page_bg)
    ImageView ivPlayBg;

    //按钮
    @BindView(R.id.iv_play)
    ImageView ivPlay;
    @BindView(R.id.iv_next)
    ImageView ivNext;
    @BindView(R.id.iv_prev)
    ImageView ivPrev;
    @BindView(R.id.sb_progress)
    SeekBar sbProgress;
    @BindView(R.id.tv_current_time)
    TextView tvCurrentTime;             //当前时间
    @BindView(R.id.tv_total_time)
    TextView tvTotalTime;               //总共时间
    @BindView(R.id.tv_title)
    TextView tvTitle;

    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;

    public Section3MusicFragment() {mediaPlayer = new MediaPlayer();}

    @Override
    public int getLayoutId() {return R.layout.fragment_section3_music;}

    @Override
    public void initView() {
        Bundle bundle = getArguments();
        String url = bundle.getString("url");
        String title = bundle.getString("title");
        String logo = bundle.getString("logo");

        tvTitle.setText(title);
        AudioManager mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);

        try {
            mediaPlayer.reset();    //重启
            mediaPlayer.setDataSource(url);
            //mediaPlayer.prepare();  //准备
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener((MediaPlayer mp) -> {
                mp.start();
                isPlaying = true;
                ivPlay.setSelected(true);
                mAlbumCoverView.start();
                tvTotalTime.setText(formatTime(mediaPlayer.getDuration()));
                sbProgress.setMax(mediaPlayer.getDuration());   //设置进度条
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        changLogo(logo);
        TimeSeekBar();
        sbProgress.setOnSeekBarChangeListener(this);
    }

    //============================================
    /* 按钮 */
    @OnClick({R.id.iv_play,R.id.iv_next,R.id.iv_prev})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_play:
                if (isPlaying) {
                    play_pause();
                } else {
                    play_start();
                }
                break;
            case R.id.iv_next:
                break;
            case R.id.iv_prev:
                break;
        }
    }

//    @OnClick(R.id.iv_back)
//    void musicBack() {
//        play_stop();
//    }

    @Override
    public void onStop() {
        super.onStop();
        play_stop();
    }

    public void play_start() {
        mediaPlayer.start(); //播放音乐
        isPlaying = true;
        ivPlay.setSelected(true);
        mAlbumCoverView.start();
    }


    public void play_pause() {
        mediaPlayer.pause(); //暂停音乐
        isPlaying = false;
        ivPlay.setSelected(false);
        mAlbumCoverView.pause();
    }

    public void play_stop() {
        mediaPlayer.stop();  //关闭音乐
        mediaPlayer.release();
        mediaPlayer = null;
        getActivity().finish();
    }



    //============================================
    /* 滚动条 */
    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {}

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (seekBar == sbProgress) {
            if (mediaPlayer != null) {
                setProgress(seekBar.getProgress());
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        }
    }

    /* 捕获音乐进度 */
    private void TimeSeekBar() {

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    setProgress(mediaPlayer.getCurrentPosition());
                }
            }
        };
        timer.schedule(timerTask,0,500);
    }

    private void setProgress(int i) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                tvCurrentTime.setText(formatTime(i));
                sbProgress.setProgress(i);
            });
        }
    }

    private String formatTime(long milli) {
        String pattern = "mm:ss";
        int m = (int) (milli / (60 * 1000));
        int s = (int) ((milli / 1000) % 60);
        String mm = String.format(Locale.US, "%02d", m);
        String ss = String.format(Locale.US, "%02d", s);
        return pattern.replace("mm", mm).replace("ss", ss);
    }

    //======================================================
    /* 修改logo */
    private void changLogo(String logo) {
        Glide.with(this)
                .load(logo.replace("HTTP:","http:"))
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                        if (bitmap != null) {
                            mAlbumCoverView.setCoverBitmap(bitmap);
                            Bitmap bg = ImgBlurUtil.blur(bitmap, ImgBlurUtil.BLUR_RADIUS);
                            ivPlayBg.setImageBitmap(bg);
                        }
                    }
                });
    }



    //======================================================
    /* 如果正在播放音乐，则改成新歌 */
    @Override
    public void onAudioFocusChange(int i) {
        switch (i) {
            case AudioManager.AUDIOFOCUS_LOSS:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                if (isPlaying && mediaPlayer != null) {
                    mediaPlayer.pause();
                    break;
                }
        }
    }
}
