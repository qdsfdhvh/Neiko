package seiko.neiko.dao.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import seiko.neiko.app.App;
import seiko.neiko.models.Book;
import seiko.neiko.models.SourceModel;
import seiko.neiko.dao.engine.DdSource;
import seiko.neiko.utils.EncryptUtil;

/**
 * Created by Seiko on 2016/8/29. YiKu
 */
public class SiteDbApi {

    private static class SiteDbContext extends DbContext {
        SiteDbContext(Context context) {super(context, "sitedb", 10);}

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL("create table sites (" +
                    "id integer primary key autoincrement," +
                    "type integer DEFAULT 0 NOT NULL," +
                    "key varchar(40)," + //md5
                    "url varchar(100)," +
                    "expr varchar(200)," +
                    "ver integer," +
                    "title varchar(40)," +
                    "author varchar(40)," +
                    "intro varchar(40)," +
                    "logo varchar(40)," +
                    "sited text," +
                    "cookies varchar(1000)," +
                    "subTime long," + //订阅时间（为0时；未订阅）
                    "logTime long);");

            db.execSQL("CREATE INDEX IX_site_key ON sites (key);");


            db.execSQL("create table historys (" +
                    "id integer primary key autoincrement," +
                    "key varchar(40)," +
                    "title varchar(40)," +
                    "url varchar(100)," +
                    "logTime long);");

            db.execSQL("CREATE INDEX IX_history_key ON historys (key);");
        }


        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
    }

    private static SiteDbContext db;

    //history
    //
    static {
        if(db == null){
            db = new SiteDbContext(App.getContext());
        }
    }
//    public static boolean HasSource(DdSource sd) {
//        return (db.existsSQL("SELECT * FROM sites WHERE key=?", sd.url_md5));
//    }

    //my subscibe
    //
//    public static void addSource(DdSource sd, String sited, boolean isSubscribe) {
//        long subTime = isSubscribe ? new Date().getTime() : 0;
//        if (!db.existsSQL("SELECT * FROM sites WHERE key=?", sd.url_md5)) {
//
//            db.updateSQL("INSERT INTO sites (author,type,key,url,expr,ver,title,intro,logo,sited,logTime,subTime,cookies) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?);",
//                    sd.author,sd.main.dtype(),sd.url_md5, sd.url, sd.expr, sd.ver, sd.title, sd.intro, sd.logo, sited, new Date().getTime(), subTime, sd.cookies());
//        }
//        else {
//            db.updateSQL("UPDATE sites SET author=?,type=?,ver=?,title=?,intro=?,logo=?,sited=?,expr=? WHERE key=?;",
//                    sd.author, sd.main.dtype(), sd.ver, sd.title, sd.intro, sd.logo, sited, sd.expr, sd.url_md5);
//        }
//    }

//    public static void delSource(String key) {
//        db.updateSQL("DELETE FROM sites WHERE key=?", key);
//    }

//    public static void setSourceExpr(DdSource sd){
//        db.updateSQL("UPDATE  sites SET expr=? WHERE key=?;",
//                sd.expr, sd.url_md5);
//    }

    public static void setSourceCookies(DdSource sd) {
        db.updateSQL("UPDATE  sites SET cookies=? WHERE key=?;",
                sd.cookies(), sd.url_md5);
    }

    @Nullable
    public static String getSourceCookies(DdSource sd) {
        SourceModel temp = getSourceByKey(sd.url_md5);
        if (temp == null)
            return null;
        else
            return temp.cookies;
    }

//    public static String getSited(String name) {
//        String sited = "";
//
//        DataReader dr = db.selectSQL("SELECT sited FROM sites WHERE title=?",name);
//        if (dr.read()) {
//            sited = dr.getString("sited");
//        }
//        dr.close();//内部同时关闭游标和数据库
//
//        return sited;
//    }


//    public static List<SourceModel> getSources(boolean isOnlyAddin) {
//        Log.v("SitesDApi","getSources");
//        List<SourceModel> list = new ArrayList<>();
//
//        DataReader dr = db.selectSQL("SELECT * FROM sites " + (isOnlyAddin ? "WHERE type<>99;" : ";"));
//
//        while (dr.read()) {
//            SourceModel m = new SourceModel();
//            m.id = dr.getInt("id");
//            m.type = dr.getInt("type");
//            m.key = dr.getString("key");
//            m.url = dr.getString("url");
//            m.expr = dr.getString("expr");
//            m.ver = dr.getInt("ver");
//            m.author = dr.getString("author");
//            m.title = dr.getString("title");
//            m.intro = dr.getString("intro");
//            m.logo = dr.getString("logo");
//            m.sited = dr.getString("sited");
//            m.cookies = dr.getString("cookies");
//            m.subTime = dr.getLong("subTime");
//            list.add(m);
//        }
//        dr.close();//内部同时关闭游标和数据库
//
//        for (SourceModel s : list) {
//            if (TextUtils.isEmpty(s.url)) {
//                s.url = s.key;
//                s.key = EncryptUtil.md5(s.url);
//                db.updateSQL("UPDATE sites SET key=?,url=? WHERE id=?", s.key, s.url, s.id);
//            }
//        }
//
//        return list;
//    }

//    public static SourceModel getSourceByUrl(String url) {
//        List<SourceModel> list = getSources(true);
//
//        for(SourceModel m :list){
//            if(m.isMe(url))
//                return m;
//        }
//        return null;
//    }

    //=======================================================
    /** 获得插件参数：完整版 */
    private static SourceModel getSourceByKey(String key) {
        DataReader dr = db.selectSQL("SELECT * FROM sites WHERE key=?;",key);
        SourceModel m = null;

        if (dr.read()) {
            m = new SourceModel();

            m.id      = dr.getInt("id");
            m.type    = dr.getInt("type");
            m.key     = dr.getString("key");
            m.url     = dr.getString("url");
            m.ver     = dr.getInt("ver");
            m.author  = dr.getString("author");
            m.title   = dr.getString("title");
            m.intro   = dr.getString("intro");
            m.logo    = dr.getString("logo");
            m.sited   = dr.getString("sited");
            m.cookies = dr.getString("cookies");
            m.subTime = dr.getLong("subTime");
        }
        dr.close();//内部同时关闭游标和数据库

        return m;
    }

    public static List<SourceModel> getSources() {
        List<SourceModel> list = new ArrayList<>();
        DataReader dr = db.selectSQL("SELECT * FROM sites;");
        while (dr.read()) {
            SourceModel m = new SourceModel();
            m.id      = dr.getInt("id");
            m.type    = dr.getInt("type");
            m.key     = dr.getString("key");
            m.url     = dr.getString("url");
            m.expr    = dr.getString("expr");
            m.ver     = dr.getInt("ver");
            m.title   = dr.getString("title");
            m.author  = dr.getString("author");
            m.intro   = dr.getString("intro");
            m.logo    = dr.getString("logo");
            m.sited   = dr.getString("sited");
            m.cookies = dr.getString("cookies");
            m.subTime = dr.getLong("subTime");
            list.add(m);
        }
        dr.close();
        return list;
    }

    //=======================================================
    /** 获得插件参数：简单版 */
//    public static SourceModel getSamples(String url) {
//        SourceModel m = new SourceModel();
//
//        DataReader dr = db.selectSQL("SELECT * FROM sites WHERE url=?", url);
//        while (dr.read()) {
//            m.key     = dr.getString("key");
//            m.url     = dr.getString("url");
//            m.title   = dr.getString("title");
//            m.logo    = dr.getString("logo");
//        }
//        dr.close();
//        return m;
//    }
//
//    public static List<SourceModel> getSamples() {
//        List<SourceModel> list = new ArrayList<>();
//        DataReader dr = db.selectSQL("SELECT * FROM sites;");
//        while (dr.read()) {
//            SourceModel m = new SourceModel();
//            m.key     = dr.getString("key");
//            m.url     = dr.getString("url");
//            m.title   = dr.getString("title");
//            m.logo    = dr.getString("logo");
//            list.add(m);
//        }
//        dr.close();
//        return list;
//    }

    //=======================================================
    /** 获得插件参数：搜索版 */
    public static List<SourceModel> getSamples2() {
        List<SourceModel> list = new ArrayList<>();
        DataReader dr = db.selectSQL("SELECT * FROM sites;");
        while (dr.read()) {
            SourceModel m = new SourceModel();
            m.key     = dr.getString("key");
            m.url     = dr.getString("url");
            m.title   = dr.getString("title");
            m.logo    = dr.getString("logo");
            m.sited   = dr.getString("sited");
            list.add(m);
        }
        dr.close();
        return list;
    }

    //=======================================================
    public static void addSource(SourceModel m) {

        if (!db.existsSQL("SELECT * FROM sites WHERE key=?", m.key)) {
            Log.d("addSource","添加"+m.key);
            db.updateSQL("INSERT INTO sites (author,type,key,url,expr,ver,title,intro,logo,sited,logTime,subTime,cookies) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?);",
                    m.author,m.type,m.key,m.url,m.expr,m.ver,m.title,m.intro,m.logo,m.sited, new Date().getTime(), m.subTime,m.cookies);
        }
        else {
            Log.d("addSource","更新"+m.key);
            db.updateSQL("UPDATE sites SET author=?,type=?,ver=?,title=?,intro=?,logo=?,sited=?,expr=? WHERE key=?;",
                    m.author, m.type, m.ver, m.title, m.intro, m.logo, m.sited, m.expr, m.key);
        }
    }

}
