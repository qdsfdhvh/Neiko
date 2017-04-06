package seiko.neiko.dao;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import seiko.neiko.app.App;
import seiko.neiko.dao.engine.DdSource;
import seiko.neiko.utils.FileUtil;


/**
 * Created by Seiko on 2016/8/29.
 * Sited引擎接口
 */
public class SourceApi {
    //===============================================
    /** 二改 */
    private static SourceApi instance;
    private final List<DdSource> _sourceList;

    public SourceApi() {_sourceList = new ArrayList<>();}

    public static SourceApi getDefault() {
        if (instance == null) {
            synchronized (SourceApi.class) {
                if (instance == null) {
                    instance = new SourceApi();
                }
            }
        }
        return instance;
    }

    //===============================================
    /** 尝试读取sited */
    public boolean readSited(final Context from, final String name) {
        for (DdSource sd : _sourceList) {
            if (sd.title.contains(name))
                return true;
        }

        String sited = getSiteD(name);
        if (TextUtils.isEmpty(sited)) {
            new AlertDialog.Builder(from)
                    .setMessage("需要安装插件:" + name)
                    .setNegativeButton("访问", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mIntent.Intent_Web(from, "http://sited.noear.org/?key=" + name);
                        }
                    })
                    .setPositiveButton("关闭", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create()
                    .show();
            return false;
        }

        loadSource(sited, null);
        return true;
    }

    public DdSource getByTitle(String name) {
        for (DdSource sd : _sourceList) {
            if (sd.title.contains(name))
                return sd;
        }

        String sited = getSiteD(name);
        if (TextUtils.isEmpty(sited)) {
            return null;
        }

        return loadSource(sited, null);
    }

    @Nullable
    public DdSource getByUrl(String url) {
        for (DdSource sd : _sourceList) {
            if (sd.isMatch(url))
                return sd;
        }

//        File file = new File(sitedPath);
//        for(File file1 : file.listFiles()) {
//            String path = mPath.getSitedPath(file1.getName());
//
//            if (!TextUtils.isEmpty(path)) {
//                String sited = FileUtil.readTextFromSDcard(path);
//                DdSource source = loadSource(sited, null);
//                if (source.isMatch(url)) {
//                    return source;
//                }
//            }
//        }
        return null;
    }

    private String getSiteD(String name) {
        String path = mPath.getSitedPath(name);

        String sited = "";
        if (!TextUtils.isEmpty(path)) {
            sited = FileUtil.readTextFromSDcard(path);
        }
        return sited;
    }

    //=========================================================
    /** 读取sited引擎 */
    public DdSource loadSource(String sited, String cookies) {
        DdSource sd = parseSource(sited);

        if (sd != null) {
            if (cookies != null) {
                sd.setCookies(cookies);
            }
            addSource(sd, cookies);
        }
        return sd;
    }

    /** 加载sited引擎 */
    @Nullable
    private DdSource parseSource(String sited){
        try {
            return new DdSource(App.getCurrent(), sited);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /** 尝试添加到list */
    private void addSource(DdSource sd, String cookies) {
        for (int i = 0, len = _sourceList.size(); i < len; i++) {
            DdSource s1 = _sourceList.get(i);
            if (s1.isMatch(sd.url)) {
                if (cookies == null) {
                    sd.setCookies(s1.cookies());
                }
                _sourceList.remove(i);
                break;
            }
        }
        _sourceList.add(sd);
    }
}
