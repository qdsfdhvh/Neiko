package seiko.neiko.dao;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.Locale;

import seiko.neiko.app.App;
import seiko.neiko.models.DownSectionBean;
import seiko.neiko.models.ImgUrlBean;
import seiko.neiko.utils.EncryptUtil;
import seiko.neiko.utils.HintUtil;

/**
 * Created by Seiko on 2017/1/22.
 * 管理各种路径
 */

public class mPath {
    /* 基本路径 */
    private static final String basePath = App.getCurrent().getBasePath();
    /* 备份 */
    public static final String backPath = basePath + "Backup/";
    /* 缓存 */
    public static final String cachePath = basePath + "cache/";
    /* 下载 */
    public static final String downPath = basePath + "download/";
    /* 插件 */
    public static final String sitedPath = basePath + "siteD/";

    @Nullable
    public static String getSitedPath(String name) {
        if (TextUtils.isEmpty(name)) {
            return null;
        }
        if (!name.endsWith(".sited")) {
            name = name + ".sited";
        }
        return sitedPath + name;
    }

    /* 缓存路径：每个首页文件 */
    public static String getHomeCachePath(String url) {
        return cachePath + "home/" + EncryptUtil.md5(url);
    }

    /* 缓存路径：每个目录文件 */
    public static String getBookCachePath(String url) {
        return cachePath + "book/" + EncryptUtil.md5(url);
    }

    /* 下载路径：每个漫画目录 */
    public static String getBookDownPath(String source, String bookName) {
        return downPath + source + "/" + Fo(bookName) + "/";
    }

    /* 下载路径：每个漫画章节目录 */
    public static String getSectionDownPath(DownSectionBean bean) {
        return getSectionDownPath(bean.getSource(), bean.getBookName(), bean.getSection());
    }

    public static String getSectionDownPath(String source, String bookName, String section) {
        return getBookDownPath(source, bookName) + Fo(section) + "/";
    }

    /* 下载路径：每个漫画章节信息文件 */
    public static String getSectionBeanPath(DownSectionBean bean) {
        return getSectionDownPath(bean) + "/SectionBean";
    }

    /* 下载路径：章节的每个图片文件 */
    public static String getSectionImgPath(String path, ImgUrlBean img) {
        return path + getFilename(img.getIndex());
    }

    //图片保存名称
    private static String getFilename(int i) {
        return String.format(Locale.US, "%03d.jpg", i+1);
    }

    //去除多余的斜杠
    private static String Fo(String name) {return name.replaceAll("/", "_");}
}
