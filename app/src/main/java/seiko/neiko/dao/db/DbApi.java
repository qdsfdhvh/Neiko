package seiko.neiko.dao.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import seiko.neiko.app.App;
import seiko.neiko.models.Book;
import seiko.neiko.models.ViewSetModel;
import seiko.neiko.viewModels.BookViewModel;

/**
 * Created by Seiko on 2016/8/31. YiKu
 */
public class DbApi {
    private static class DDCatDbContext extends DbContext {
        DDCatDbContext(Context context) {super(context, "comicsdb", 11);}

        @Override
        public void onCreate(SQLiteDatabase db) {
            //ver.2
            db.execSQL("create table books (" +
                    "id integer primary key autoincrement," +
                    "bkey varchar(40)," +
                    "name varchar(40)," +
                    "url varchar(100)," +

                    "section_count integer DEFAULT 0 ," +

                    "view_orientation integer DEFAULT 0," + //屏幕方向:0LANDSCAPE; 1PORTRAIT
                    "view_model integer DEFAULT 0," +       //翻页模式
                    "view_scale integer DEFAULT 0," +       //图片缩放
                    "view_direction integer DEFAULT 0," +   //阅读方向

                    "last_surl varchar(100)," +
                    "last_pidx integer DEFAULT -1," +
                    "last_page integer DEFAULT 0 ," +
                    "source varchar(40));");

            //收藏
            db.execSQL("create table likes (" +
                    "id integer primary key autoincrement," +
                    "type integer DEFAULT 0 NOT NULL," +
                    "bkey varchar(40)," + //md5
                    "url varchar(100)," +
                    "name varchar(40)," +
                    "logo varchar(40)," +
                    "author varchar(40)," +
                    "source varchar(40)," +
                    "number integer DEFAULT 0 NOT NULL," +
                    "logTime long);");

            //历史
            db.execSQL("create table historys (" +
                    "id integer primary key autoincrement," +
                    "type integer DEFAULT 0 NOT NULL," +
                    "bkey varchar(40)," + //md5
                    "url varchar(100)," +
                    "name varchar(40)," +
                    "logo varchar(40)," +
                    "author varchar(40)," +
                    "source varchar(40)," +
                    "logTime long);");

            //设置
            db.execSQL("create table setting (" +
                    "id integer primary key autoincrement," +
                    "setName varchar(20)," +
                    "setData integer DEFAULT 0);");

            db.execSQL("CREATE INDEX IX_books_bKey ON books (bkey);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
    }

    private static DDCatDbContext db;

    //history
    //
    static {
        if (db == null) {
            db = new DDCatDbContext(App.getContext());
        }
    }

    //donws
    //
//    public static void logBID(DdSource source, BookViewModel book) {
//        if (source.title != null) {
//            logBID(source.title,book.bookKey,book.bookUrl);
//        }
//    }

    public static void logBID(String source, String bookKey, String bookUrl) {
        if (!db.existsSQL("SELECT * FROM books WHERE bkey=?", bookKey)) {
            db.updateSQL("INSERT INTO books(bkey,url,source) VALUES(?,?,?);",
                    bookKey, bookUrl, source);
        }
    }

//    public static int getBID(String bookKey) {
//
//        int bid = 0;
//        DataReader dr = db.selectSQL("SELECT id FROM books WHERE bkey=?;", bookKey);
//        if (dr.read()) {
//            bid = dr.getInt("id");
//        }
//        dr.close();
//        return bid;
//    }


    //=====================================================
    /** books */
    public static void setBookLastLook(String bkey, String last_surl, Integer page) {
        if(page<0)
            db.updateSQL("UPDATE books SET last_surl=?, last_pidx=-1 WHERE bkey=? )", last_surl, bkey);
        else
            db.updateSQL("UPDATE books SET last_surl=?, last_pidx=? WHERE bkey=?", last_surl, page, bkey);
    }

    public static void setBookLastLook(String bkey, Integer page) {
        if(page<0)
            db.updateSQL("UPDATE books SET last_pidx=-1 WHERE bkey=? )", bkey);
        else
            db.updateSQL("UPDATE books SET last_pidx=? WHERE bkey=?", page, bkey);
    }

//    public static void setBookSectionCount(Integer bookBID, Integer num) {
//        db.updateSQL("UPDATE books SET section_count=? WHERE id=?", num, bookBID);
//    }

    public static int getBookLastLookSection(String bkey) {
        int temp = -1;
        DataReader dr = db.selectSQL("SELECT last_pidx FROM books WHERE bkey=?", bkey);
        if (dr.read()) {
            temp = dr.getInt("last_pidx");
        }
        dr.close();
        return temp;
    }

//    public static String getLastBookKey(String sectionUrl) {
//
//        String temp = null;
//        DataReader dr = db.selectSQL("SELECT bkey FROM books WHERE url=?" ,sectionUrl);
//        if (dr.read()) {
//            temp = dr.getString("bkey");
//        }
//        dr.close();
//
//        return temp;
//    }

//    public static void setLastBookPage(String bkey, String sectionUrl, Integer page, Integer book) {
//        db.updateSQL("UPDATE books SET last_page=?, last_pidx=?, last_surl=? WHERE bkey=?", page, book, sectionUrl, bkey);
//    }

    public static void setLastBookPage2(String bookurl, String sectionUrl, Integer page, Integer book) {
        db.updateSQL("UPDATE books SET last_page=?, last_pidx=?, last_surl=? WHERE url=?", page, book, sectionUrl, bookurl);
    }

    public static int getLastBookPage(String sectionUrl) {
        int temp = 0;

        DataReader dr = db.selectSQL("SELECT last_page FROM books WHERE last_surl=?" , sectionUrl);
        if (dr.read()) {
            temp = dr.getInt("last_page");
        }
        dr.close();

        return temp;
    }


    public static String getLastBookUrl(String bkey) {
        String temp = null;
        DataReader dr = db.selectSQL("SELECT last_surl FROM books WHERE bkey=?" , bkey);
        if (dr.read()) {
            temp = dr.getString("last_surl");
        }
        dr.close();

        return temp;
    }


    public static void setBookViewSet(String bkey, ViewSetModel set) {
        db.updateSQL("UPDATE books SET view_orientation=?,view_model=?,view_scale=?,view_direction=? WHERE bkey=?",
                set.view_orientation, set.view_model, set.view_scale,set.view_direction, bkey);
    }

    public static ViewSetModel getBookViewSet(String bkey) {
        ViewSetModel temp = new ViewSetModel();
        DataReader dr = db.selectSQL("SELECT view_orientation, view_model,view_scale,view_direction FROM books WHERE bkey=?", bkey);
        if (dr.read()) {
            temp.view_orientation = dr.getInt("view_orientation");
            temp.view_model       = dr.getInt("view_model");
            temp.view_scale       = dr.getInt("view_scale");
            temp.view_direction   = dr.getInt("view_direction");
        }
        dr.close();

        return temp;
    }



    //==================================
    /** likes */
    public static void delLike(String bkey) {
        db.updateSQL("DELETE FROM likes WHERE bkey=?", bkey);
    }

//    public static void addLike(BookViewModel sd) {
//        if (!db.existsSQL("SELECT * FROM likes WHERE bkey=?", sd.bookKey)) {
//
//            db.updateSQL("INSERT INTO likes(type,bkey,url,name,logo,author,source,logTime) VALUES(?,?,?,?,?,?,?,?);",
//                    sd.dtype(),sd.bookKey,sd.bookUrl,sd.name,sd.logo,sd.author,sd.source, new Date().getTime());
//        }
//        else {
//            db.updateSQL("UPDATE likes SET type=?,url=?,name=?,logo=?,author=?,source=? WHERE bkey=?;",
//                    sd.dtype(), sd.bookUrl, sd.name, sd.logo, sd.author, sd.source, sd.bookKey);
//        }
//    }


    //备份用
    public static void addLike(Book book) {
        if (!db.existsSQL("SELECT * FROM likes WHERE bkey=?", book.getBkey())) {
//            Log.d("addLike","添加："+book.getBkey());
            db.updateSQL("INSERT INTO likes(type,bkey,url,name,logo,author,source,number,logTime) VALUES(?,?,?,?,?,?,?,?,?);",
                    book.getType(), book.getBkey(), book.getUrl(), book.getName(), book.getLogo(),
                    book.getAuthor(), book.getSource(),book.getNumber(), new Date().getTime());
        }
        else {
//            Log.d("addLike","更新："+book.getBkey());
            db.updateSQL("UPDATE likes SET type=?,url=?,name=?,logo=?,author=?,source=?,number=? WHERE bkey=?;",
                    book.getType(), book.getUrl(), book.getName(), book.getLogo(), book.getAuthor(), book.getSource(), book.getNumber(), book.getBkey());
        }

        if (book.isref()) {
            if(!db.existsSQL("SELECT * FROM books WHERE bkey=?", book.getBkey())) {
//                Log.d("addBook","添加："+book.getBkey());
                db.updateSQL("INSERT INTO books(bkey, url, section_count, last_surl, last_pidx, last_page, source) VALUES(?,?,?,?,?,?,?);",
                        book.getBkey(), book.getUrl(), book.getSection_count(),
                        book.getLast_surl(), book.getLast_pidx(), book.getLast_page(), book.getSource());
            } else {
//                Log.d("addBook","更新："+book.getBkey());
                db.updateSQL("UPDATE books SET url=?, section_count=?, last_surl=?, last_pidx=?, last_page=?, source=? WHERE bkey=?;",
                        book.getUrl(), book.getSection_count(), book.getLast_surl(),
                        book.getLast_pidx(), book.getLast_page(), book.getSource(), book.getBkey());
            }
        }
    }

    public static void setNumber(String url, int number) {
        db.updateSQL("UPDATE likes SET number=? WHERE url=?", number, url);
    }

    public static List<Book> getLikeS() {
        List<Book> list = new ArrayList<>();
        DataReader dr = db.selectSQL("SELECT * FROM likes");

        while (dr.read()) {
            Book book = new Book();
            book.setName(dr.getString("name"));
            book.setLogo(dr.getString("logo"));
            book.setUrl(dr.getString("url"));
            book.setType(dr.getInt("type"));
            book.setSource(dr.getString("source"));
            book.setNumber(dr.getInt("number"));
            book.setNew(false);
            list.add(0, book);
        }
        return list;
    }

//    public static List<Book> getLike() {
//        List<Book> list = new ArrayList<>();
//
//        DataReader dr = db.selectSQL("SELECT * FROM likes");
//
//        while (dr.read()) {
//            Book book = new Book();
//            book.setName(dr.getString("name"));
//            book.setLogo(dr.getString("logo"));
//            book.setUrl(dr.getString("url"));
//            book.setType(dr.getInt("type"));
//            book.setSource(dr.getString("source"));
//            book.setAuthor(dr.getString("author"));
//            book.setBkey(dr.getString("bkey"));
//            book.setNumber(dr.getInt("number"));
//            book.setNew(false);
//            getLikeBooks(book);
//            list.add(0, book);
//        }
//        dr.close();//内部同时关闭游标和数据库
//
//        return list;
//    }
//
//    /* 从book中获得收藏漫画的相关参数 */
//    private static void getLikeBooks(Book book) {
//        DataReader dr = db.selectSQL("SELECT * FROM books WHERE bkey=?;", book.getBkey());
//
//        while (dr.read()) {
//            book.setSection_count(dr.getInt("section_count"));
//            book.setLast_surl(dr.getString("last_surl"));
//            book.setLast_pidx(dr.getInt("last_pidx"));
//            book.setLast_page(dr.getInt("last_page"));
//        }
//        dr.close();//内部同时关闭游标和数据库
//    }

    //==========================================
    /** history */
    public static void delAllHistory() {
        db.updateSQL("DELETE FROM historys");
    }

    public static void delHistory(String bkey) {
        db.updateSQL("DELETE FROM historys WHERE bkey=?", bkey);
    }


    public static void addHistory(BookViewModel sd) {
        if (db.existsSQL("SELECT * FROM historys WHERE bkey=?", sd.bookKey)) {
            db.updateSQL("DELETE FROM historys WHERE bkey=?", sd.bookKey);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.US);
        db.updateSQL("INSERT INTO historys(type,bkey,url,name,logo,author,source,logTime) VALUES(?,?,?,?,?,?,?,?);",
                sd.dtype(),sd.bookKey,sd.bookUrl,sd.name,sd.logo,sd.author,sd.source, sdf.format(new Date()) );
    }

    public static List<Book> getHistory() {
        List<Book> list2 = new ArrayList<>();

        DataReader dr = db.selectSQL("SELECT * FROM historys");

        while (dr.read()) {
            Book book = new Book();
            book.setName(dr.getString("name"));
            book.setLogo(dr.getString("logo"));
            book.setUrl(dr.getString("url"));
            book.setType(dr.getInt("type"));
            book.setSource(dr.getString("source"));
            list2.add(0, book);
        }
        dr.close();//内部同时关闭游标和数据库

        return list2;
    }


    //==========================================
    /** 设置 */
    public static int getTextSize() {
        if (!db.existsSQL("SELECT setData FROM setting WHERE setName=?", "TextSize")) {
            db.updateSQL("INSERT INTO setting(setName, setData) VALUES(?,?)", "TextSize", 20);
            return 20;
        }

        int TextSize = 20;
        DataReader dr = db.selectSQL("SELECT setData FROM setting WHERE setName=?", "TextSize");
        if (dr.read()) {
            TextSize = dr.getInt("setData");
        }
        dr.close();

        return TextSize;
    }

    public static void setTextSize(int i) {
        db.updateSQL("UPDATE setting SET setData=? WHERE setName=?", i, "TextSize");
    }


    public static int getCutPic() {
        if (!db.existsSQL("SELECT setData FROM setting WHERE setName=?", "CutPic")) {
            db.updateSQL("INSERT INTO setting(setName, setData) VALUES(?,?)", "CutPic", 0);
            return 0;
        }

        int CutPic = 0;
        DataReader dr = db.selectSQL("SELECT setData FROM setting WHERE setName=?", "CutPic");
        if (dr.read()) {
            CutPic = dr.getInt("setData");
        }
        dr.close();

        return CutPic;
    }

    public static void setCutPic(int i) {
        db.updateSQL("UPDATE setting SET setData=? WHERE setName=?", i, "CutPic");
    }
}
