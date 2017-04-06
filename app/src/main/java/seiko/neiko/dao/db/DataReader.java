package seiko.neiko.dao.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Seiko on 2016/12/9.
 * 数据库处理接口
 */

public class DataReader {
    private Cursor cursor;
    private SQLiteDatabase db;

    public DataReader(SQLiteDatabase db, Cursor cursor)
    {
        this.cursor = cursor;
        this.db     = db;
    }

    public boolean read()
    {
        return cursor.moveToNext();
    }

    public long getLong(String colName)
    {
        return cursor.getLong(cursor.getColumnIndex(colName));
    }

    public int getInt(String colName)
    {
        return cursor.getInt(cursor.getColumnIndex(colName));
    }

    public String getString(String colName)
    {
        return cursor.getString(cursor.getColumnIndex(colName));
    }

    public void close()
    {
        cursor.close();
        db.close();
    }
}
