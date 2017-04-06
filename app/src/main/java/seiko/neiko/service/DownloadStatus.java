package seiko.neiko.service;

/**
 * Created by Seiko on 2017/1/19. Y
 */

public class DownloadStatus {

    /** 默认 */
    public static final int STATE_NONE = 0;
    /** 开始下载 */
    public static final int STATE_START = 1;
    /** 等待中 */
    public static final int STATE_WAITING = 2;
//    /** 下载中 */
//    public static final int STATE_DOWNLOADING = 3;
    /** 暂停 */
    public static final int STATE_PAUSED = 4;
    /** 下载完毕 */
    public static final int STATE_DOWNLOADED = 5;
//    /** 下载失败 */
//    public static final int STATE_ERROR = 6;
//    /** 删除下载成功 */
//    public static final int STATE_DELETE = 7;
    /** 解析链接 */
    public static final int STATE_DOLOAD = 8;
    /** 进度*/
    public static final int STATE_PROCRESS = 9;

}
