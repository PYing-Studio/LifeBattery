package io.github.zhaomy6.lifebattery;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.content.ContentValues.TAG;

/**
 * Created by zhangsht on 2016/12/10.
 */

public class MyDB extends SQLiteOpenHelper {
    private static final String DB_Name = "PlanStores3";
    private static final String Table_Name = "Plans4";
    private static final int DB_Version = 1;

    public MyDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public MyDB(Context context) {
        super(context, DB_Name, null, DB_Version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String Create_Table = "CREATE TABLE if not exists "
                + Table_Name
                + " (_id INTEGER PRIMARY KEY, title TEXT, DDL TEXT, type TEXT, detail TEXT, finished TEXT)";
        db.execSQL(Create_Table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void insert2DB(String title, String DDL, String type, String detail, String finished) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("title", title);
        cv.put("DDL", DDL);
        cv.put("type", type);
        cv.put("detail", detail);
        cv.put("finished", finished);
        db.insert(Table_Name, null, cv);
        db.close();
    }

    public void updateDB(String title, String DDL, String type, String detail) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("title", title);
        cv.put("DDL", DDL);
        cv.put("type", type);
        cv.put("detail", detail);
        String whereClause = "title=?";
        String[] whereArgs = {title};
        db.update(Table_Name, cv, whereClause, whereArgs);
        db.close();
    }

    public void updateFinished(String title, String finished) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("title", title);
        cv.put("finished", finished);
        String whereClause = "title=?";
        String[] whereArgs = {title};
        db.update(Table_Name, cv, whereClause, whereArgs);
        db.close();
    }

    public void updateTimeout(String currentTime) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("finished", "超时");
        String whereClause = "type = ? AND DDL < ?";
        String[] whereArgs = {"false", currentTime};
        db.update(Table_Name, cv, whereClause, whereArgs);
        db.close();
    }

    public void deleteDB(String title) {
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = "title=?";
        String[] whereArgs = {title};
        db.delete(Table_Name, whereClause, whereArgs);
        db.close();
    }

    public boolean isExists(String title) {
        SQLiteDatabase db = getWritableDatabase();
        String[] columns = {"title"};
        String whereClause = "title=?";
        String[] whereArgs = {title};
        Cursor cursor = db.query(Table_Name, columns, whereClause, whereArgs, null, null, null);
        boolean ans = cursor.getCount() != 0;
        cursor.close();
        db.close();
        return ans;
    }

    public Cursor getWithTitle(String title) {
        SQLiteDatabase db = getWritableDatabase();
        String[] colums = {"_id", "title", "DDL", "type", "detail", "finished"};
        String whereClause = "title=?";
        String[] whereArgs = {title};
        Cursor cursor = db.query(Table_Name, colums, whereClause, whereArgs, null, null, null);
        if (cursor.getCount() > 0) {
            Log.i(TAG, "getWithTitle: count");
        }
        return cursor;
    }

    public Cursor queryDB(String title) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(Table_Name, new String[]{"title"}, null, null, null, null, null);
        db.close();
        return cursor;
    }

    public Cursor getLatestPlan() {
        SQLiteDatabase db = getWritableDatabase();
        String query_sql = "SELECT title, MIN(DDL) AS DDL FROM " + Table_Name + " WHERE type = 'false' AND finished = '未完成'";
        Cursor cursor = db.rawQuery(query_sql, null);
        return cursor;
    }

    public Cursor getAll() {
        SQLiteDatabase db = getWritableDatabase();
        String[] tableColumns = {"_id", "title", "DDL", "type", "detail", "finished"};
        return db.query(Table_Name, tableColumns,
                null, null, null, null, null);
    }

    // 获取表中部分列
    public Cursor getPart() {
        SQLiteDatabase db = getWritableDatabase();
        String query_sql = "SELECT _id, title, DDL FROM " + Table_Name + " WHERE type = 'false' AND finished = '未完成'";
        Cursor cursor = db.rawQuery(query_sql, null);
        return cursor;

    }

    // 根据关键词对任务的每一列进行匹配
    public Cursor queryWithKeyword(String keyword) {
        SQLiteDatabase db = getWritableDatabase();
        String query_sql = "SELECT * FROM " + Table_Name + " WHERE title LIKE '%" + keyword
                + "%' OR DDL LIKE '%" + keyword + "%' OR type LIKE '%" + keyword + "%' OR detail LIKE '%"
                + keyword + "%' OR finished LIKE '%" + keyword + "%'";
        Cursor cursor = db.rawQuery(query_sql, null);
        return cursor;
    }

    public Cursor sortWithTime() {
        SQLiteDatabase db = getWritableDatabase();
        String query_sql = "SELECT * FROM " + Table_Name + " WHERE type = 'false' AND finished = '未完成' ORDER BY DDL";
        Cursor cursor = db.rawQuery(query_sql, null);
        return cursor;
    }
}
