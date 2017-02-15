package seiko.neiko.dao.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Seiko on 2016/12/9.
 * 数据库处理工具
 */

abstract class DbContext  extends SQLiteOpenHelper {

//    private final String dbKey;

    DbContext(Context context, String dbKey, int dbVer) {
        super(context, dbKey + ".db", null, dbVer);
//        this.dbKey = dbKey;
    }

    //-------------------------
    boolean existsSQL(String sql,String... args)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, args);

        DataReader dr = new DataReader(db,cursor);
        boolean isOk = dr.read();

        cursor.close();
        dr.close();

        return isOk;
    }

    boolean updateSQL(String sql,Object... args) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            db.execSQL(sql, args);
            db.close();
            return true;
        }catch (Exception ex) {
            ex.printStackTrace();
            db.close();
            return false;
        }
    }

    public int getVersion(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.getVersion();
    }

    public void setVersion(int version){
        SQLiteDatabase db = this.getWritableDatabase();
        db.setVersion(version);
    }

//    public  int insertSQL(String table, String sql,Object... args) {
//        if (updateSQL(sql, args)) {
//
//            DataReader dr = selectSQL("SELECT last_insert_rowid() id FROM " + table);
//            if (dr.read()) {
//                int temp = dr.getInt("id");
//                dr.close();
//                return temp;
//            } else
//                return -1;
//        } else {
//            return -1;
//        }
//    }


    DataReader selectSQL(String sql,String... args) {
        SQLiteDatabase db = this.getReadableDatabase();
        return new DataReader(db,db.rawQuery(sql, args));
    }

//    public void saveDatabase() {
//        //保存数据库 //跳过
//    }
}
