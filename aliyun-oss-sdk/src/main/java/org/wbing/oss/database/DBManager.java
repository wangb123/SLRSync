package org.wbing.oss.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.wbing.oss.UploadTask;


/**
 * @author 王冰
 * @date 2018/4/20
 */
public class DBManager {
    private DBHelper helper;
    private SQLiteDatabase db;

    public DBManager(Context context) {
        helper = new DBHelper(context);
        //因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0, mFactory);
        //所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
        db = helper.getWritableDatabase();
    }

    public void createOrUpdateTask(UploadTask task) {
        db.beginTransaction();
        try {
            Cursor cursor = db.rawQuery("select * from OSSFile where md5=?", new String[]{task.getId()});
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    if (cursor.moveToNext()) {
                        int status = cursor.getInt(cursor.getColumnIndex("status"));
                        String url = cursor.getString(cursor.getColumnIndex("url"));
                        long length = cursor.getLong(cursor.getColumnIndex("length"));
                        long total = cursor.getLong(cursor.getColumnIndex("total"));

                        task.setStatus(Math.max(0, status));
                        task.setUrl(url);
                        task.setLength(length);
                        task.setTotal(total);

                        Log.e("DBManager", task.toString());
                    }
                } else {
                    Log.e("DBManager", task.toString());
                    db.execSQL("INSERT INTO OSSFile VALUES(null, ?, ?, ?,?, ?, ?,?)",
                            new Object[]{task.getId(), task.getStatus(), task.getRes().toString(), task.getUrl(), task.getLength(), task.getTotal(), task.getExtra()});
                }
                cursor.close();
            }
            db.setTransactionSuccessful();  //设置事务成功完成
        } finally {
            db.endTransaction();    //结束事务
        }
    }

    public void update(String taskId, ContentValues cv) {
        db.update("OSSFile", cv, "md5 = ?",
                new String[]{taskId});
    }

    public void close() {
        db.close();
    }
}
