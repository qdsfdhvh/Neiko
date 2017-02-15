package seiko.neiko.dao.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Seiko on 2016/12/9.
 * 数据库处理接口
 */

class DataReader {
    private Cursor cursor;
    private SQLiteDatabase db;

    DataReader(SQLiteDatabase db, Cursor cursor) {
        this.db = db;
        this.cursor = cursor;
    }

    void close() {
        cursor.close();
        db.close();
    }

    long getLong(String colName) {return cursor.getLong(cursor.getColumnIndex(colName));}

    public int getInt(String colName) {return cursor.getInt(cursor.getColumnIndex(colName));}

    public String getString(String colName) {return cursor.getString(cursor.getColumnIndex(colName));}

    boolean read() {return cursor.moveToNext();}
}
