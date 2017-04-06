package seiko.neiko.dao.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Seiko on 2016/12/9.
 * 数据库处理工具
 */

public abstract class DbContext  extends SQLiteOpenHelper {

    public final String dbKey;

    public DbContext(Context context, String dbKey, int dbVer) {
        super(context, dbKey + ".db", null, dbVer);

        this.dbKey = dbKey;
    }

    //-------------------------
    public boolean existsSQL(String sql,String... args)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, args);

        DataReader dr = new DataReader(db,cursor);
        boolean isOk = dr.read();

        dr.close();

        return isOk;
    }

    public  boolean updateSQL(String sql,Object... args)
    {
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

    public  int insertSQL(String table, String sql,Object... args) {
        if (updateSQL(sql, args)) {

            DataReader dr = selectSQL("SELECT last_insert_rowid() id FROM " + table);
            if (dr.read()) {
                int temp = dr.getInt("id");
                dr.close();
                return temp;
            } else
                return -1;
        } else {
            return -1;
        }
    }


    public DataReader selectSQL(String sql,String... args) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, args);

        return new DataReader(db,cursor);
    }

    public void saveDatabase() {
        //保存数据库 //跳过
    }
}
