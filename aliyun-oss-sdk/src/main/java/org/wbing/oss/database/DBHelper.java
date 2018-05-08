package org.wbing.oss.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author 王冰
 * @date 2018/4/20
 */
public class DBHelper extends SQLiteOpenHelper {
    //数据库名称
    private static final String DATABASE_NAME = "oss.db";
    //数据库的版本号
    private static final int DATABASE_VERSION = 1;

    //表名
    private final String TABLE_NAME = "OSSFile";
    private final String COL_ID = "_id";
    private final String COL_MD5 = "md5";
    private final String COL_STATUS = "status";
    private final String COL_RES = "res";
    private final String COL_URL = "url";
    private final String COL_LENGTH = "length";
    private final String COL_TOTAL = "total";
    private final String COL_EXTRA = "extra";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table if not exists [" + TABLE_NAME + "] "
                + "(" + COL_ID + " integer primary key autoincrement, "
                + COL_MD5 + " CHAR (32) UNIQUE, "
                + COL_STATUS + " INTEGER, "
                + COL_RES + " TEXT, "
                + COL_URL + " TEXT, "
                + COL_LENGTH + " INTEGER, "
                + COL_TOTAL + " INTEGER, "
                + COL_EXTRA + " TEXT)";

        Log.i("info", "create table = " + sql);
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
