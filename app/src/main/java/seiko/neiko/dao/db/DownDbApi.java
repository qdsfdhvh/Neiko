package seiko.neiko.dao.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import seiko.neiko.app.App;
import seiko.neiko.models.DownSectionBean;
import seiko.neiko.models.DownBookBean;
import seiko.neiko.viewModels.BookViewModel;

/**
 * Created by Seiko on 2016/11/20. YiKu
 */

public class DownDbApi {

    private static class DownDbContext extends DbContext {
        DownDbContext(Context context) {super(context, "downdb", 2);}

        @Override
        public void onCreate(SQLiteDatabase db) {

            //章节
            db.execSQL("create table down_section (" +
                    "id integer primary key autoincrement," +
                    "type integer DEFAULT 0 NOT NULL," +
                    "bkey varchar(40)," +   //识别
                    "source varchar(40)," +
                    "bookName varchar(50)," +
                    "bookUrl varchar(100)," +
                    "name varchar(50)," +
                    "url varchar(100)," +
                    "success integer DEFAULT 0," +   //成功
                    "failed integer DEFAULT 0," +    //失败
                    "total integer DEFAULT 0," +     //共计
                    "state integer DEFAULT 0," +     //状态
                    "path varchar(100)," +
                    "index2 integer DEFAULT 0," +
                    "logTime long);");

            //漫画
            db.execSQL("create table down_queue (" +
                    "id integer primary key autoincrement," +
                    "type integer DEFAULT 0 NOT NULL," +
                    "bkey varchar(40)," +   //识别
                    "source varchar(20)," +
                    "logo varchar(100)," +
                    "bookName varchar(50)," +
                    "bookUrl varchar(100)," +
                    "progress integer DEFAULT 0," +  //位置
                    "total integer DEFAULT 0," +     //共计
                    "state integer DEFAULT 0," +     //状态
                    "path varchar(100)," +
                    "logTime long);");

            //设置
            db.execSQL("create table down_setting (" +
                    "id integer primary key autoincrement," +
                    "setName varchar(20)," +
                    "setData integer DEFAULT 0);");

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
    }

    private static DownDbContext db;

    static {
        if (db == null) {
            db = new DownDbContext(App.getContext());
        }
    }

    //=====================================
    /** 漫画列表 */
    public static void addDownedQueue(BookViewModel sd, String path) {

        if (db.existsSQL("SELECT * FROM down_queue WHERE bkey=?", sd.bookKey)) {
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.US);

        db.updateSQL("INSERT INTO down_queue(type, bkey, source, logo, bookName, bookUrl, path, logTime) VALUES(?,?,?,?,?,?,?,?)",
                sd.dtype(), sd.bookKey, sd.source, sd.logo,
                sd.name, sd.bookUrl, path, sdf.format(new Date())
        );
    }

    public static List<DownBookBean> getDownedQueue() {
        List<DownBookBean> list = new ArrayList<>();
        DataReader dr = db.selectSQL("SELECT * FROM down_queue");
        while (dr.read()) {
            DownBookBean temp = new DownBookBean();
            temp.setDtype(dr.getInt("type"));
            temp.setTitle(dr.getString("bookName"));
            temp.setImage(dr.getString("logo"));
            temp.setUrl(dr.getString("bookUrl"));
            temp.setBkey(dr.getString("bkey"));
            temp.setSource(dr.getString("source"));
            temp.setProgress(dr.getInt("progress"));
            temp.setTotal(dr.getInt("total"));
            temp.setState(dr.getInt("state"));
            list.add(temp);
        }
        dr.close();//内部同时关闭游标和数据库
        return list;
    }

    //删除全部漫画
    public static void delDownedQueueAll() {
        db.updateSQL("DELETE FROM down_queue");
    }

    //删除指定漫画
    public static void delDownedQueue(String bkey) {
        db.updateSQL("DELETE FROM down_queue WHERE bkey=?", bkey);
    }

    //修改状态
    public static void setDownedQueueState(String bkey, int state) {
        db.updateSQL("UPDATE down_queue SET state=? WHERE bkey=?", state, bkey);
    }

    //获得状态
    public static int getDownedQueueState(String bkey) {
        int state = 0;
        DataReader dr = db.selectSQL("SELECT state FROM down_queue WHERE bkey=?", bkey);
        if (dr.read()) {
            state = dr.getInt("state");
        }
        dr.close();
        return state;
    }

    //获得总数
    public static void setDownedQueueTotal(String bkey, int total) {
        db.updateSQL("UPDATE down_queue SET total=? WHERE bkey=?", total, bkey);
    }

    //修改总数
    public static int getDownedQueueTotal(String bkey) {
        int total = 0;
        DataReader dr = db.selectSQL("SELECT total FROM down_queue WHERE bkey=?", bkey);
        if (dr.read()) {
            total = dr.getInt("total");
        }
        dr.close();
        return total;
    }

    //修改进度
    public static void setDownedQueueProgress(String bkey, int progress) {
        db.updateSQL("UPDATE down_queue SET progress=? WHERE bkey=?", progress, bkey);
    }

    //获得进度
    public static int getDownedQueueProgress(String bkey) {
        int progress = 0;
        DataReader dr = db.selectSQL("SELECT progress FROM down_queue WHERE bkey=?", bkey);
        if (dr.read()) {
            progress = dr.getInt("progress");
        }
        dr.close();
        return progress;
    }


//    public static String getDownedQueuePath(String bkey) {
//        String path = null;
//        DataReader dr = db.selectSQL("SELECT path FROM down_queue WHERE bkey=?", bkey);
//        if (dr.read()) {
//            path = dr.getString("path");
//        }
//        dr.close();
//        return path;
//    }


    //=====================================
    /** 章节列表 */
    public static void addDownedSection(DownSectionBean down) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.US);

        db.updateSQL("INSERT INTO down_section(type, bkey, source, bookName, bookUrl, name, url, state, path, index2, logTime) VALUES(?,?,?,?,?,?,?,?,?,?,?)",
                down.getType(), down.getBkey(), down.getSource(), down.getBookName(), down.getBookUrl(), down.getSection(), down.getSectionurl(), down.getStatus(), down.getPath(), down.getIndex(), sdf.format(new Date()) );
    }

    public static List<DownSectionBean> getDownedSection(String bkey) {
        List<DownSectionBean> list = new ArrayList<>();
        DataReader dr = db.selectSQL("SELECT * FROM down_section WHERE bkey=?", bkey);
        while (dr.read()) {
            DownSectionBean down = new DownSectionBean();
            down.setType(dr.getInt("type"));
            down.setBkey(dr.getString("bkey"));
            down.setSource(dr.getString("source"));
            down.setBookName(dr.getString("bookName"));
            down.setBookUrl(dr.getString("bookUrl"));
            down.setSection(dr.getString("name"));
            down.setSectionurl(dr.getString("url"));
            down.setSuccess(dr.getInt("success"));
            down.setFailed(dr.getInt("failed"));
            down.setTotal(dr.getInt("total"));
            down.setStatus(dr.getInt("state"));
            down.setPath(dr.getString("path"));
            down.setIndex(dr.getInt("index2"));
            list.add(down);
        }
        dr.close();//内部同时关闭游标和数据库
        return list;
    }



    //删除全部
    public static void delDownedSectionAll() {
        db.updateSQL("DELETE FROM down_section");
    }

    //删除漫画记录时，同时删除章节记录
    public static void delDownedSections(String bkey) {
        db.updateSQL("DELETE FROM down_section WHERE bkey=?", bkey);
    }

    //删除章节记录
    public static void delDownedSection(String url) {
        db.updateSQL("DELETE FROM down_section WHERE url=?", url);
    }

    //更新进度
    public static void setDownedSectionIndex(String url, int success, int failed, int total) {
        db.updateSQL("UPDATE down_section SET success=?, failed=?, total=? WHERE url=?", success, failed, total, url);
    }

    //更新总页数
    public static void setDownedSectionTotal(String url, int total) {
        db.updateSQL("UPDATE down_section SET total=? WHERE url=?", total, url);
    }

    //修改状态
    public static void setDownedSectionState(String url, int state) {
        db.updateSQL("UPDATE down_section SET state=? WHERE url=?", state, url);
    }

    //读取章节状态
    public static int readDownedSectionState(String url) {
        int status=0;

        DataReader dr = db.selectSQL("SELECT state FROM down_section WHERE url=?", url);
        if (dr.read()) {
            status = dr.getInt("state");
        }
        dr.close();

        return status;
    }

    //读取章节状态
    public static String getDownedSectionPath(String url) {
        String path = null;

        DataReader dr = db.selectSQL("SELECT path FROM down_section WHERE url=?", url);
        if (dr.read()) {
            path = dr.getString("path");
        }
        dr.close();

        return path;
    }

//    //=====================================
//    /** 图片列表 */
//    public static void addDowned(String sUrl, String url, String SavedName, String SavedPath, int isDowned) {
//
//        db.updateSQL("INSERT INTO downed(sectionUrl, url, SavedName, SavedPath, isDowned) VALUES(?,?,?,?,?)",
//               sUrl, url, SavedName, SavedPath, isDowned);
//    }
//
//    public static String getDownedPath(String url) {
//        String path = null;
//        DataReader dr = db.selectSQL("SELECT SavedName, SavedPath FROM downed WHERE url=?", url);
//        if (dr.read()) {
//            String SavedName = dr.getString("SavedName");
//            String SavedPath = dr.getString("SavedPath");
//            path = SavedPath + "/" + SavedName;
//        }
//        dr.close();
//        return path;
//    }

    //=====================================
    /** 设置列表 */
    public static int getMaxDownNumber() {
        if (!db.existsSQL("SELECT * FROM down_setting")) {
            db.updateSQL("INSERT INTO down_setting(setName, setData) VALUES(?,?)", "MaxDownNumber", 1);
            return 1;
        }

        int maxDownNumber = 1;
        DataReader dr = db.selectSQL("SELECT setData FROM down_setting WHERE setName=?", "MaxDownNumber");
        if (dr.read()) {
            maxDownNumber = dr.getInt("setData");
        }
        dr.close();

        return maxDownNumber;
    }

    public static void setMaxDownNumber(int i) {
        db.updateSQL("UPDATE down_setting SET setData=? WHERE setName=?", i, "MaxDownNumber");
    }
}
